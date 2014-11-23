package com.testng.tests.mockthird;

import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MockTests3 {
	
	@Test
	@Parameters(value={"first","second"})
	public void test1(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}
}
