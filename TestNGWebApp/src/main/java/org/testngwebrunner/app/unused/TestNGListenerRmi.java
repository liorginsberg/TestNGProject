package org.testngwebrunner.app.unused;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import testng.commons.rmi.Constant;
import testng.commons.rmi.IExecutionActionRemote;

public class TestNGListenerRmi implements IExecutionListener, ITestListener {

	private boolean pause = false;
	private Registry rmiRegistry = null;

	@Override
	public void onExecutionStart() {
		startServer();
	}

	@Override
	public void onExecutionFinish() {
		stopServer();
	}

	@Override
	public void onTestStart(ITestResult result) {

	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestFailure(ITestResult result) {
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

		while (pause) {
			System.out.println("in pause mode");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onFinish(ITestContext context) {
		while (pause) {
			System.out.println("in pause mode");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void startServer() {

		try {
			ExecutionActionRemoteImpl impl = new ExecutionActionRemoteImpl();
			rmiRegistry = LocateRegistry.createRegistry(Constant.EXECUTION_RMI_PORT);
			rmiRegistry.rebind(Constant.EXECUTION_RMI_ID, impl);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Started RMI Action Server");
	}

	private void stopServer() {
		if (rmiRegistry != null) {
			try {
				rmiRegistry.unbind(Constant.EXECUTION_RMI_ID);
				UnicastRemoteObject.unexportObject(rmiRegistry, true);
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
