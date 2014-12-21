package org.testngwebrunner.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TestNGSocketConnection {
	public static void main(String[] args) {
		String host = "127.0.0.1";
		int port = 4441;

		StringBuffer instr = new StringBuffer();
		System.out.println("SocketClient initialized");
		try {
			Socket connection = new Socket(host, port);
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

		
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			osw.write("pause" +  (char) 13);
			osw.flush();
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		
			InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");

			int c;
			while ((c = isr.read()) != 13)
				instr.append((char) c);

			connection.close();
			System.out.println(instr.toString());
		} catch (IOException f) {
			System.out.println("IOException: " + f);
		} catch (Exception g) {
			System.out.println("Exception: " + g);
		}
	}
}
