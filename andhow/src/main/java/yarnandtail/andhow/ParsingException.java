package yarnandtail.andhow;

/**
 * An exception where a String is unreadable or unconvertable to a target type.
 * 
 * @author eeverman
 */
public class ParsingException extends Exception {
	
	final String paramText;
	final String problemText;

	public ParsingException(String message, String paramText, String problemText, Throwable cause) {
		super(message, cause);
		this.paramText = paramText;
		this.problemText = problemText;
	}
	
	public ParsingException(String message, String paramText, String problemText) {
		super(message);
		this.paramText = paramText;
		this.problemText = problemText;
	}
	
	public ParsingException(String message, String problemText, Throwable cause) {
		super(message, cause);
		this.paramText = null;
		this.problemText = problemText;
	}
	
	public ParsingException(String message, String problemText) {
		super(message);
		this.paramText = null;
		this.problemText = problemText;
	}

	public String getParamText() {
		return paramText;
	}

	public String getProblemText() {
		return problemText;
	}


	
	

}
