package com.testng.tests.listeners;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExecutionListener implements ITestListener, IExecutionListener {


	
	public void onTestStart(ITestResult result) {
		System.out.println("{\"type\":\"testStart\", \"message\":\"" + result.getTestContext().getName() + "\"}");
	}

	
	public void onTestSuccess(ITestResult result) {
		System.out.println("{\"type\":\"testSuccess\", \"message\":\"" + result.getTestContext().getName() + "\"}");
	}

	
	public void onTestFailure(ITestResult result) {
		System.out.println("{\"type\":\"testFail\", \"message\":\"" + result.getTestContext().getName() + "\"}");
	}

	
	public void onTestSkipped(ITestResult result) {
		System.out.println("{\"type\":\"testSkip\", \"message\":\"" + result.getTestContext().getName() + "\"}");

	}

	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	
	public void onStart(ITestContext context) {
		System.out.println("{\"type\":\"start\", \"message\":\"" + context.getName() + "\"}");

	}

	
	public void onFinish(ITestContext context) {
//		System.out.println("{\"type\":\"finish\", \"message\":\"" + context.getName() + "\"}");
//
	}

	
	public void onExecutionStart() {
		System.out.println("{\"type\":\"startExecution\", \"message\":\"startExecution\"}");

	}

	
	public void onExecutionFinish() {
		System.out.println("{\"type\":\"finishExecution\", \"message\":\"finishExecution\"}");
	}

}
