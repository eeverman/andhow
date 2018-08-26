package org.yarnandtail.andhow.sample;

import org.yarnandtail.andhow.util.TextUtil;

/**
 * PrintFormat implementation specifically for property files.
 * 
 * @author ericeverman
 */
public class PropFileFormat extends PrintFormat {

	/**
	 * Constructor that initializes all needed values to be a properties file.
	 */
	public PropFileFormat() {
		blockCommentStart = null;
		blockCommentEnd = null;
		blockCommentSeparator = null;
		lineCommentPrefix = "#";
		lineCommentPrefixSeparator = " ";	//Separate the opening line comment from the text
		secondLineIndent = "\t";
		hr = TextUtil.repeat("##", 45);
		lineWidth = 90;
	}
}
