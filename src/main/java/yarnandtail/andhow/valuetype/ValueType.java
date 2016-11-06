package yarnandtail.andhow.valuetype;

import java.util.List;
import yarnandtail.andhow.ParsingException;

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
	 * @param loadedValues
	 * @return
	 * @throws IllegalArgumentException 
	 */
	T convert(String sourceValue) throws ParsingException;
		
	boolean isConvertable(String sourceValue);
	
	boolean isConvertableTo(String sourceValue, Class<?> toType);
	
	boolean isExplicitlySet(String sourceValue);
	
	boolean isNullConsideredAValue();
	
	boolean isEmptyConsideredAValue();
	
	TrimStyle getTrimStyle();
}
