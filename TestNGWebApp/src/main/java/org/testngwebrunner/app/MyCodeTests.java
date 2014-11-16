package org.testngwebrunner.app;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MyCodeTests extends TestListenerAdapter {
   
	private MyCode myCode;
	private LiveReporter reporter = LiveReporter.getInstance();
	
	private int num1 = 6;
	private int num2 = 3;
	
	
	
	@BeforeClass
	public void beforeClass() {
		reporter.report("before class called ...");
		myCode = new MyCode();
		
	}

	@Test(testName = "test add")
	@Parameters({"num1","num2"})
	public void test1() throws InterruptedException {
		reporter.report("running testAdd("+ num1 +", " + num2 + ")");
		reporter.report("running int res = myCode.add(a, b);");
		int res = myCode.add(num1, num2);
		reporter.report("sleeping 5 seconds");
		Thread.sleep(5000);
		reporter.report("after running int res = myCode.add(a, b);");
		Assert.assertEquals(res, 9);
		reporter.report("after running Assert.assertEquals(res, 9);");	
	}
	
	@Test(testName = "test sub")
	@Parameters({"num1","num2"})
	public void test2() throws InterruptedException {
		reporter.report("running testAdd("+ num2 +", " + num2 + ")");
		reporter.report("running int res = myCode.add(a, b);");
		reporter.report("sleeping 3 seconds");
		Thread.sleep(3000);
		int res = myCode.add(1, num2);
		reporter.report("after running int res = myCode.add(a, b);");
		Assert.assertEquals(res, 9);
		reporter.report("after running Assert.assertEquals(res, 9);");	
	}
	
	@Test(testName = "test sub")
	@Parameters({"num1","num2"})
	public void test3() throws InterruptedException {
		reporter.report("running testAdd("+ num1 +", " + num2 + ")");
		reporter.report("running int res = myCode.add(a, b);");
		int res = myCode.add(num1, num2);
		reporter.report("sleeping 2 seconds");
		Thread.sleep(2000);
		reporter.report("after running int res = myCode.add(a, b);");
		Assert.assertEquals(res, 9);
		reporter.report("after running Assert.assertEquals(res, 9);");	
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
