package org.testngwebrunner.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TestNGSocketConnection {

	
	
	private final String HOST = "127.0.0.1";
	private final int PORT = 4441;
	private Socket connection;
	private StringBuffer instr;
	private OutputStreamWriter osw;
	private InputStreamReader isr;

	public TestNGSocketConnection() {
		instr = new StringBuffer();
		try {
			connection = new Socket(HOST, PORT);
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			osw = new OutputStreamWriter(bos);
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			isr = new InputStreamReader(bis, "US-ASCII");
			System.out.println("TestNGSocketConnection: connection established.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pauseExec() throws IOException {
		System.out.println("about to send \"pause\" to TestNGServersocket" );
		osw.write("pause" + (char) 13);
		osw.flush();
		int c;
		while ((c = isr.read()) != 13) {
			instr.append((char) c);
		}
		
		System.out.println(instr.toString());
	}
	public void resumeExec() throws IOException {
		System.out.println("about to send \"resume\" to TestNGServersocket" );
		osw.write("resume" + (char) 13);
		osw.flush();
		int c;
		while ((c = isr.read()) != 13) {
			instr.append((char) c);
		}
		
		System.out.println(instr.toString());
	}
	
	public void skipAll() throws IOException {
		System.out.println("about to send \"skipAll\" to TestNGServersocket" );
		osw.write("skipAll" + (char) 13);
		osw.flush();
		int c;
		while ((c = isr.read()) != 13) {
			instr.append((char) c);
		}
		
		System.out.println(instr.toString());
	}
	
	
	public void echo(String msg) throws IOException {
		
		System.out.println("about to send \"" + msg + "\" to TestNGServersocket" );
		osw.write(msg + (char) 13);
		osw.flush();
		int c;
		while ((c = isr.read()) != 13) {
			instr.append((char) c);
		}
		System.out.println(instr.toString());
	}
	
	
	public void close() {
		try {
			osw.close();
			isr.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
