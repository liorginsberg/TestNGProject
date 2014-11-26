package org.testngwebrunner.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		}
		return javadoc;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		extractMethodJavadoc(new File("C:\\TestNGProject\\com.testng.tests\\src\\main\\java\\com\\testng\\tests\\mock\\CopyOfCopyOfMockTests.java"), "test3");
	}

}