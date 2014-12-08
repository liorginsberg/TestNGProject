package org.testngwebrunner.app;

import java.util.LinkedList;

public class LiveReporter {

	private static LiveReporter reporter;

	private static LinkedList<LiveReporterListener> liveReporterListeners = null;

	private LiveReporter() {
	}

	public static LiveReporter getInstance() {
		if (reporter == null) {
			reporter = new LiveReporter();
			liveReporterListeners = new  LinkedList<LiveReporterListener>();
		}
		return reporter;

	}

	public void addListener(LiveReporterListener listener) {
		
		if (liveReporterListeners.contains(listener)) {
			return;
		}
		liveReporterListeners.add(listener);
	}

	public  void removeListener(LiveReporterListener listener) {
		if (liveReporterListeners.contains(liveReporterListeners)) {
			liveReporterListeners.remove(liveReporterListeners);
		}
	}

	public void report(String report) {

		for (LiveReporterListener listener : liveReporterListeners) {
			listener.onReport(report);
		}
	}
}
