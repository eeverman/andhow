package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Problems with invalid values, values that cannot be converted to their destination type.
 */
public abstract class ValueProblem extends PropertyProblem {
	
	public LoaderPropertyCoord getLoaderPropertyCoord() {
		return (LoaderPropertyCoord) propertyCoord;
	}

	@Override
	public String getProblemContext() {

		LoaderPropertyCoord badValueCoord = (LoaderPropertyCoord) propertyCoord;

		String loadDesc = UNKNOWN;

		if (badValueCoord.getLoader() != null &&
				badValueCoord.getLoader().getSpecificLoadDescription() != null) {
			loadDesc = badValueCoord.getLoader().getSpecificLoadDescription();
		}
		return TextUtil.format("Property {} loaded from {}",
					badValueCoord.getPropName(), loadDesc);

	}
	
	public static class InvalidValueProblem<T> extends ValueProblem {
		LoaderPropertyCoord def;
		Validator<T> validator;
		T value;
		
		public InvalidValueProblem(
				Loader loader, Class<?> group, Property<T> prop, 
				T value, Validator<T> validator) {
			propertyCoord = new LoaderPropertyCoord(loader, group, prop);
			this.validator = validator;
			this.value = value;
		}
		
		@Override
		public String getProblemDescription() {
			return validator.getInvalidMessage(value);
		}
	}
	
}
