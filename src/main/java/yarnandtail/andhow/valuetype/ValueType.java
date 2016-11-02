package yarnandtail.andhow.valuetype;

/**
 *
 * @author eeverman
 */
public interface ValueType<T> {
	
	Class<T> getDestinationType();
	
	T convert(Object sourceValue) throws IllegalArgumentException;
	
	boolean isConvertable(Object sourceValue);
	
	boolean isNullConsideredAValue();
	
	boolean isEmptyConsideredAValue();
	
	TrimStyle getTrimStyle();
	
	boolean isUsingExpressions();
}
