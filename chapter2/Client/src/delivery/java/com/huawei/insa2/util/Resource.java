package com.huawei.insa2.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;

public class Resource {

	public Resource(String url) throws IOException {
		init(url);
	}

	public Resource(Class c, String url) throws IOException {
		String className = c.getName();
		int i = className.lastIndexOf('.');
		if (i > 0)
			className = className.substring(i + 1);
		URL u = new URL(c.getResource(String.valueOf(String.valueOf(className))
				.concat(".class")), url);
		init(u.toString());
	}

	public void init(String url) throws IOException {
		String str = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(url))))));
		InputStream in = null;
		do
			try {
				resource = new Cfg(String.valueOf(String.valueOf(str)).concat(
						".xml"), false);
				return;
			} catch (IOException ex) {
				ex.printStackTrace();
				int i = str.lastIndexOf('_');
				if (i < 0)
					throw new MissingResourceException(String.valueOf(String
							.valueOf((new StringBuffer(
									"Can't find resource url:")).append(url)
									.append(".xml"))), getClass().getName(),
							null);
				str = str.substring(0, i);
			}
		while (true);
	}

	public String get(String key) {
		return resource.get(key, key);
	}

	public String[] childrenNames(String key) {
		return resource.childrenNames(key);
	}

	public String get(String key, Object params[]) {
		String value = resource.get(key, key);
		try {
			String s = MessageFormat.format(value, params);
			return s;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String s1 = key;
		return s1;
	}

	public String get(String key, Object param) {
		return get(key, new Object[] { param });
	}

	public String get(String key, Object param1, Object param2) {
		return get(key, new Object[] { param1, param2 });
	}

	public String get(String key, Object param1, Object param2, Object param3) {
		return get(key, new Object[] { param1, param2, param3 });
	}

	private Cfg resource;
}
