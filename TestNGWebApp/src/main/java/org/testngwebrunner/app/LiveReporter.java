package org.testngwebrunner.app;

import java.util.LinkedList;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class LiveReporter implements ITestListener, IExecutionListener {

	private static LiveReporter reporter;

	private LinkedList<LiveReporterListener> liveReporterListeners = null;

	private LiveReporter() {
	}

	public static LiveReporter getInstance() {
		if (reporter == null) {
			reporter = new LiveReporter();
		}
		return reporter;

	}

	public void addListener(LiveReporterListener listener) {
		if (liveReporterListeners == null) {
			liveReporterListeners = new LinkedList<LiveReporterListener>();
		}
		if (!liveReporterListeners.contains(listener)) {
			liveReporterListeners.add(listener);
		}
	}

	public void removeListener(LiveReporterListener listener) {
		if (liveReporterListeners.contains(liveReporterListeners)) {
			liveReporterListeners.remove(liveReporterListeners);
		}
	}

	public void report(String type,String message) {
		
		for(LiveReporterListener listener: liveReporterListeners) {
			listener.onReport(type, message);
		}
	}
	
	@Override
	public void onTestStart(ITestResult result) {
		report("testStart", result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		report("testSuccess",result.getName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		report("testFail",result.getName());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		report("testSkip",result.getName());

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onStart(ITestContext context) {
		report("start",context.getName());

	}

	@Override
	public void onFinish(ITestContext context) {
		report("finish",context.getName());

	}

	@Override
	public void onExecutionStart() {
		report("startExecution","");

	}

	@Override
	public void onExecutionFinish() {
		report("finishExecution","");

	}
}
