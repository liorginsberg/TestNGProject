package com.testng.tests.mock;

import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class CopyOfMockTests {
	
	
	@Test
	@Parameters(value={"first","second"})
	public void test1(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}
	@Test
	@Parameters(value={"first","second"})
	public void test2(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}
	@Test
	@Parameters(value={"first","second"})
	public void test3(String first, int second) {
		Reporter.log("MockTests.test1 runs...", true);
	}

}
