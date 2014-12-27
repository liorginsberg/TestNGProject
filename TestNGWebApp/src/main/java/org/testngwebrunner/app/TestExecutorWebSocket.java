package org.testngwebrunner.app;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.testngwebrunner.app.rmi.Constant;
import org.testngwebrunner.app.rmi.IExecutionActionRemote;

@WebSocket
public class TestExecutorWebSocket {

	private Session session;
	private TestExecutor testExecutor;

	@OnWebSocketConnect
	public void onSessionConnect(Session session) {
		this.session = session;
		System.out.println("End point Connect");

	}

	@OnWebSocketMessage
	public void onText(Session session, String message) throws Exception {
		System.out.println("got message from app: " + message);
		if (message.equals("pause")) {
			Registry registry = LocateRegistry.getRegistry("localhost", Constant.RMI_PORT);
			IExecutionActionRemote executionActionRemote = (IExecutionActionRemote) registry.lookup(Constant.RMI_ID);
			executionActionRemote.pause();
			
		} else if (message.equals("resume")) {
			Registry registry = LocateRegistry.getRegistry("localhost", Constant.RMI_PORT);
			IExecutionActionRemote executionActionRemote = (IExecutionActionRemote) registry.lookup(Constant.RMI_ID);
			executionActionRemote.resume();
			
		} else if (message.equals("stop")) {
			Registry registry = LocateRegistry.getRegistry("localhost", Constant.RMI_PORT);
			IExecutionActionRemote executionActionRemote = (IExecutionActionRemote) registry.lookup(Constant.RMI_ID);
			executionActionRemote.skipAll();
			
		} else {
			testExecutor = new TestExecutor();
			testExecutor.runTests(session, message);
		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int closeCode, String closeReason) {
		System.out.println("End point close");
	}
}