package com.testng.tests;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.reporters.XMLStringBuffer;

import com.testng.infra.MyCode;

public class MyCodeTests extends TestListenerAdapter {

	private MyCode myCode;
	
	private int num1 = 6;
	private int num2 = 3;
	
	
	
	@BeforeClass
	public void beforeClass() {
		System.out.println("before class called ...");
		myCode = new MyCode();
		
	}

	@Test(testName = "test add")
	@Parameters({"num1","num2"})
	public void testAdd() {
		Reporter.log("running testAdd("+ num1 +", " + num2 + ")", 0);
		Reporter.log("running int res = myCode.add(a, b);", 1);
		int res = myCode.add(num1, num2);
		Reporter.log("after running int res = myCode.add(a, b);", 1);
		Assert.assertEquals(res, 9);
		Reporter.log("after running Assert.assertEquals(res, 9);", 0);	
	}

	@Override
	public void onStart(ITestContext testContext) {
		System.out.println("test context.getName() returns: " + testContext.getName());
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}
	
	
	
	
}
