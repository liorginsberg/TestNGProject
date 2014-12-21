package org.testngwebrunner.app;

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

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		if (stopAll) {
			methods = new ArrayList<IMethodInstance>();
		}
		return methods;
	}

}
