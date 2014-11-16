package org.testngwebrunner.app;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class TestProjectChooser {

	private Session session;
	
	@OnWebSocketConnect
	public void onSessionConnect(Session session) {
		this.session = session;
	}
	
	@OnWebSocketMessage
	public void onText(Session session, String message) {
		//if(message.equals("execute")) {
			new TestExecutor().runTests(session, message);
			//session.close(0, "end execute for: " + message);
	//	}
//		if (session.isOpen()) {
//			System.out.printf("Echoing back message [%s]%n", message);
//			session.getRemote().sendString(message, null);
//		}
	}
}