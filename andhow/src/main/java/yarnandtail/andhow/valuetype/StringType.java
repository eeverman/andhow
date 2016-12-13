package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.load.ParsingException;

/**
 *
 * @author eeverman
 */
public class StringType extends BaseValueType<String> {

	private static final StringType instance = new StringType();
	
	private StringType() {
		super(String.class, false, false, TrimStyle.TO_NULL);
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
	public boolean isConvertable(String sourceValue) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isExplicitlySet(String sourceValue) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
