package com.testng.tests;

import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestTimeout {
	@Test
	@Parameters({"sleep"})
	public void testTimeout(long sleep) {
		try {
			System.out.println("Sleeping for " + sleep +"ms ...");
			Thread.sleep(sleep);
		} catch (Exception e) {}
	}
}
