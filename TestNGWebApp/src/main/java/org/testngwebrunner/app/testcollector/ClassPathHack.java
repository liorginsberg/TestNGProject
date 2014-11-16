package org.testngwebrunner.app.testcollector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathHack {
	private static final Class<?>[] parameters = new Class[] { URL.class };

	public static URLClassLoader addFile(String s) throws IOException {
		File f = new File(s);
		return addFile(f);
	}

	public static URLClassLoader addFile(File f) throws IOException {
		return addURL(f.toURI().toURL());
	}

	public static URLClassLoader addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
		return sysloader;

	}
}