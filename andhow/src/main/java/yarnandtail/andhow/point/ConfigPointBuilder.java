package yarnandtail.andhow.point;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public abstract class ConfigPointBuilder<B extends ConfigPointBuilder, C extends ConfigPoint<T>, T> {
	
	//Weird tracking of its own instance is so we get the type correct when returning
	//to the caller.  Returning 'this' is a ConfigPointBuilder.  Returing 'B' is
	//a specific subclass.
	protected B instance;
	
	
	protected ConfigPointType paramType = ConfigPointType.SINGLE_NAME_VALUE;
	protected ValueType<T> valueType;
	protected T defaultValue;
	protected boolean required = false;
	protected String shortDesc;
	protected List<Validator<T>> validators = new ArrayList();
	protected String helpText;
	protected final List<String> aliases = new ArrayList();
	
	
	
	//All subclasses should have this a static builder() method that returns
	//an appropriate subclass of ConfigPointBuilder.
	
	//All subclasses should have a constructor that looks like this:
//	public [[TYPE]]PointBuilder() {
//		this.instance = this;	//Required to set instance to a correct type
//		this.setValueType([[type]]Type.instance());
//	}
	
	protected void setInstance(B instance) {
		this.instance = instance;
	}
	
	public B setParamType(ConfigPointType paramType) {
		this.paramType = paramType;
		return instance;
	}
	
	/**
	 * This method should be called by subclasses.
	 * If unset, build() will throw an exception.
	 * @param valueType
	 * @return 
	 */
	public B setValueType(ValueType<T> valueType) {
		this.valueType = valueType;
		return instance;
	}
	
	public B setDefault(T defaultValue) {
		this.defaultValue = defaultValue;
		return instance;
	}
		
	public B setRequired(boolean required) {
		this.required = required;
		return instance;
	}
	
	public B required() {
		this.required = true;
		return instance;
	}
	
	public B setDescription(String shortDesc) {
		this.shortDesc = shortDesc;
		return instance;
	}
	
	public B addValidation(Validator<T> validator) {
		validators.add(validator);
		return instance;
	}
	
	public B addValidations(List<Validator<T>> validators) {
		this.validators.addAll(validators);
		return instance;
	}
	
	public B setValidations(List<Validator<T>> validators) {
		this.validators.clear();
		this.validators.addAll(validators);
		return instance;
	}
	
	public B setHelpText(String helpText) {
		this.helpText = helpText;
		return instance;
	}
	
	public B addAlias(String alias) {
		aliases.add(alias);
		return instance;
	}
	
	public B addAliases(List<String> aliases) {
		aliases.addAll(aliases);
		return instance;
	}
	
	public B setAliases(List<String> aliases) {
		this.aliases.clear();
		this.aliases.addAll(aliases);
		return instance;
	}
	
	public abstract C build();
}
