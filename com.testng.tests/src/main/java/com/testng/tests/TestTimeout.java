package com.testng.tests;

import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestTimeout {
	@Test
	@Parameters({"sleep"})
	public void testTimeout(long sleep) {
		try {
			System.out.println("Sleeping for " + sleep +"ms ...");
			Thread.sleep(sleep);
			Reporter.log("hello : SLEEPING...)");
			Reporter.getCurrentTestResult().setAttribute("ok", "wow");
		} catch (Exception e) {}
	}
}
