package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.util.TextUtil;
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
	public String parse(String sourceValue) throws ParsingException {
		return sourceValue;		//Too simple!  Trimming is handled separately
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
