package org.testngwebrunner.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testngwebrunner.app.testcollector.ClassPathHack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ProjectLoaderServlet extends HttpServlet {

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
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		long timeStart = System.nanoTime();
		String classPath = null;
		String classesFolder = null;
		Properties prop = new Properties();
		InputStream input = null;
		String propFile = request.getParameter("projLoc");
		System.out.println(propFile);
		propFile = propFile.replace("\\\\", "\\");
		propFile = propFile.replaceFirst("\\\\", "");
		try {
			input = new FileInputStream(propFile);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			classPath = prop.getProperty("TEST_CLASSPATH");
			classesFolder = prop.getProperty("CLASSES_DIRECTORY");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String[] classPathElements = classPath.split(";");
		for (String path : classPathElements) {
			if (path.contains("6.8.8") || path.contains("infra")) {
				continue;
			}
			File f = new File(path);
			if (f.isFile()) {
				URL jarfile = new URL("jar", "", "file:" + f.getAbsolutePath() + "!/");
				ClassPathHack.addURL(jarfile);
			} else {
				ClassPathHack.addFile(f);
			}
		}

		JsonObject root = new JsonObject();
		root.addProperty("text", prop.getProperty("PROJECT_NAME"));
		root.addProperty("type", "root");
		JsonArray children = new JsonArray();
		for (File file : new File(classesFolder).listFiles()) {
			JsonObject obj = null;
			try {
				obj = getDirectoryAsJsonJsTreeFormat(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (obj.has("type")) {
				children.add(obj);
			}
		}
		root.add("children", children);
		// JsonObject root = getDirectoryAsJsonJsTreeFormat(new
		// File(classesFolder));
		System.out.println("create json took: " + (System.nanoTime() - timeStart) + " nano seconds");

		System.out.println(root);

		// return this json to the application
		out.print(root);
	}

	public JsonObject getDirectoryAsJsonJsTreeFormat(File mainFile) throws Exception {
		JsonObject node = new JsonObject();
		node.addProperty("text", removeFileExtention(mainFile));
		if (mainFile.isFile()) {
			if (isClassFile(mainFile)) {
				JsonArray testMethods = getTestMethodsJsonJSTreeFormat(mainFile);
				if (testMethods != null) {
					node.addProperty("type", "class_node");
					node.add("children", testMethods);
				}
			}
		} else {
			JsonArray children = new JsonArray();
			for (File file : mainFile.listFiles()) {
				JsonObject obj = getDirectoryAsJsonJsTreeFormat(file);
				if (obj.has("type")) {
					children.add(obj);
				}
			}
			if (children.size() > 0) {
				node.addProperty("type", "package_node");
				node.add("children", children);
			}
		}
		return node;
	}

	// TODO - DONE!! Move to the rest
	private JsonArray getTestMethodsJsonJSTreeFormat(File file) throws ClassNotFoundException {
		JsonArray testMethods = null;

		String classFullName = getClassName(file);
		Class c = Class.forName(classFullName);
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			m.setAccessible(true);
			Test testAnnotation = m.getAnnotation(Test.class);
			if (testAnnotation == null) {
				continue;
			}
			JsonObject methodObj = new JsonObject();
			JsonObject li_attr_obj = new JsonObject();
			li_attr_obj.addProperty("className", classFullName);
			li_attr_obj.addProperty("methodName", m.getName());
			li_attr_obj.addProperty("testName", testAnnotation.testName());
			methodObj.addProperty("type", "test_method_node");
			methodObj.addProperty("text", m.getName() + (!testAnnotation.testName().isEmpty() ? (" - " + testAnnotation.testName()) : ""));
			Parameters parametersAnnotation = m.getAnnotation(Parameters.class);
			if (parametersAnnotation != null) {
				String[] params = parametersAnnotation.value();
				if (m.getParameterTypes().length > params.length) {
					System.out.println("the method " + m.getName() + " takes more prameters than decleared in @Parameters");
					continue;
				} else if (m.getParameterTypes().length < params.length) {
					System.out.println("the method " + m.getName() + " takes less prameters than decleared in @Parameters");
					continue;
				}
				JsonArray paramsArray = new JsonArray();
				for (String param : params) {
					paramsArray.add(new JsonPrimitive(param));
				}

				li_attr_obj.add("params", paramsArray);
			}
			if (testMethods == null) {
				testMethods = new JsonArray();
			}
			methodObj.add("li_attr", li_attr_obj);
			testMethods.add(methodObj);
		}
		return testMethods;
	}

	private String getClassName(File file) {
		String filePath = file.getAbsolutePath();
		String classPath = filePath.split("classes\\\\")[1];
		String fileFullName = classPath.replace("\\", ".").replace(".class", "");
		return fileFullName;

	}

	private boolean isClassFile(File file) throws Exception {
		// TODO - make it better
		return file.getAbsolutePath().endsWith(".class");
	}

	private String removeFileExtention(File file) {
		String fname = file.getName();
		int pos = fname.lastIndexOf(".");
		if (pos > 0) {
			fname = fname.substring(0, pos);
		}
		return fname;
	}

}
