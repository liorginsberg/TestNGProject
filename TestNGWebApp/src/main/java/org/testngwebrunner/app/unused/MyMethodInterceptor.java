package org.testngwebrunner.app.unused;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.text.StyleContext.SmallAttributeSet;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.xml.XmlTest;

public class MyMethodInterceptor implements IMethodInterceptor {

	public static boolean stopAll = false;
	public static boolean collect = false;

	public static List<IMethodInstance> collectedMethods = new ArrayList<IMethodInstance>();
	public static String currentContainer = null;
	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		 if (stopAll) {
			methods = new ArrayList<IMethodInstance>();
		} else if (collect) {
			collectedMethods.addAll(methods);
			methods = new ArrayList<IMethodInstance>();
		}
		return methods;
	}

	
	/*System.out.println("Intercept: " + context.getName());
	String name = context.getName();
	if (name.contains("startContainer")) {
		System.out.println("adding attr for current container");
		currentContainer = name.replace(":startContainer", "");
		collectedMethods.clear();
		collect = true;
	} else if (name.contains("endContainer")) {
		collect = false;
		if (currentContainer != null) {
			if (context.getName().replace(":endContainer", "").equals(currentContainer)) {
				System.out.println("returning collected methods@@@@@@@@@@");
				currentContainer = null;
				System.out.println("number of tests: " + collectedMethods.size());
				return collectedMethods;
			}
		}
	} else*/
}
