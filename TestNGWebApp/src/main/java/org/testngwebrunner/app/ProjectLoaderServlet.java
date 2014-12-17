package org.testngwebrunner.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
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
import org.testngwebrunner.app.testcollector.ClassPathHack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ProjectLoaderServlet extends HttpServlet {

	private String sourceFolder = null;
	public static Properties currentProperties = null;
	
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
		String suiteFolder = null;
		
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
			currentProperties = prop;

			// get the property value and print it out
			classPath = prop.getProperty("TEST_CLASSPATH");
			classesFolder = prop.getProperty("CLASSES_DIRECTORY");
			sourceFolder = prop.getProperty("SOURCE_DIR");
			suiteFolder = prop.getProperty("SUITES_DIR");
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
		JsonObject suitesObj = null;
		try {
			 handledSuitesFiles.clear();
			 suitesObj = getSuitesDirectoryAsJsonJsTreeFormat(new File(suiteFolder));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(suitesObj != null) {
			children.add(suitesObj);
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
				List<JsonElement> chiledList = jsonArrayToList(children);
				
				Collections.sort(chiledList, new Comparator<JsonElement>() {
					@Override
					public int compare(JsonElement a, JsonElement b) {
						JsonObject joa = (JsonObject)a;
						JsonObject job = (JsonObject)b;
						
						String aType = joa.get("type").getAsString();
						String bType = job.get("type").getAsString();
						
						

						if (aType.equals(bType)) {
							String aText = joa.get("text").getAsString();
							String bText = job.get("text").getAsString();
							return aText.compareTo(bText);
						}
						if(aType.equals("package_node")) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				children = listToJsonArray(chiledList);
				node.add("children", children);
			}
		}

		return node;
	}
	
	private static List<String> handledSuitesFiles = new ArrayList<String>();
	
	
	public JsonObject getSuitesDirectoryAsJsonJsTreeFormat(File mainFile) throws Exception {
		
		JsonObject node = null;
		String fileName = removeFileExtention(mainFile);
	
		if (mainFile.isFile()) {
			if (isSuiteFile(mainFile)) {
				if(handledSuitesFiles.contains(fileName)) {
					return null;
				}
				
				JsonObject data = new JsonObject();
				data.addProperty("full_path", mainFile.getAbsolutePath());
				
				JsonParser parser = new JsonParser();
	            JsonElement jsonElement = parser.parse(new FileReader(mainFile));
	            node = (JsonObject)jsonElement;	         
				node.add("data", data);
				handledSuitesFiles.add(removeFileExtention(mainFile));
			}
		} else {
			node = new JsonObject();
			node.addProperty("text", fileName);
			node.addProperty("type", "suite_folder_node");
			JsonArray children = new JsonArray();
			for (File file : mainFile.listFiles()) {
				JsonObject obj = getSuitesDirectoryAsJsonJsTreeFormat(file);
				if (obj != null) {
					children.add(obj);
				}
			}
			if (children.size() > 0) {
				
				List<JsonElement> chiledList = jsonArrayToList(children);
				
				Collections.sort(chiledList, new Comparator<JsonElement>() {
					@Override
					public int compare(JsonElement a, JsonElement b) {
						
						JsonObject joa = (JsonObject)a;
						JsonObject job = (JsonObject)b;
						
						String aType = joa.get("type").getAsString();
						String bType = job.get("type").getAsString();
						
						

						if (aType.equals(bType)) {
							String aText = joa.get("text").getAsString();
							String bText = job.get("text").getAsString();
							return aText.compareTo(bText);
						}
						if(aType.equals("suite_folder_node")) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				children = listToJsonArray(chiledList);
				node.add("children", children);
			}
		}

		return node;
	}

	private JsonArray listToJsonArray(List<JsonElement> list) {
		JsonArray jsonArray = new JsonArray();
		for (JsonElement element : list) {
			jsonArray.add(element);
		}
		return jsonArray;
	}

	private List<JsonElement> jsonArrayToList(JsonArray jsonArray) {
		List<JsonElement> list = new ArrayList<JsonElement>();
		Iterator<JsonElement> it = jsonArray.iterator();
		while (it.hasNext()) {
			list.add((JsonElement) it.next());
		}
		return list;
	}

	// TODO - DONE!! Move to the rest
	private JsonArray getTestMethodsJsonJSTreeFormat(File file) throws ClassNotFoundException, FileNotFoundException, IOException {
		
		
		
		JsonArray testMethods = null;

		String classFullName = getClassName(file);
		String classSource = getClassSourceFile(file);
		File classSourceFile = new File(classSource);
		Map<String, String> javadocMethodsMap = JavadocExtractor.extractMethods(classSourceFile);
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
			li_attr_obj.addProperty("testJavadoc", javadocMethodsMap.get(m.getName()));
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
	
	private String getClassSourceFile(File file) {
		String filePath = file.getAbsolutePath();
		String classPath = filePath.split("classes\\\\")[1];
		String javaFile = sourceFolder + File.separator + classPath.replace(".class", ".java");
		return javaFile;
		
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
	private boolean isSuiteFile(File file) throws Exception {
		// TODO - make it better
		return file.getAbsolutePath().endsWith(".json");
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
