package org.testngwebrunner.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import org.eclipse.jetty.websocket.api.Session;
import org.testng.reporters.XMLStringBuffer;
import org.testng.xml.Parser;
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
	private Process p;
    private Properties prop;
    private OutputStream output = null;
    
	public void runTests(Session session, String execJson) throws Exception {
		
		liveReporter = LiveReporter.getInstance();
		liveReporter.addListener(this);

		this.session = session;
		System.out.println(execJson);
		JsonParser parser = new JsonParser();
		JsonObject suiteJson = (JsonObject) parser.parse(execJson);

		// NEW IMPLEMENTATION - WRITE XML YOUR SELF
		XMLStringBuffer suiteBuffer = new XMLStringBuffer();
		suiteBuffer.setDocType("suite SYSTEM \"" + Parser.TESTNG_DTD_URL + '\"');
		
		String suiteId = suiteJson.get("id").getAsString();
		String suiteName = suiteJson.get("text").getAsString();
		
		Properties attr = new Properties();
		attr.setProperty("name", suiteId);
		suiteBuffer.push("suite", attr);
		
		prop = new Properties();
		
		output = new FileOutputStream("id.properties");
		
		for (JsonElement testJson : suiteJson.getAsJsonArray("children")) {
			JsonObject testJsonObj = (JsonObject)testJson;
			handleTestJson(suiteBuffer, testJsonObj, false);
		}
		
		prop.store(output, null);
		output.close();
		
		suiteBuffer.pop("suite");
		
		String xmlToWrite = suiteBuffer.toXML();
	
		String classPath = null;

		// get latest properties of project under test
		Properties prop = ProjectLoaderServlet.currentProperties;

		String suiteFolder = prop.getProperty("SUITES_DIR");
		String generatedFile = suiteFolder + File.separator + suiteName + ".xml"; //new imple
		PrintWriter writer = new PrintWriter(generatedFile, "UTF-8");
		writer.println(xmlToWrite);
		writer.close();

		String jsonGeneratedFile = suiteFolder + File.separator + suiteName + ".json";
		writer = new PrintWriter(jsonGeneratedFile, "UTF-8");
		writer.println(suiteJson.toString());
		writer.close();
		
		// get the property value and print it out
		classPath = prop.getProperty("TEST_CLASSPATH");
		classPath = System.getProperty("java.class.path") + classPath;

		testCommandLine(classPath, generatedFile);

	}
	

	private void handleTestJson(XMLStringBuffer suiteBuffer, JsonObject testJson, boolean generateNewId) throws Exception {

		if (testJson.get("type").getAsString().equals("test_method_node")) {
			JsonObject li_attr_json = testJson.getAsJsonObject("li_attr");
			String testName = li_attr_json.get("testName").getAsString();
			String timeout = li_attr_json.get("timeout").getAsString();
			String testId = null;
			String realId = null;
			if(generateNewId) {
				testId = UUID.randomUUID().toString(); 
				realId = testJson.get("id").getAsString();
			} else {				
				realId = testId = testJson.get("id").getAsString();
			}
			prop.setProperty(testId, realId);
			
			boolean checked = false;
			if(testJson.get("state").getAsJsonObject().has("checked")) {
				checked = testJson.get("state").getAsJsonObject().get("checked").getAsBoolean();
			}
			
			Properties attr = new Properties();
			attr.setProperty("time-out", timeout);
			attr.setProperty("enabled", String.valueOf(checked));
			attr.setProperty("name", testId);
			suiteBuffer.push("test", attr);
			
			suiteBuffer.push("classes");
			String className = li_attr_json.get("className").getAsString();
			attr = new Properties();
			attr.setProperty("name", className);
			suiteBuffer.push("class", attr);
			suiteBuffer.push("methods");
			
			String methodName = li_attr_json.get("methodName").getAsString();
			attr = new Properties();
			attr.setProperty("name", methodName);
			suiteBuffer.push("include", attr);
			
			JsonArray paramArray = li_attr_json.getAsJsonArray("params");
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
					attr = new Properties();
					attr.setProperty("name", param);
					attr.setProperty("value", value);
					suiteBuffer.addEmptyElement("parameter", attr);
				}
			}
			
			suiteBuffer.pop("include");
			suiteBuffer.pop("methods");
			suiteBuffer.pop("class");
			suiteBuffer.pop("classes");
			suiteBuffer.pop("test");
			
		} else if (testJson.get("type").getAsString().equals("test_node") ||
				testJson.get("type").getAsString().equals("suite_node")) {
			String type = testJson.get("type").getAsString();
			Properties attr = new Properties();
			boolean checked = false;
			if(testJson.get("state").getAsJsonObject().has("checked")) {
				checked = testJson.get("state").getAsJsonObject().get("checked").getAsBoolean();
			}
			attr.setProperty("enabled", String.valueOf(checked));
			String testId = null;
			String realId = null;
			if(generateNewId) {
				testId = UUID.randomUUID().toString(); 
				realId = testJson.get("id").getAsString();
			} else {				
				realId = testId = testJson.get("id").getAsString();
			}
			prop.setProperty(testId, realId);
			
			attr.setProperty("name", testId + (type.equals("test_node") ? ":startContainer": ":startSuite"));
			suiteBuffer.addEmptyElement("test", attr);
			int count = 1;
			if(testJson.has("data")) {
				JsonObject dataJson = testJson.getAsJsonObject("data");
				if(dataJson.has("ddt")) {
					JsonObject ddtJson = dataJson.getAsJsonObject("ddt");
					count = ddtJson.get("count").getAsInt();
				}
			}
			
			JsonArray childrenTests = testJson.getAsJsonArray("children");
			if(childrenTests != null) {
				for(int i = 0; i< count; i++) {				
					for(JsonElement testElement: childrenTests) {
						JsonObject testJsonChild = (JsonObject)testElement;
						handleTestJson(suiteBuffer, testJsonChild, true);
					}
				}
			}	
			attr.setProperty("name", testId + (type.equals("test_node") ? ":endContainer": ":endSuite"));
			suiteBuffer.addEmptyElement("test", attr);
		} else {
			throw new Exception("Should not get here, type: " + testJson.get("type").getAsString());
		}

	}


	private void testCommandLine(String classpath, String xmlFile) throws Exception {
		p = Runtime.getRuntime().exec(
				"cmd /c java -cp \"" + classpath + "\"  org.testng.TestNG " + xmlFile
						+ " -listener org.testngwebrunner.app.ExecutionListener,org.testngwebrunner.app.TestNGListenerSocket,org.testngwebrunner.app.MyMethodInterceptor -usedefaultlisteners false");
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

	@Override
	public void onReport(String report) {

		JsonObject obj = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		Date now = new Date();
		try {
			obj = (JsonObject) new JsonParser().parse(report);

		} catch (Exception e) {
			obj = new JsonObject();
			obj.addProperty("type", "not_set");
			obj.addProperty("message", report);
		}
		obj.addProperty("date", sdf.format(now));

		try {
			session.getRemote().sendString(obj.toString());
		} catch (Exception e) {
			System.out.println(e.getCause());
		}

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

	// in case type test_method_node
	private XmlSuite createXmlSuite(JsonObject suiteJson) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setVerbose(-1);
		String suiteName = suiteJson.get("text").getAsString();
		xmlSuite.setName(suiteName);
		
		JsonArray rootArray = suiteJson.getAsJsonArray("children");
		// Map<String, Integer> testCounter = new HashMap<String, Integer>();
		for (JsonElement child : rootArray) {
			JsonObject testChild = (JsonObject) child;
			XmlTest xmlTest = createXmlTest(testChild);
			// String testName = xmlTest.getName();
			// if (testCounter.containsKey(testName)) {
			// int count = testCounter.get(testName);
			// xmlTest.setName(testName + "(" + ++count + ")");
			// testCounter.put(testName, count);
			// } else {
			// testCounter.put(xmlTest.getName(), 0);
			// }
			
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
		// TODO HAndle
		// boolean checked =
		// testJson.getAsJsonObject("state").get("checked").getAsBoolean();
		String testName = li_attr_json.get("testName").getAsString();
		String id = testJson.get("id").getAsString();
		// if (!testName.isEmpty()) {
		// xmlTest.setName(testName);
		// }
		// testName = testJson.get("text").getAsString();
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
}
