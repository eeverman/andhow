package yarnandtail.andhow.valuetype;

import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigValueCollection;

/**
 *
 * @author eeverman
 */
public class StringType extends BaseValueType implements ValueType {

	private StringType() {
		super(String.class, false, false, TrimStyle.TO_NULL, true);
	}

	@Override
	public Object convert(Object sourceValue, ConfigValueCollection loadedValues) throws IllegalArgumentException {
		if (sourceValue != null) {
			String str = StringUtils.trimToNull(sourceValue.toString());
			return str;
		} else {
			return null;
		}
	}

	@Override
	public boolean isMissingReferences(Object sourceValue, ConfigValueCollection loadedValues) {
		return false;
	}
	
}
