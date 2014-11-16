package com.testng.tests.mock.inner;

import org.testng.Reporter;
import org.testng.annotations.Test;

@Test(testName="InnerMockTestsClass", description="inner mock test class", suiteName = "suite1")
public class InnerMockTests {
	
	@Test(testName="InnerMoclTestTest1", description="")
	public void test1() {
		Reporter.log("InnerMockTests.test1 runs...", true);
	}
	@Test(testName="InnerMoclTestTest1", description="This is MockInner Desc")
	public void test2() {
		Reporter.log("InnerMockTests.test2 runs...", true);
	}
	@Test(testName="InnerMoclTestTest1", description="")
	public void test3() {
		Reporter.log("InnerMockTests.test3 runs...", true);
	}	
}
