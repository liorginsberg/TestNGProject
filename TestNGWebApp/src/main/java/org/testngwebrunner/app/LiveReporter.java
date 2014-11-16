package org.testngwebrunner.app;

import java.text.SimpleDateFormat;
import java.util.Date;

//singletone implementation of LiveReporter
public class LiveReporter {

	private static LiveReporter reporter;
	private static LiveReporterListener liveListenter;
	
	private LiveReporter() {		
	}
	
	public static LiveReporter getInstance() {
		if(reporter == null) {
			reporter = new LiveReporter();
		} 
		return reporter;
		
	}
	
	public static void setListener(LiveReporterListener listener) {
		liveListenter = listener;
	}
	
	public void report(String message) {
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		System.out.println(time + ": " + message);
		if(liveListenter!=null)
			liveListenter.onLogMessage(time + ": " + message);
	}
}
