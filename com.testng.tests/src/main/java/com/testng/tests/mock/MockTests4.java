package com.testng.tests.mock;

import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MockTests4 {
	
	/**
	 * javadoc for test1 in MockTests4
	 * @param first
	 * @param second
	 */
	@Test
	@Parameters(value={"first","second"})
	public void test1(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}
}
