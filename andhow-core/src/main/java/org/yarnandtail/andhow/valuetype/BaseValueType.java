package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.api.ValueType;

/**
 * Simple ValueType base class that provides shared functionality
 *
 * @param <T> The Class type this ValueType represents
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
	public String toString(T value) {
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

}
