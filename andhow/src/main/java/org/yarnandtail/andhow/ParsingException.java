package org.yarnandtail.andhow;

/**
 * An exception where a String is unreadable or unconvertable to a target type.
 * 
 * @author eeverman
 */
public class ParsingException extends Exception {
	
	final String problemText;
	
	public ParsingException(String message, String problemText, Throwable cause) {
		super(message, cause);
		this.problemText = problemText;
	}
	
	public ParsingException(String message, String problemText) {
		super(message);
		this.problemText = problemText;
	}

	public String getProblemText() {
		return problemText;
	}


	
	

}
