package org.yarnandtail.andhow.testutil;

import java.util.Properties;

/**
 * Non-reflective utils for testing
 */
public class GeneralTestUtil {

	/**
	 * Creates a clone of a Properties object so it can be detached from System.
	 *
	 * @param props
	 * @return
	 */
	public static Properties clone(Properties props) {
		Properties newProps = new Properties();
		newProps.putAll(props);
		return newProps;
	}
}
