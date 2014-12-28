package org.testngwebrunner.app.unused;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.internal.InvokeMethodRunnable.TestNGRuntimeException;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TestExecutorServlet extends HttpServlet {

	private String sourceFolder = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		if (br != null) {
			json = br.readLine();
		}

		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName("my suite");
		XmlTest xmlTest = new XmlTest(xmlSuite);
		xmlTest.setName("t1");
		XmlClass xmlClass = new XmlClass("com.test.TestClass", 0);
		List<XmlInclude> includes = new ArrayList<XmlInclude>();
		XmlInclude inc  = new XmlInclude("test2");
		inc.setDescription("my desc");
		inc.addParameter("p1", "v1");
		inc.addParameter("p2", "v2");
		includes.add(inc);
		xmlClass.setIncludedMethods(includes);
		xmlClass.setXmlTest(xmlTest);
		
		String xmlString = xmlSuite.toXml();
		
		out.print("got it");
	}

	public static void main(String[] args) {
		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName("my suite");
		xmlSuite.setVerbose(2);
		
		XmlTest xmlTest = new XmlTest();
		xmlTest.setName("t1");
		
		
		XmlClass xmlClass = new XmlClass("com.testng.tests.MyCodeTests",false);
		
		List<XmlInclude> includes = new ArrayList<XmlInclude>();
		XmlInclude inc  = new XmlInclude("testAdd");
		inc.setDescription("my desc");
		inc.addParameter("num1", "3");
		inc.addParameter("num2", "6");
		includes.add(inc);
		
		List<XmlClass> classes = new ArrayList<XmlClass>();
		xmlClass.setIncludedMethods(includes);
		classes.add(xmlClass);
		xmlTest.setClasses(classes);
		xmlSuite.addTest(xmlTest);
		
		
		String xmlString = xmlSuite.toXml();
		System.out.println(xmlString);
	}
}