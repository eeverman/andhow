package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.Loader;
import org.yarnandtail.andhow.Problem;
import org.yarnandtail.andhow.Property;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.Validator;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Problems with invalid values, values that cannot be converted to their destination type.
 * 
 * @author eeverman
 */
public abstract class ValueProblem extends Problem {
	
	protected ValueCoord badValueCoord;
	
	public ValueCoord getBadValueCoord() {
		return badValueCoord;
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
		ValueCoord def;
		Validator<T> validator;
		T value;
		
		public InvalidValueProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property<T> prop, 
				T value, Validator<T> validator) {
			badValueCoord = new ValueCoord(loader, group, prop);
			this.validator = validator;
			this.value = value;
		}
		
		@Override
		public String getProblemDescription() {
			return validator.getInvalidMessage(value);
		}
	}
	
}
