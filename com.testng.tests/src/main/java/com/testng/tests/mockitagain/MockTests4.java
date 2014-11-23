package com.testng.tests.mockitagain;

import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MockTests4 {
	
	@Test
	@Parameters(value={"first","second"})
	public void test1(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}
}
