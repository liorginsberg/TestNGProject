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
import org.testngwebrunner.app.listeners.MyTransformer;

public class TestNGListenerSocket implements IExecutionListener, ITestListener {

	private ServerSocket serverSocket;
	protected final static int port = 4441;

	static StringBuffer process;

	private boolean pause = false;
	private boolean serverUP = false;

	@Override
	public void onExecutionStart() {
		startServer();
		System.out.println("execution start");
	}

	@Override
	public void onExecutionFinish() {
		try {
			System.out.println("closing server...");
			serverUP = false;
			serverSocket.close();
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
		serverUP = true;
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
		serverThread.setName("server4441");
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
				System.out.println("a new socket runnable running");
				BufferedInputStream is = new BufferedInputStream(clientSocket.getInputStream());
				InputStreamReader isr = new InputStreamReader(is);
				process = new StringBuffer();
				int character;
				while ((character = isr.read()) != 13) {
					process.append((char) character);
				}
				if (process.toString().equals("pause")) {
					pause = true;
				} else if(process.toString().equals("resume")) {			
					pause = false;
				} else if(process.toString().equals("skipAll")) {			
					MyMethodInterceptor.stopAll = true;
				} else {
					System.out.println("message not handled: " + process.toString());
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
