package yarnandtail.andhow.valid;

import java.util.regex.PatternSyntaxException;
import yarnandtail.andhow.Validator;

/**
 *
 * @author eeverman
 */
public class StringRegex implements Validator<String> {
	
	String regex;
	
	public StringRegex(String regex) {
		this.regex = regex;
	}
	
	@Override
	public boolean isSpecificationValid() {
		
		try {
			"".matches(regex);
			return true;
		} catch (PatternSyntaxException e) {
			return false;
		}
		
	}
	
	@Override
	public String getInvalidSpecificationMessage() {
		return "The expression '" + regex + "' is not a valid regex expression";
	}

	@Override
	public boolean isValid(String value) {
		if (value != null && value.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getInvalidMessage(String value) {
		return "The value '" + value + "' does not match the regex expression '" + regex + "'";
	}
	
}
