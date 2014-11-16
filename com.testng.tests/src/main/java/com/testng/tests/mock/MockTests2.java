package com.testng.tests.mock;

import org.testng.Reporter;
import org.testng.annotations.Test;

public class MockTests2 {
	
	@Test(description="this is moking test")
	public void test1() {
		Reporter.log("MockTests2.test1 runs...", true);
	}
	@Test
	public void test2() {
		Reporter.log("MockTests2.test2 runs...", true);
	}
	@Test
	public void test3() {
		Reporter.log("MockTests2.test3 runs...", true);
	}	
}
