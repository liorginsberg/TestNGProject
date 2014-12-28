package com.testng.tests;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestTimeout {
	@Test
	@Parameters({ "sleep" })
	public void testTimeout(long sleep) {
		try {
			Reporter.getInstance().report("Sleeping for " + sleep + "ms ...");
			Thread.sleep(sleep);
			Reporter.getInstance().report("hello : SLEEPING...)");
		} catch (Exception e) {
			System.out.println("ok.... Some thing is wrong....");
		}
	}
}
