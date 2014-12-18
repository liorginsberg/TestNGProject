package org.testngwebrunner.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.testng.IExecutionListener;

public class TestNGListenerSocket implements IExecutionListener {

	static ServerSocket server;
	protected final static int port = 4441;
	static Socket connection;

	static boolean first;
	static StringBuffer process;
	static String TimeStamp;

	@Override
	public void onExecutionStart() {
		try {
			server = new ServerSocket(port);
			System.out.println("server socket: initialize");
			int character;
			while (true) {
				connection = server.accept();
				BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
				InputStreamReader isr = new InputStreamReader(is);
				process = new StringBuffer();
				while ((character = isr.read()) != 13) {
					process.append((char) character);
				}
				BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
		        OutputStreamWriter osw = new OutputStreamWriter(os);
		        osw.write("Server Respond:" + process.toString() + (char) 13);
		        osw.flush();
		        break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onExecutionFinish() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
