package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public class StringType extends BaseValueType<String> {

	private static final StringType instance = new StringType();
	
	private StringType() {
		super(String.class);
	}
	
	public static StringType instance() {
		return instance;
	}

	@Override
	public String convert(String sourceValue) throws ParsingException {
		if (sourceValue != null) {
			return TextUtil.trimToNull(sourceValue);
		} else {
			return null;
		}
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
