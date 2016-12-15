package yarnandtail.andhow;

/**
 * Problems with invalid values, values that cannot be converted to their destination type.
 * 
 * @author eeverman
 */
public abstract class ValueProblem extends Problem {
	
	ValueCoord propertyValueDef;
	
	public ValueProblem() {
		
	}
	
	public ValueCoord getPropertyValueDef() {
		return propertyValueDef;
	}
	
	public static class InvalidValueProblem<T> extends ValueProblem {
		ValueCoord def;
		Validator<T> validator;
		T value;
		
		public InvalidValueProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property<T> prop, 
				T value, Validator<T> validator) {
			propertyValueDef = new ValueCoord(loader, group, prop);
			this.validator = validator;
			this.value = value;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("Property {} loaded from {}", 
						getPropertyValueDef().getName(), 
						getPropertyValueDef().getLoader().getSpecificLoadDescription());
		}
		
		@Override
		public String getProblemDescription() {
			return validator.getInvalidMessage(value);
		}
	}
	
	public static class StringConversionValueProblem extends ValueProblem {
		String str;
		
		public StringConversionValueProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property prop, 
				String str) {
			
			propertyValueDef = new ValueCoord(loader, group, prop);
			this.str = str;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("Property {} loaded from {}", 
						getPropertyValueDef().getName(), 
						getPropertyValueDef().getLoader().getSpecificLoadDescription());
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
					"The string '{}' could not be converted to type {}",
					(str!=null)?str:TextUtil.NULL_PRINT, 
					this.propertyValueDef.property.getValueType().getDestinationType().getSimpleName());
		}
	}
	
	public static class ObjectConversionValueProblem extends ValueProblem {
		Object obj;
		
		public ObjectConversionValueProblem(
				Loader loader, Class<? extends PropertyGroup> group, Property prop, 
				Object obj) {
			
			propertyValueDef = new ValueCoord(loader, group, prop);
			this.obj = obj;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("Property {} loaded from {}", 
						getPropertyValueDef().getName(), 
						getPropertyValueDef().getLoader().getSpecificLoadDescription());
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
					"The object '{}' could not be converted to type {}",
					(obj!=null)?obj:TextUtil.NULL_PRINT,
					this.propertyValueDef.property.getValueType().getDestinationType().getSimpleName());
		}
	}
	
}
