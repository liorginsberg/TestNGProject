package org.testngwebrunner.app.reporters;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.xml.XmlSuite;

public class HTMLReporter implements IReporter {

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		for (ISuite suite : suites) {
			for (ISuiteResult result : suite.getResults().values()) {
				result.getTestContext().getFailedConfigurations();
			}
		}
	}
}
