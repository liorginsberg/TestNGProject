package org.testngwebrunner.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testngwebrunner.app.unused.MyMethodInterceptor;

import testng.commons.rmi.Constant;
import testng.commons.rmi.IExecutionActionRemote;
import testng.commons.rmi.IExecutionListenerRemote;

public class ExecutionListener implements ITestListener, IExecutionListener {
	private boolean pause = false;
	private Registry executionListenerRegistry;
	private Registry executionActionRegistry;
	private IExecutionListenerRemote executionListenerRemote;

	@Override
	public void onTestStart(ITestResult result) {
		report("{\"type\":\"testStart\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		report("{\"type\":\"testSuccess\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		report("{\"type\":\"testFail\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		report("{\"type\":\"testSkip\", \"message\":\"" + getRealId(result.getTestContext().getName()) + "\"}");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onStart(ITestContext context) {
		while (pause) {
			report("in pause mode");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Pattern p = Pattern
				.compile("([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}):([startContainer|endContainer|startSuite|endSuite]+)");
		Matcher m = p.matcher(context.getName());
		if (m.find()) {
			String containerEvent = m.group(2);
			String msg = m.group(1);
			report("{\"type\":\"" + containerEvent + "\", \"message\":\"" + getRealId(msg) + "\"}");
		} else {
			report("{\"type\":\"start\", \"message\":\"" + getRealId(context.getName()) + "\"}");
		}

	}

	@Override
	public void onFinish(ITestContext context) {
		while (pause) {
			report("in pause mode");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		report("{\"type\":\"finish\", \"message\":\"" + getRealId(context.getName()) + "\"}");
		

	}

	@Override
	public void onExecutionStart() {
		startServer();
		report("{\"type\":\"startExecution\", \"message\":\"startExecution\"}");

	}

	@Override
	public void onExecutionFinish() {
		stopServer();
		report("{\"type\":\"finishExecution\", \"message\":\"finishExecution\"}");
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
		if (realId == null) {
			System.out.println("##################### not goood ################");
		}
		return realId;

	}

	private void report(String msg) {

		try {
			executionListenerRegistry = LocateRegistry.getRegistry("localhost", Constant.EXECUTION_LISTENER_RMI_PORT);
			executionListenerRemote = (IExecutionListenerRemote) executionListenerRegistry.lookup(Constant.EXECUTION_LISTENER_RMI_ID);
			executionListenerRemote.report(msg);
		} catch (RemoteException | NotBoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void startServer() {

		try {
			ExecutionActionRemoteImpl impl = new ExecutionActionRemoteImpl();
			executionActionRegistry = LocateRegistry.createRegistry(Constant.EXECUTION_RMI_PORT);
			executionActionRegistry.rebind(Constant.EXECUTION_RMI_ID, impl);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Started RMI Action Server");
	}

	private void stopServer() {
		if (executionActionRegistry != null) {
			try {
				executionActionRegistry.unbind(Constant.EXECUTION_RMI_ID);
				UnicastRemoteObject.unexportObject(executionActionRegistry, true);
				System.out.println("Stopped RMI Aciton Server");
			} catch (NoSuchObjectException e) {
				System.out.println(e.getMessage());
			} catch (AccessException e) {
				System.out.println(e.getMessage());
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			} catch (NotBoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	class ExecutionActionRemoteImpl extends UnicastRemoteObject implements IExecutionActionRemote {

		private static final long serialVersionUID = 1L;

		protected ExecutionActionRemoteImpl() throws RemoteException {
			super();
		}

		@Override
		public void pause() throws RemoteException {
			pause = true;
		}

		@Override
		public void resume() throws RemoteException {
			pause = false;

		}

		@Override
		public void skipAll() throws RemoteException {
			MyMethodInterceptor.stopAll = true;
		}

	}

}
