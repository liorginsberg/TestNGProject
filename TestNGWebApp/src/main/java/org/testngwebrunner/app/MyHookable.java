package org.testngwebrunner.app;

import javax.security.auth.Subject;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

public class MyHookable implements IHookable{
	
	@Override
	public void run(IHookCallBack callBack, ITestResult testResult) {
		System.out.println(testResult.getTestContext().getName() + " gonna skip????");
		testResult.setStatus(ITestResult.SKIP);
		callBack.runTestMethod(testResult);
		
	}
	
}
