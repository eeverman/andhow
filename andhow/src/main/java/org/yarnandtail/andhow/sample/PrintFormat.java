package org.yarnandtail.andhow.sample;

import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericeverman
 */
public class PrintFormat {
	
	/** either block comments or lineComment should be non-null, but not both */
	public String blockCommentStart;
	public String blockCommentEnd;
	public String blockCommentSeparator = "\t"; //Use between the last text and the comment end if on same line
	public String lineCommentPrefix;
	public String lineCommentPrefixSeparator = " "; //Separate the opening line comment from the text
	public String secondLineIndent;
	public String hr;
	public int lineWidth;

	public boolean usesLineComments() {
		return TextUtil.trimToNull(lineCommentPrefix) != null;
	}

	public boolean usesBlockComments() {
		return !usesLineComments();
	}
	
}
