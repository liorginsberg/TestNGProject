package com.testng.tests.mock.inner.copy;

import org.testng.Reporter;
import org.testng.annotations.Test;

@Test(testName="InnerMockTestsClass", description="inner mock test class", suiteName = "suite1")
public class InnerMockTests {
	/**
	 * javadoc for InnerMoclTestTest1
	 */
	@Test(testName="InnerMoclTestTest1", description="This is MockInner Desc")
	public void test2() {
		Reporter.log("InnerMockTests.test2 runs...", true);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test(testName="InnerMoclTestTest1", description="")
	public void test3() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Reporter.log("InnerMockTests.test3 runs...", true);
	}	
	@Test(testName="InnerMoclTestTestNewName", description="")
	public void testNewName() {
		Reporter.log("InnerMockTests.test3 runs...", true);
	}	
}
