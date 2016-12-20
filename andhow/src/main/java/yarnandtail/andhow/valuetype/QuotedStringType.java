package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.ParsingException;

/**
 * A String type that will keep everything inside of quotes, including whitespace.
 * 
 * Non-quoted values will be trimmed to remove all whitespace and whitespace outside
 * of quotes will be removed.  Internal quotes are unaffected.
 * Examples (using ... to represent spaces):
 * <ul>
 * <li>"...some value..." - - > ...some value...
 * <li>..."...some value..."... - - > ...some value...
 * <li>...some value... - - > some value
 * <li>...some "words" you said... - - > some "words" you said
 * <li>..".some "words" you said.".. - - > .some "words" you said.</ul>
 * 
 * @author eeverman
 */
public class QuotedStringType extends BaseValueType<String> {

	private static final QuotedStringType instance = new QuotedStringType();
	
	private QuotedStringType() {
		super(String.class);
	}
	
	public static QuotedStringType instance() {
		return instance;
	}

	@Override
	public String convert(String sourceValue) throws ParsingException {
		if (sourceValue != null) {
			String s =  TextUtil.trimToNull(sourceValue);
			
			if (s != null && s.length() > 1 && s.startsWith("\"") && s.endsWith("\"")) {
				return s.substring(1, s.length() - 1);
			} else {
				return s;
			}
			
		} else {
			return null;
		}
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
