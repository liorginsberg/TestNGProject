package org.testngwebrunner.app.reporters;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class HtmlWriter {
	public static void main(String[] args) throws Exception {
		/* first, get and initialize an engine */
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		/* next, get the Template */
		Template t = ve.getTemplate("templates/index.vm");
		/* create a context and add data */
		VelocityContext context = new VelocityContext();
		context.put("action", "Click Me!");
		/* now render the template into a StringWriter */
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		/* show the World */
		System.out.println(writer.toString());
		
	}
}
