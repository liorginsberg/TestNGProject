package org.testngwebrunner.app;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

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
			TestNGSocketConnection conn = new TestNGSocketConnection();
			conn.pauseExec();
			conn.close();
		} else if (message.equals("resume")) {
			TestNGSocketConnection conn = new TestNGSocketConnection();
			conn.resumeExec();
			conn.close();
		} else if (message.equals("stop")) {
			TestNGSocketConnection conn = new TestNGSocketConnection();
			conn.skipAll();
			conn.close();
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