package org.testngwebrunner.app;

import org.eclipse.jetty.websocket.api.Session;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestNG;

public class TestExecutor implements IExecutionListener, ITestListener, LiveReporterListener {

	private Session session;
	private String name;

	public void runTests(Session session, String name) {
		LiveReporter.setListener(this);
		this.session = session;
		this.name = name;
		TestNG testng = new TestNG();
		testng.setDefaultSuiteName("root");
		testng.setVerbose(2);
		testng.addExecutionListener(this);
		testng.addListener(this);
		testng.addListener(new IInvokedMethodListener() {

			@Override
			public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

			}

			@Override
			public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
				// TODO Auto-generated method stub

			}
		});

		testng.setTestClasses(new Class[] { MyCodeTests.class });
		testng.run();
		
	}

	@Override
	public void onExecutionStart() {
		pushMessage(name + ": Execution Start");
	}

	@Override
	public void onExecutionFinish() {
		pushMessage(name + ": Execution Finish");
	}

	@Override
	public void onTestStart(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " started");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " passing?");

	}

	@Override
	public void onTestFailure(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " fail");

	}

	@Override
	public void onTestSkipped(ITestResult result) {
		pushMessage(name + ": " + result.getName() + " skipped");

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// not implemented
	}

	@Override
	public void onStart(ITestContext context) {
		ITestNGMethod[] m = context.getAllTestMethods();
		for (ITestNGMethod meth : m) {
			System.out.println(m.length);
		}
		pushMessage(name + ": " + context.getName() + " started");

	}

	@Override
	public void onFinish(ITestContext context) {
		pushMessage(name + ": " + context.getName() + " finished");

	}

	@Override
	public void onLogMessage(String message) {
		pushMessage(name + ": " + message);

	}

	private void pushMessage(String message) {
		try {
			session.getRemote().sendString(message);
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

}
