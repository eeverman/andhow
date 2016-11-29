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
	public boolean isValid(String value) {
		if (value != null && value.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}
	
}
