package com.testng.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class TestContainer {

	@BeforeTest
	public void beforeTest() {
		System.out.println("in beforeTest of testContainer");
	}

	@AfterTest
	public void afterTest() {
		System.out.println("in afterTest of testContainer");
	}

	@BeforeClass
	public void beforeClass() {
		System.out.println("in beforeClass of testContainer");
	}

	@AfterClass
	public void afterClass() {
		System.out.println("in afterClass of testContainer");
	}

	@BeforeMethod
	public void beforeMethod() {
		System.out.println("in beforeMethod of testContainer");
	}

	@AfterMethod
	public void afterMethod() {
		System.out.println("in afterMethod of testContainer");
	}

	@BeforeSuite
	public void beforeSuite() {
		System.out.println("in beforeSuite of testContainer");
	}

	@AfterSuite
	public void afterSuite() {
		System.out.println("in afterSuite of testContainer");
	}
	
	@Test
	public void t1() {
		System.out.println("testing...");
	}
}
