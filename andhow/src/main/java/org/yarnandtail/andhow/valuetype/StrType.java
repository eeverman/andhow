package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public class StrType extends BaseValueType<String> {

	private static final StrType instance = new StrType();
	
	private StrType() {
		super(String.class);
	}
	
	public static StrType instance() {
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
