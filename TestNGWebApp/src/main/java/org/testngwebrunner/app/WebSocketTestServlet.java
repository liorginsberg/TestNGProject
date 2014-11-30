package org.testngwebrunner.app;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;



@SuppressWarnings("serial")
public class WebSocketTestServlet extends WebSocketServlet {

	
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(TestExecutorWebSocket.class);
	}
}