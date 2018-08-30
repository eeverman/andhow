package org.yarnandtail.andhow.sample;

import org.yarnandtail.andhow.util.TextUtil;

/**
 * PrintFormat implementation specifically for property files.
 * 
 * @author ericeverman
 */
public class JndiFileFormat extends PrintFormat {

	/**
	 * Constructor that initializes all needed values to be a properties file.
	 */
	public JndiFileFormat() {
		blockCommentStart = "<!--";
		blockCommentEnd = "-->";
		blockCommentSeparator = " "; //Use between the last text and the comment end if on same line
		lineCommentPrefix = null;
		lineCommentPrefixSeparator = " ";	//Separate the opening line comment from the text
		secondLineIndent = "\t";
		hr = TextUtil.repeat("- ", 45);
		lineWidth = 90;
	}
}
