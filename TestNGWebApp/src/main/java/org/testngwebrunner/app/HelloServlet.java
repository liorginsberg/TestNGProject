//package org.testngwebrunner.app;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.testng.IExecutionListener;
//import org.testng.ITestContext;
//import org.testng.ITestListener;
//import org.testng.ITestResult;
//import org.testng.TestNG;
//
//public class HelloServlet extends HttpServlet implements IExecutionListener, ITestListener {
//
//	private static final long serialVersionUID = 1L;
//	private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
//
//	private String msg = "";
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.setContentType("text/event-stream");
//		resp.setCharacterEncoding("UTF-8");
//
//		PrintWriter writer = resp.getWriter();
//		while(true) {
//			if(!msg.isEmpty()) {
//				String currentTime = sdf.format(new Date(System.currentTimeMillis()));
//				writer.write("data: execution " + msg + ": " + currentTime + "\n\n");
//				msg = "";
//				break;
//			} else {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				};
//			}
//		}
//		
////
////		for (int i = 0; i < 20; i++) {
////
////			writer.write("data: " + sdf.format(new Date(System.currentTimeMillis())) + "\n\n");
////			writer.flush();
////
////			try {
////				Thread.sleep(1000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
////		}
//		writer.close();
//	}
//
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		TestNG testng = new TestNG();
//		testng.setVerbose(2);
//		testng.addExecutionListener(this);
//		testng.addListener(this);
//		testng.setTestClasses(new Class[] { MyCodeTests.class });
//		testng.run();
//	}
//
//	@Override
//	public void onExecutionStart() {
//		msg = "start";
//
//	}
//
//	@Override
//	public void onExecutionFinish() {
//		msg = "finish";
//	}
//
//	@Override
//	public void onTestStart(ITestResult result) {
//		msg = "test start: " + result.getName();
//
//	}
//
//	@Override
//	public void onTestSuccess(ITestResult result) {
//		msg = "test success: " + result.getName();
//
//	}
//
//	@Override
//	public void onTestFailure(ITestResult result) {
//		msg = "test fail: " + result.getName();
//
//	}
//
//	@Override
//	public void onTestSkipped(ITestResult result) {
//		msg = "test skip: " + result.getName();
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStart(ITestContext context) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onFinish(ITestContext context) {
//		// TODO Auto-generated method stub
//
//	}
//}
