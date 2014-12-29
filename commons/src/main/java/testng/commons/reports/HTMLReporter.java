package testng.commons.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class HTMLReporter implements IReporter {

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		FileWriter fw = null;
		File indexFile = null;
		for (ISuite suite : suites) {
			File testingFile = new File(suite.getOutputDirectory() + File.separator + "liorReport");
			try {
				testingFile.mkdirs();
				indexFile = new File(suite.getOutputDirectory() + File.separator + "liorReport" + File.separator + "index.html");
				indexFile.createNewFile();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			try {
				fw = new FileWriter(indexFile);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} 

			for (ISuiteResult result : suite.getResults().values()) {
				String status = null; 
				try {
					writeNewLineToFile(fw,result.getTestContext().getName());
					writeToFile(fw," start=" + result.getTestContext().getStartDate());
					writeToFile(fw,", end=" + result.getTestContext().getEndDate());
					Set<ITestResult> resPass = result.getTestContext().getPassedTests().getAllResults();
					if(resPass.size() > 0) {
						status = "PASS";
						Iterator<ITestResult> it = resPass.iterator();
						while (it.hasNext()) {
							ITestResult iTestResult = (ITestResult) it.next();
							writeToFile(fw, ", My Attr=" + iTestResult.getAttribute("ok"));
							writeToFile(fw, ", Name: " + iTestResult.getMethod().getMethodName());
							
						}
						
					}
					Set<ITestResult> resFail = result.getTestContext().getFailedTests().getAllResults();
					if(resFail.size() > 0) {
						status = "FAIL";
						Iterator<ITestResult> it = resFail.iterator();
						while (it.hasNext()) {
							ITestResult iTestResult = (ITestResult) it.next();
							writeToFile(fw, ", My Attr=" + iTestResult.getAttribute("ok"));
							writeToFile(fw, ", Name: " + iTestResult.getMethod().getMethodName());
							
						}
					}
					if(status != null)
						writeToFile(fw,", status=" + status);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void writeNewLineToFile(FileWriter fw, String content) throws IOException {
		fw.write(System.getProperty("line.separator") + content);
		fw.flush();
	}
	public void writeToFile(FileWriter fw, String content) throws IOException {
		fw.write(content);
		fw.flush();
	}

}
