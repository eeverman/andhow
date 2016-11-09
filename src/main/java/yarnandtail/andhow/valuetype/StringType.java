package yarnandtail.andhow.valuetype;

import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigValueCollection;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public class StringType extends BaseValueType<String> {

	private StringType() {
		super(String.class, false, false, TrimStyle.TO_NULL);
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
