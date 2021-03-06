package com.testng.tests;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.testng.infra.MyCode;

public class MyCodeTests {

	private MyCode myCode;

	private int num1 = 6;
	private int num2 = 3;

	@BeforeClass
	public void beforeClass() {
		Reporter.getInstance().report("before class called ...");
		myCode = new MyCode();

	}

	private  int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	@Test(testName = "test add")
	@Parameters({ "num1", "num2" })
	public void testAdd(int num1, int num2) throws InterruptedException {
		Thread.sleep(randInt(300, 700));
		Reporter.getInstance().report("num1=" + num1 + ", num2=" + num2);
		int res = myCode.add(num1, num2);
		Assert.assertEquals(res, 9);
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
