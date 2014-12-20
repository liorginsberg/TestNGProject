package org.testngwebrunner.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListenerSocket implements IExecutionListener, ITestListener {

	static ServerSocket serverSocket;
	private ServerSocket serverSocket;
	protected final static int port = 4441;
	static Socket connection;

	static boolean first;
	static StringBuffer process;
	static String TimeStamp;

	private boolean pause = false;
	private boolean serverUP = false;

	@Override
	public void onExecutionStart() {
		startServer();
		System.out.println("execution start");
//		try {
//			server = new ServerSocket(port);
//			System.out.println("server socket: initialize");
//			int character;
//			while (true) {
//				connection = server.accept();
//				BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
//				InputStreamReader isr = new InputStreamReader(is);
//				process = new StringBuffer();
//				while ((character = isr.read()) != 13) {
//					process.append((char) character);
//				}
//				BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
//				OutputStreamWriter osw = new OutputStreamWriter(os);
//				osw.write("Server Respond:" + process.toString() + (char) 13);
//				osw.flush();
//				break;
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

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
		while (pause) {}

	}

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub

	}

	public void startServer() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

		Runnable serverTask = new Runnable() {
			

			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(4441);
					System.out.println("Waiting for clients to connect...");
					while (serverUP) {
						Socket clientSocket = serverSocket.accept();
						clientProcessingPool.submit(new ClientTask(clientSocket));
					}
				} catch (IOException e) {
					System.err.println("Unable to process client request");
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();

	}

	private class ClientTask implements Runnable {
		private final Socket clientSocket;

		private ClientTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try {
				BufferedInputStream is = new BufferedInputStream(clientSocket.getInputStream());
				InputStreamReader isr = new InputStreamReader(is);
				process = new StringBuffer();
				int character;
				while ((character = isr.read()) != 13) {
					process.append((char) character);
				}
				if(process.toString().equals("pause")) {
					pause = true;
				}
				BufferedOutputStream os = new BufferedOutputStream(clientSocket.getOutputStream());
				OutputStreamWriter osw = new OutputStreamWriter(os);
				osw.write("Server Respond:" + process.toString() + (char) 13);
				osw.flush();
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
