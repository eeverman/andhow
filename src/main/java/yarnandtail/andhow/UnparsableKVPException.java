package yarnandtail.andhow;

import org.apache.commons.lang3.StringUtils;

/**
 * Happens when an error occurs parsing KVP values and we have not context
 * of what parameter is being parsed of where the value is coming from.
 * @author eeverman
 */
public class UnparsableKVPException extends RuntimeException {
	
	String reason;
	String paramText;
	
	/**
	 * We know why there is an issue, but don't have the complete original text
	 * @param reason 
	 */
	public UnparsableKVPException(String reason) {
		super(reason);
		
		this.reason = reason;
	}
	
	public UnparsableKVPException(String reason, String paramText) {
		super("The parameter text '" + StringUtils.abbreviate(paramText, 40) +  
				"' could not be parsed.  Reason: " + reason);
		
		this.paramText = paramText;
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}

	public String getParamText() {
		return paramText;
	}
}
