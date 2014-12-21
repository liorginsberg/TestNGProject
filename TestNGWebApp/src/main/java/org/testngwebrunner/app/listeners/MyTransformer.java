package org.testngwebrunner.app.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

public class MyTransformer implements IAnnotationTransformer2 {

	public MyTransformer() {
		System.out.println("created MyTransformer");		
	}

	private static boolean disableAll = false;

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		System.out.println("transform.... for: " + testMethod.getName() + "disabled all: " + disableAll);
		if (disableAll) {
			System.out.println("setEnable to false on: " + testMethod.getName());
			annotation.setEnabled(false);
		}

	}

	@Override
	public void transform(IConfigurationAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		if (disableAll) {
			System.out.println("setEnable to false on: " + testMethod.getName());
			annotation.setEnabled(false);
		}

	}

	@Override
	public void transform(IDataProviderAnnotation annotation, Method method) {
		// TODO - implement data provider true here... maby

	}

	@Override
	public void transform(IFactoryAnnotation annotation, Method method) {
		if (disableAll) {
			annotation.setEnabled(false);
		}
	}

	/**
	 * @return the disableAll
	 */
	public static boolean isDisableAll() {
		return disableAll;
	}

	/**
	 * @param disableAll the disableAll to set
	 */
	public static void setDisableAll(boolean disableAll) {
		System.out.println("setDisableAll() called!!!!!!!!!!!!!!!!!!");
		MyTransformer.disableAll = disableAll;
		
	}
	
	
}