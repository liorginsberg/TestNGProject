package org.testngwebrunner.app;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class SimpleTesListenerImpl implements ITestListener {

	
		




	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("#################### OK COMMAND LINE - EXECUTION START ##################");
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("#################### OK COMMAND LINE - EXECUTION FAILURE ##################");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		System.out.println("#################### OK COMMAND LINE - EXECUTION SECCESS ##################");
		
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("#################### OK COMMAND LINE - EXECUTION FAILURE ##################");
		// TODO Auto-generated method stub
		
	}

}
