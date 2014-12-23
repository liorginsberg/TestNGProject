package org.testngwebrunner.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExecutionListener implements ITestListener, IExecutionListener {


	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("{\"type\":\"testStart\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("{\"type\":\"testSuccess\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("{\"type\":\"testFail\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("{\"type\":\"testSkip\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onStart(ITestContext context) {
		Pattern p = Pattern.compile("([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}):([startContainer|endContainer|startSuite|endSuite]+)");
		Matcher m = p.matcher(context.getName());
		if(m.find()) {
			String containerEvent = m.group(2);
			String msg = m.group(1);
			System.out.println("{\"type\":\"" + containerEvent + "\", \"message\":\"" + getRealId(msg) + "\"}");
		} else {
			System.out.println("{\"type\":\"start\", \"message\":\"" + getRealId(context.getName()) + "\"}");			
		}

	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("{\"type\":\"finish\", \"message\":\"" + getRealId(context.getName()) + "\"}");

	}

	@Override
	public void onExecutionStart() {
		System.out.println("{\"type\":\"startExecution\", \"message\":\"startExecution\"}");

	}

	@Override
	public void onExecutionFinish() {
		System.out.println("{\"type\":\"finishExecution\", \"message\":\"finishExecution\"}");
	}
	
	private String getRealId(String testId) {
		String realId = null;
		try {
			File file = new File("id.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			realId = properties.getProperty(testId);							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(realId == null) {
			System.out.println("##################### not goood ################");
		}
		return realId;
		
	}

}
