//package org.testngwebrunner.app;
//
//import org.eclipse.jetty.websocket.api.Session;
//import org.testng.IExecutionListener;
//import org.testng.ITestContext;
//import org.testng.ITestListener;
//import org.testng.ITestResult;
//
//public class SimpleTesListenerImpl implements ITestListener, IExecutionListener, LiveReporterListener {
//
//	private static Session session = null;
//	
//	@Override
//	public void onTestStart(ITestResult result) {
//		System.out.println("#################### TEST START: " + result.getTestName() + " ##################");
//	}
//
//	@Override
//	public void onTestSuccess(ITestResult result) {
//		System.out.println("#################### TEST SUCCESS: " + result.getTestName() + " ##################");
//	}
//
//	@Override
//	public void onTestFailure(ITestResult result) {
//		System.out.println("#################### TEST FAIL: " + result.getTestName() + " ##################");
//	}
//
//	@Override
//	public void onTestSkipped(ITestResult result) {
//		System.out.println("#################### TEST SKIP: " + result.getTestName() + " ##################");
//
//	}
//
//	@Override
//	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
//
//	}
//
//	@Override
//	public void onStart(ITestContext context) {
//		System.out.println("#################### START: " + context.getSkippedTests() + " ##################");
//
//	}
//
//	@Override
//	public void onFinish(ITestContext context) {
//		System.out.println("#################### FINISH: " + context.getFailedTests() + " ##################");
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onExecutionStart() {
//		System.out.println("#################### EXECUTION START ##################");
//
//	}
//
//	@Override
//	public void onExecutionFinish() {
//		System.out.println("#################### EXECUTION FINISH ##################");
//
//	}
//
//	@Override
//	public void onLogMessage(String message) {
//		if(session.isOpen()) {
//			session.getRemote().sendString(text, callback);
//		}
//		
//	}
//
//	@Override
//	public void onReport(String type, String message) {
//		if(session.isOpen()) {
//			session.getRemote().sendString(type);
//		}
//		
//	}
//
//}
