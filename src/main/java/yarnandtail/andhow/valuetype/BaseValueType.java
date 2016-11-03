package yarnandtail.andhow.valuetype;

import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigValueCollection;

/**
 *
 * @author eeverman
 */
public abstract class BaseValueType<T> implements ValueType<T> {

	protected final Class<T> clazzType;
	protected final boolean nullConsideredAValue;
	protected final boolean emptyConsideredAValue;
	protected final TrimStyle trimStyle;
	protected final boolean usingExpressions;

	public BaseValueType(Class<T> clazzType, boolean nullConsideredAValue, boolean emptyConsideredAValue, TrimStyle trimStyle, boolean usingExpressions) {
		this.clazzType = clazzType;
		this.nullConsideredAValue = nullConsideredAValue;
		this.emptyConsideredAValue = emptyConsideredAValue;
		this.trimStyle = trimStyle;
		this.usingExpressions = usingExpressions;
	}
	
	@Override
	public Class<T> getDestinationType() {
		return clazzType;
	}

	@Override
	public boolean isConvertable(Object sourceValue, ConfigValueCollection loadedValues) {
		try {
			convert(sourceValue, loadedValues);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	@Override
	public boolean isExplicitlySet(Object sourceValue, ConfigValueCollection loadedValues) {
		
		String strVal = null;
		
		if (sourceValue != null) {
			if (sourceValue instanceof String) {
				strVal = (String)sourceValue;
			} else {
				return true;
			}
		}
		
		if (trimStyle.equals(TrimStyle.TO_EMPTY)) {
			strVal = StringUtils.trimToEmpty(strVal);
		} else if (trimStyle.equals(TrimStyle.TO_NULL)) {
			strVal = StringUtils.trimToNull(strVal);
		}
		
		if (strVal != null) {
			if (! strVal.isEmpty()) {
				return true;
			} else {
				return emptyConsideredAValue;
			}
		} else {
			return nullConsideredAValue;
		}
	}

	@Override
	public boolean isNullConsideredAValue() {
		return nullConsideredAValue;
	}

	@Override
	public boolean isEmptyConsideredAValue() {
		return emptyConsideredAValue;
	}

	@Override
	public TrimStyle getTrimStyle() {
		return trimStyle;
	}

	@Override
	public boolean isUsingExpressions() {
		return usingExpressions;
	}
	
}
