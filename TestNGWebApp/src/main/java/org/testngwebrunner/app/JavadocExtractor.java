package org.testngwebrunner.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

public class JavadocExtractor {
	public static String extractMethodJavadoc(File srcFile, String methodName) throws FileNotFoundException, IOException {
		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSource(srcFile);
		JavaClass[] cls = builder.getClasses();
		if (cls == null) {
			System.out.println("null cls");
		}
		JavaMethod[] methods = cls[0].getMethods(true);
		String javadoc = null;
		for (JavaMethod method : methods) {
			if (method.getName().equals(methodName)) {
				javadoc = method.getComment();
				System.out.println(javadoc);
				break;
			}
			System.out.println("annotations: " + method.getAnnotations());
			System.out.println("call singnature: "+ method.getCallSignature());
			System.out.println("code block: " + method.getCodeBlock());			
		}
		return javadoc;
	}
	
	public static Map<String, String> extractMethods(File srcFile) throws FileNotFoundException, IOException {
		
		Map<String, String> mapToReturn = new HashMap<String, String>();
		
		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSource(srcFile);
		JavaClass[] cls = builder.getClasses();
		if (cls == null) {
			System.out.println("null cls");
		}
		JavaMethod[] methods = cls[0].getMethods(true);
		for (JavaMethod method : methods) {
			mapToReturn.put(method.getName(), method.getComment());
				
		}
		return mapToReturn;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		extractMethodJavadoc(new File("C:\\TestNGProject\\com.testng.tests\\src\\main\\java\\com\\testng\\tests\\mock\\CopyOfCopyOfMockTests.java"), "test3");
	}

}