package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.valuetype.*;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigValueCollection;
import yarnandtail.andhow.ParsingException;
import yarnandtail.andhow.staticparam.BaseValueType;

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
			String str = StringUtils.trimToNull(sourceValue.toString());
			return str;
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

	
}
