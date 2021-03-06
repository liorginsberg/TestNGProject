package com.testng.tests;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import testng.commons.rmi.Constant;
import testng.commons.rmi.IExecutionListenerRemote;

public class Reporter {

	private static Reporter instance = null;
	private Registry executionListenerRegistry;
	private IExecutionListenerRemote executionListenerRemote;

	private Reporter() {
	}

	public static Reporter getInstance() {
		if (instance == null) {
			instance = new Reporter();
		}
		return instance;
	}

	public void report(String msg) {

		try {
			executionListenerRegistry = LocateRegistry.getRegistry("localhost", Constant.EXECUTION_LISTENER_RMI_PORT);
			executionListenerRemote = (IExecutionListenerRemote) executionListenerRegistry.lookup(Constant.EXECUTION_LISTENER_RMI_ID);
			executionListenerRemote.report(msg);
		} catch (NotBoundException e) {
			System.out.println(e.getMessage());
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (Throwable e) {
			System.out.println(e.getMessage());			
		}
	}
}
