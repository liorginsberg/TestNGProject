package com.testng.tests.mock;

import org.testng.Reporter;
import org.testng.annotations.Test;

public class MockTests {
	
	@Test
	public void test1() {
		Reporter.log("MockTests.test1 runs...", true);
	}
	@Test
	public void test2() {
		Reporter.log("MockTests.test2 runs...", true);
	}
	@Test
	public void test3() {
		Reporter.log("MockTests.test3 runs...", true);
	}	
}
