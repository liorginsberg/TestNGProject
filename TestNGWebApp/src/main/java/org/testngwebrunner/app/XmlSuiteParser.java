package org.testngwebrunner.app;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class XmlSuiteParser{
	public static void main(String[] args) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		parser.parse(new File("C:\\TestNGProject\\com.testng.tests\\src\\main\\resources\\suites\\s1.xml"), new XmlSuiteHandler());
		
	}
	
	static class XmlSuiteHandler extends DefaultHandler {
		Long start = null;
		String indent = "  ";
		int indentStack = 0;
		
		
		@Override
		public void startDocument() throws SAXException {
			start = System.currentTimeMillis();
			System.out.println("start suite xml processing");
		}

		@Override
		public void endDocument() throws SAXException {
			
			System.out.println("process took: " + (System.currentTimeMillis() - start) + "ms");
		}
		
		private void indent() {
			for(int i= 0; i< indentStack; i++) {
				System.out.print(indent);
			}
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			indentStack++;
			
			switch (qName) {
			case "suite":
				System.out.println("<suite name=" + attributes.getValue("name") + ">");
				break;
			case "test":
				
				String name = attributes.getValue("name");
				if(name.contains("startContainer") || name.contains("endContainer")) {
					System.out.println("<test name=" + attributes.getValue("name") + " enabled=" + attributes.getValue("enabled") + "/>");					
				} else {
					System.out.println("<test name=" + attributes.getValue("name") + " enabled=" + attributes.getValue("enabled") + ">");					
				}
				break;
			case "classes":
				System.out.println("<classes>");
				break;
			case "class":
				System.out.println("<class name=" + attributes.getValue("name") + ">");
				break;
			case "methods":
				System.out.println("<methods>");
				break;
			case "include":
				System.out.println("<include name=" + attributes.getValue("name") + ">");
				break;
			case "parameter":
				System.out.println("<parameter name=" + attributes.getValue("name") +" value=" + attributes.getValue("value") + "/>");
				break;
			default:
				break;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			
			
			switch (qName) {
			case "parameter":
				break;
			default:
				indentStack--;
				indent();
				System.out.println("<" + qName + "/>");
				break;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			//not needed
		}
		
	}
}
