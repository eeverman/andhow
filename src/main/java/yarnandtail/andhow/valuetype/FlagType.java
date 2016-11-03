package yarnandtail.andhow.valuetype;

import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigParamUtil;
import yarnandtail.andhow.ConfigValueCollection;

/**
 *
 * @author eeverman
 */
public class FlagType extends BaseValueType implements ValueType {

	private static final FlagType instance = new FlagType();
	
	private FlagType() {
		super(String.class, false, true, TrimStyle.TO_EMPTY, true);
	}
	
	public static FlagType get() {
		return instance;
	}

	@Override
	public Object convert(Object sourceValue, ConfigValueCollection loadedValues) throws IllegalArgumentException {
		if (sourceValue != null) {
			if (sourceValue instanceof Boolean) {
				return ((Boolean)sourceValue).booleanValue();
			} else {
				
				String str = StringUtils.trimToEmpty(sourceValue.toString());
				if (str.isEmpty()) {
					return true;	//a flag is considered try just by its presence
				} else {
					return ConfigParamUtil.toBoolean(str);
				}
			}

		} else {
			return false;
		}
	}

	@Override
	public boolean isMissingReferences(Object sourceValue, ConfigValueCollection loadedValues) {
		return false;
	}
	
}
