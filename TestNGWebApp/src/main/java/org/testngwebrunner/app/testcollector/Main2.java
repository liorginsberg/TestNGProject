package org.testngwebrunner.app.testcollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Main2 {


	public static void main(String[] args) throws Exception {
		long timeStart = System.nanoTime();
		String classPath = null;
		String classesFolder = null;
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream("D:\\testNGwork\\com.testng.tests\\my.properties");
	 
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
		for(String path: classPathElements) {
			if(path.contains("6.8.8") || path.contains("infra")) {
				continue;
			}
			File f = new File(path);
			if(f.isFile()) {
				URL jarfile = new URL("jar", "", "file:" + f.getAbsolutePath() + "!/");
				ClassPathHack.addURL(jarfile);
			} else {
				ClassPathHack.addFile(f);
			}
		}
//		File classesFolder = new File("D:\\testNGwork\\com.testng.tests\\target\\classes\\");
//
//		// File jcommanderJar = new
//		// File("D:\\testNGwork\\com.testng.tests\\libs\\jcommander-1.27.jar");
//
//		// URL jcommanderJarFile = new URL("jar", "", "file:" +
//		// jcommanderJar.getAbsolutePath() + "!/");
//		File testngJar = new File("D:\\testNGwork\\com.testng.tests\\libs\\testng-6.8.8.jar");
//		URL jarfile = new URL("jar", "", "file:" + testngJar.getAbsolutePath() + "!/");
//		// ClassPathHack.addURL(jcommanderJarFile);
//		ClassPathHack.addURL(jarfile);
//		ClassPathHack.addFile(classesFolder);
		JsonObject root = getDirectoryAsJson(new File(classesFolder));
		System.out.println("create json took: " + (System.nanoTime() - timeStart) + " nano seconds");

		System.out.println(root);
		// // Class c2 = Class.forName("org.testng.TestNG");
		// Class c = Class.forName("com.testng.tests.Test1");
		// Method[] methods = c.getDeclaredMethods();
		// for (Method m : methods) {
		// m.setAccessible(true);
		// System.out.println(m.getName());
		// Annotation[] annotations = m.getAnnotations();
		// for (Annotation a : annotations) {
		// if
		// (a.annotationType().getName().equals("org.testng.annotations.Parameters"))
		// {
		// String[] params = ((Parameters) a).value();
		// System.out.print(a.annotationType().getSimpleName());
		// System.out.print("({");
		// for (String p : params) {
		// System.out.print("\"" + p + "\", ");
		// }
		// System.out.println("})");
		// }
		// }
		// }
		// printTree(null);
	}

	public static JsonObject getDirectoryAsJson(File mainFile) throws Exception {
		JsonObject node = new JsonObject();
		node.addProperty("name", removeFileExtention(mainFile));
		if (mainFile.isFile()) {
			node.addProperty("type", "file");
			if (isClassFile(mainFile)) {
				node.addProperty("file_type", "class");
				JsonArray testMethods = getTestMethods(mainFile);
				if (testMethods == null) {
					node.addProperty("file_type", "non_class");
				} else {
					node.add("test_methods", testMethods);
				}
			} else {
				node.addProperty("file_type", "non_class");
			}
		} else {
			node.addProperty("type", "directory");
			JsonArray children = new JsonArray();
			for (File file : mainFile.listFiles()) {
				JsonObject obj = getDirectoryAsJson(file);
				if (obj.has("file_type") && !("class".equals(obj.get("file_type").getAsString()))) {
					continue;
				}
				children.add(obj);
			}
			node.add("children", children);
		}
		return node;
	}

	private static JsonArray getTestMethods(File file) throws ClassNotFoundException {
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
			methodObj.addProperty("className", classFullName);
			methodObj.addProperty("method_name", m.getName());
			Parameters parametersAnnotation = m.getAnnotation(Parameters.class);
			if (parametersAnnotation != null) {
				String[] params = parametersAnnotation.value();
				if(m.getParameterTypes().length > params.length) {
					System.out.println("the method " + m.getName() + " takes more prameters than decleared in @Parameters");
				} else if(m.getParameterTypes().length < params.length) {
					System.out.println("the method " + m.getName() + " takes less prameters than decleared in @Parameters");
				}
				JsonArray paramsArray = new JsonArray();
				for (String param : params) {
					paramsArray.add(new JsonPrimitive(param));
				}
				methodObj.add("params", paramsArray);
			}
			if (testMethods == null) {
				testMethods = new JsonArray();
			}
			testMethods.add(methodObj);
		}
		return testMethods;
	}

	private static String getClassName(File file) {
		String filePath = file.getAbsolutePath();
		String classPath = filePath.split("classes\\\\")[1];
		String fileFullName = classPath.replace("\\", ".").replace(".class", "");
		return fileFullName;

	}

	private static boolean isClassFile(File file) throws Exception {
		// TODO - make it better
		return file.getAbsolutePath().endsWith(".class");
	}

	private static String removeFileExtention(File file) {
		String fname = file.getName();
		int pos = fname.lastIndexOf(".");
		if (pos > 0) {
			fname = fname.substring(0, pos);
		}
		return fname;
	}

}
