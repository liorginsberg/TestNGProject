package org.testngwebrunner.app;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExecutionListener implements ITestListener, IExecutionListener {


	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("{\"type\":\"testStart\", \"message\":\"" + result.getName() + "\"}");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("{\"type\":\"testSuccess\", \"message\":\"" + result.getName() + "\"}");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("{\"type\":\"testFail\", \"message\":\"" + result.getName() + "\"}");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("{\"type\":\"testSkip\", \"message\":\"" + result.getName() + "\"}");

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onStart(ITestContext context) {
		System.out.println("{\"type\":\"start\", \"message\":\"" + context.getName() + "\"}");

	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("{\"type\":\"finish\", \"message\":\"" + context.getName() + "\"}");

	}

	@Override
	public void onExecutionStart() {
		System.out.println("{\"type\":\"startExecution\", \"message\":\"startExecution\"}");

	}

	@Override
	public void onExecutionFinish() {
		System.out.println("{\"type\":\"finishExecution\", \"message\":\"finishExecution\"}");
	}

}
