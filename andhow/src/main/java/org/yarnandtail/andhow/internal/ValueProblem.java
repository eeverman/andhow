package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 * Problems with invalid values, values that cannot be converted to their destination type.
 * 
 * @author eeverman
 */
public abstract class ValueProblem implements Problem {
	
	protected LoaderValueCoord badValueCoord;
	
	public LoaderValueCoord getBadValueCoord() {
		return badValueCoord;
	}
	
	
	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
	
	@Override
	public String getProblemContext() {
		
		if (badValueCoord != null) {
			
			String loadDesc = UNKNOWN;
			
			if (badValueCoord.getLoader() != null && 
					badValueCoord.getLoader().getSpecificLoadDescription() != null) {
				loadDesc = badValueCoord.getLoader().getSpecificLoadDescription();
			}
			return TextUtil.format("Property {} loaded from {}", 
						badValueCoord.getPropName(), loadDesc);
		} else {
			return UNKNOWN;
		}
	}
	
	public static class InvalidValueProblem<T> extends ValueProblem {
		LoaderValueCoord def;
		Validator<T> validator;
		T value;
		
		public InvalidValueProblem(
				Loader loader, Class<?> group, Property<T> prop, 
				T value, Validator<T> validator) {
			badValueCoord = new LoaderValueCoord(loader, group, prop);
			this.validator = validator;
			this.value = value;
		}
		
		@Override
		public String getProblemDescription() {
			return validator.getInvalidMessage(value);
		}
	}
	
}
