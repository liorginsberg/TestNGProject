package org.testngwebrunner.app;

import java.util.List;

import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

public class MyMethodSelector implements IMethodSelector{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean includeMethod(IMethodSelectorContext context, ITestNGMethod method, boolean isTestMethod) {
		System.out.println("MyMethodSelector.includeMethod " + method.getMethodName());
		return true;
	}

	@Override
	public void setTestMethods(List<ITestNGMethod> testMethods) {
		for(ITestNGMethod method: testMethods) {
			System.out.println("MyMethodSelector.setTestMethods " + method.getMethodName());		
		}
	}


}
