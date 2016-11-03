package yarnandtail.andhow.valuetype;

import java.util.List;
import yarnandtail.andhow.ConfigValueCollection;

/**
 *
 * @author eeverman
 */
public interface ValueType<T> {
	
	Class<T> getDestinationType();
	
	/**
	 * This assumes that the sourceValue is already parsed if it is a multi-value.
	 * @param sourceValue
	 * @param loadedValues
	 * @return
	 * @throws IllegalArgumentException 
	 */
	T convert(Object sourceValue, ConfigValueCollection loadedValues) throws IllegalArgumentException;
		
	boolean isConvertable(Object sourceValue, ConfigValueCollection loadedValues);
	
	boolean isMissingReferences(Object sourceValue, ConfigValueCollection loadedValues);
	
	boolean isExplicitlySet(Object sourceValue, ConfigValueCollection loadedValues);
	
	boolean isNullConsideredAValue();
	
	boolean isEmptyConsideredAValue();
	
	TrimStyle getTrimStyle();
	
	boolean isUsingExpressions();
}
