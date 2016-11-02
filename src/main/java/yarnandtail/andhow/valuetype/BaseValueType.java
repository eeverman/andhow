package yarnandtail.andhow.valuetype;

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

//	@Override
//	public T convert(Object sourceValue) throws IllegalArgumentException {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}

	@Override
	public boolean isConvertable(Object sourceValue) {
		try {
			convert(sourceValue);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
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
