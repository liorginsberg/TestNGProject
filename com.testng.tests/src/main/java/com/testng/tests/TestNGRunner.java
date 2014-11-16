package com.testng.tests;

import org.testng.TestNG;

public class TestNGRunner {
	public static void main(String[] args) {
		TestNG testng = new TestNG();
		testng.setVerbose(2);
	
		testng.setTestClasses(new Class[] { MyCodeTests.class });
		testng.run();

		
	}
}
