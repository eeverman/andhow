package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public abstract class BaseValueType<T> implements ValueType<T> {

	protected final Class<T> clazzType;

	public BaseValueType(Class<T> clazzType) {
		this.clazzType = clazzType;
	}
	
	@Override
	public Class<T> getDestinationType() {
		return clazzType;
	}

	@Override
	public boolean isParsable(String sourceValue) {
		try {
			parse(sourceValue);
			return true;
		} catch (ParsingException e) {
			return false;
		}
	}
	
}
