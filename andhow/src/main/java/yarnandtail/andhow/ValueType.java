package yarnandtail.andhow;

import yarnandtail.andhow.load.ParsingException;
import yarnandtail.andhow.valuetype.TrimStyle;

/**
 *
 * @author eeverman
 */
public interface ValueType<T> {
	
	Class<T> getDestinationType();
	
	/**
	 * Attempts to convert the passed, single sourceValue into the type represented
	 * by the destinationType.
	 * If this configPoint is a multiValue, each value should be converted separately.
	 * @param sourceValue
	 * @return
	 * @throws IllegalArgumentException 
	 */
	T convert(String sourceValue) throws ParsingException;
		
	boolean isConvertable(String sourceValue);
	
	boolean isExplicitlySet(String sourceValue);
	
	boolean isNullConsideredAValue();
	
	boolean isEmptyConsideredAValue();
	
	TrimStyle getTrimStyle();
	
	/**
	 * Attempt to cast the passed object to the generic type T.
	 * AndHow uses this internally to cast values known to already be of type T,
	 * but were stored in a generic way.
	 * If used for unknown types that are not castable to T, it will throw
	 * a RuntimeException.
	 * 
	 * @param o
	 * @return
	 * @throws RuntimeException 
	 */
	T cast(Object o) throws RuntimeException;
}
