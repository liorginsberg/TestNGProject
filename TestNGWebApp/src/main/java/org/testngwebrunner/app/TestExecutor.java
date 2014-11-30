package org.testngwebrunner.app;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.jetty.websocket.api.Session;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestExecutor implements IExecutionListener, ITestListener, LiveReporterListener {

	private Session session;
	private String name;

	public void runTests(Session session, String execJson) throws Exception {
		LiveReporter.setListener(this);
		this.session = session;

		JsonParser parser = new JsonParser();
		JsonObject execObb = (JsonObject)parser.parse(execJson);

		List<XmlSuite> suites = buildXmlSuite(execObb);
		
		String classPath = null;
		
		//get latest properties of project under test
		Properties prop = ProjectLoaderServlet.currentProperties;
	
		String suiteFolder = prop.getProperty("SUITES_DIR");
		String generatedFile = suiteFolder + File.separator + suites.get(0).getName() + ".xml";
		PrintWriter writer = new PrintWriter(generatedFile, "UTF-8");
		writer.println(suites.get(0).toXml());
		writer.close();
		
		// get the property value and print it out
		classPath = prop.getProperty("TEST_CLASSPATH");
		classPath = "D:\\TestNGProject\\TestNGWebApp\\target\\classes;" + classPath;
		
		testCommandLine(classPath, generatedFile);
		
	}
	
	private static List<XmlSuite> buildXmlSuite(JsonObject execObb) {
	
		List<XmlSuite> suitesList = new ArrayList<XmlSuite>();
		
		JsonArray rootArray = execObb.getAsJsonArray("children");
		for(JsonElement child: rootArray) {
			
			JsonObject suitChild = (JsonObject)child;
			XmlSuite xmlSuite = createXmlSuite(suitChild);
			
			suitesList.add(xmlSuite);
		}
		return suitesList;
	}
	
	private  static void testCommandLine(String classpath, String xmlFile) throws Exception {
	    Process p = Runtime.getRuntime().exec("cmd /c java -cp \""+ classpath  +"\"  org.testng.TestNG D:\\TestNGProject\\com.testng.tests\\src\\main\\resources\\suites\\ "+ xmlFile + " -listener org.testngwebrunner.app.SimpleTesListenerImpl;org.testngwebrunner.app.MyCodeTests");
	    inheritIO(p.getInputStream(), System.out);
	    inheritIO(p.getErrorStream(), System.err);

	}

	private static void inheritIO(final InputStream src, final PrintStream dest) {
	    new Thread(new Runnable() {
	        public void run() {
	            Scanner sc = new Scanner(src);
	            while (sc.hasNextLine()) {
	                dest.println(sc.nextLine());
	            }
	        }
	    }).start();
	}
	
	//in case typ test_method_node
	private static  XmlSuite createXmlSuite(JsonObject suiteJson) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setVerbose(2);
		String suiteName = suiteJson.get("text").getAsString();
		xmlSuite.setName(suiteName);
		List<XmlTest> tests = new ArrayList<XmlTest>();
		
		JsonArray rootArray = suiteJson.getAsJsonArray("children");
		for(JsonElement child: rootArray) {
			JsonObject testChild = (JsonObject)child;
			XmlTest xmlTest = createXmlTest(testChild);
			String xmlString = xmlTest.toXml(" ");// TODO Auto-generated method stub
			System.out.println(xmlString);
		
			xmlSuite.addTest(xmlTest);
		}
		
	
		return xmlSuite;
		
	}
	
	private static XmlTest createXmlTest(JsonObject testJson) {
		XmlTest xmlTest = new XmlTest();
		xmlTest.setVerbose(2);
		xmlTest.setPreserveOrder("true");
		JsonObject li_attr_json = testJson.getAsJsonObject("li_attr");
		String testName = li_attr_json.get("testName").getAsString();
		if(!testName.isEmpty()) {
			xmlTest.setName(testName);
		}
		testName = testJson.get("text").getAsString();
		xmlTest.setName(testName);
		
		String className = li_attr_json.get("className").getAsString();
		String methodName = li_attr_json.get("methodName").getAsString();
		JsonArray paramArray = li_attr_json.getAsJsonArray("params");
		XmlClass xmlClass = new XmlClass(className,false);
		List<XmlInclude> includes = new ArrayList<XmlInclude>();
		XmlInclude inc = new XmlInclude(methodName);
		for (JsonElement jsonElement : paramArray) {
			String paramString = jsonElement.getAsString();
			String[] paramPair = paramString.split(":");
			String param = paramPair[0];
			String value = paramPair[1];
			inc.addParameter(param, value);			
		}
		includes.add(inc);
		//inc.setDescription("my desc");
		xmlClass.setIncludedMethods(includes);
		List<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(xmlClass);
		xmlTest.setClasses(classes);
		
		return xmlTest;
	}

	@Override
	public void onExecutionStart() {
		pushMessage(name + ": Execution Start");
	}

	@Override
	public void onExecutionFinish() {
		pushMessage(name + ": Execution Finish");
	}

	@Override
	public void onTestStart(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " started");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " passing?");

	}

	@Override
	public void onTestFailure(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " fail");

	}

	@Override
	public void onTestSkipped(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " skipped");

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// not implemented
	}

	@Override
	public void onStart(ITestContext context) {
		ITestNGMethod[] m = context.getAllTestMethods();
		for (ITestNGMethod meth : m) {
			System.out.println(m.length);
		}
		pushMessage(name + ": " + context.getName() + " started");

	}

	@Override
	public void onFinish(ITestContext context) {
		pushMessage(name + ": " + context.getName() + " finished");

	}

	@Override
	public void onLogMessage(String message) {
		pushMessage(name + ": " + message);

	}

	private void pushMessage(String message) {
		try {
			session.getRemote().sendString(message);
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

}
