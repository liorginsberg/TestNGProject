package org.testngwebrunner.app;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.jetty.websocket.api.Session;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestExecutor implements LiveReporterListener {

	private Session session;
	private LiveReporter liveReporter;
	
	public void runTests(Session session, String execJson) throws Exception {
		liveReporter = LiveReporter.getInstance();
		liveReporter.addListener(this);

		this.session = session;

		JsonParser parser = new JsonParser();
		JsonObject execObb = (JsonObject) parser.parse(execJson);

		List<XmlSuite> suites = null;
		try {
			suites = buildXmlSuite(execObb);
		} catch (Exception e) {
			session.close();
			return;
		}

		String classPath = null;

		// get latest properties of project under test
		Properties prop = ProjectLoaderServlet.currentProperties;

		String suiteFolder = prop.getProperty("SUITES_DIR");
		String generatedFile = suiteFolder + File.separator + suites.get(0).getName() + ".xml";
		PrintWriter writer = new PrintWriter(generatedFile, "UTF-8");
		writer.println(suites.get(0).toXml());
		writer.close();

		// get the property value and print it out
		classPath = prop.getProperty("TEST_CLASSPATH");
		classPath = System.getProperty("java.class.path") + classPath;

		testCommandLine(classPath, generatedFile);

	}

	private List<XmlSuite> buildXmlSuite(JsonObject execObb) {

		List<XmlSuite> suitesList = new ArrayList<XmlSuite>();

		JsonArray rootArray = execObb.getAsJsonArray("children");
		for (JsonElement child : rootArray) {

			JsonObject suitChild = (JsonObject) child;
			XmlSuite xmlSuite = createXmlSuite(suitChild);

			suitesList.add(xmlSuite);
		}
		return suitesList;
	}

	private void testCommandLine(String classpath, String xmlFile) throws Exception {
		Process p = Runtime.getRuntime().exec(
				"cmd /c java -cp \"" + classpath + "\"  org.testng.TestNG " + xmlFile
						+ " -listener org.testngwebrunner.app.ExecutionListener -usedefaultlisteners false");
		inheritIO(p.getInputStream(), System.out);
		inheritIO(p.getErrorStream(), System.err);

	}

	private void inheritIO(final InputStream src, final PrintStream dest) {
		new Thread(new Runnable() {
			public void run() {
				LiveReporter liveReporter = LiveReporter.getInstance();
				Scanner sc = new Scanner(src);
				while (sc.hasNextLine()) {
					String output = sc.nextLine();
					dest.println(output);
					liveReporter.report(output);
				}
			}
		}).start();
	}

	// in case type test_method_node
	private XmlSuite createXmlSuite(JsonObject suiteJson) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setVerbose(-1);
		String suiteName = suiteJson.get("text").getAsString();
		xmlSuite.setName(suiteName);

		JsonArray rootArray = suiteJson.getAsJsonArray("children");
		//Map<String, Integer> testCounter = new HashMap<String, Integer>();
		for (JsonElement child : rootArray) {
			JsonObject testChild = (JsonObject) child;
			XmlTest xmlTest = createXmlTest(testChild);
//			String testName = xmlTest.getName();
//			if (testCounter.containsKey(testName)) {
//				int count = testCounter.get(testName);
//				xmlTest.setName(testName + "(" + ++count + ")");
//				testCounter.put(testName, count);
//			} else {
//				testCounter.put(xmlTest.getName(), 0);
//			}

			xmlSuite.addTest(xmlTest);
		}
		String xmlString = xmlSuite.toXml();// TODO Auto-generated method
		System.out.println(xmlString);
		return xmlSuite;

	}

	private XmlTest createXmlTest(JsonObject testJson) {
		XmlTest xmlTest = new XmlTest();
		xmlTest.setVerbose(2);
		xmlTest.setPreserveOrder("true");
		JsonObject li_attr_json = testJson.getAsJsonObject("li_attr");
		String testName = li_attr_json.get("testName").getAsString();
		String id = testJson.get("id").getAsString();
//		if (!testName.isEmpty()) {
//			xmlTest.setName(testName);
//		}
//		testName = testJson.get("text").getAsString();
		xmlTest.setName(id);

		String className = li_attr_json.get("className").getAsString();
		String methodName = li_attr_json.get("methodName").getAsString();
		JsonArray paramArray = li_attr_json.getAsJsonArray("params");
		XmlClass xmlClass = new XmlClass(className, false);
		List<XmlInclude> includes = new ArrayList<XmlInclude>();
		XmlInclude inc = new XmlInclude(methodName);
		if (paramArray != null) {
			for (JsonElement jsonElement : paramArray) {

				String paramString = jsonElement.getAsString();
				String[] paramPair = paramString.split(":");
				String param = paramPair[0];
				String value = null;
				try {
					value = paramPair[1];
				} catch (Exception e) {
					liveReporter.report("{\"type\":\"missingParamValue\",\"message\":\"param missing\"}");
				}
				inc.addParameter(param, value);
			}
		}
		includes.add(inc);
		// inc.setDescription("my desc");
		xmlClass.setIncludedMethods(includes);
		List<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(xmlClass);
		xmlTest.setClasses(classes);

		return xmlTest;
	}

	@Override
	public void onReport(String report) {

		JsonObject obj = null;
		try {
			obj = (JsonObject) new JsonParser().parse(report);
		} catch (Exception e) {
			System.out.println("Could not parse message");
		}
		
		try {
			session.getRemote().sendString(obj.toString());
		} catch (Exception e) {
			System.out.println(e.getCause());
		}

	}

}
