package yarnandtail.andhow.property;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.Trimmer;

/**
 * A generic PropertyBuilder class which needs to be fully implemented as an
 * inner class in each Property implementation.
 * 
 * The builder code is intended to be concise and readable in use.  Thus,
 * 'set' or 'add' is dropped from method name prefixes where possible.
 * 
 * For single valued properties, like description, calling description("")
 * sets the value.  For list of values, like validation, calling validation(new Validator(...))
 * would add a validitor to the list.
 * 
 * @author eeverman
 */
public abstract class PropertyBuilderBase<B extends PropertyBuilderBase, P extends Property<T>, T> {
	
	//Weird tracking of its own instance is so we get the type correct when returning
	//to the caller.  Returning 'this' is a PropertyBuilderBase.  Returing 'B' is
	//a specific subclass.
	protected B instance;
	
	protected ValueType<T> _valueType;
	protected Trimmer _trimmer;
	protected T _defaultValue;
	protected boolean _required = false;
	protected String _shortDesc;
	protected List<Validator<T>> _validators = new ArrayList();
	protected String _helpText;
	
	
	
	//All subclasses should have this a static builder() method that returns
	//an appropriate subclass of PropertyBuilderBase.
	
	//All subclasses should have a constructor that looks like this:
	//	public [[TYPE]]Builder() {
	//		this.instance = this;	//Required to set instance to a correct type
	//		this.valueType([[type]]Type.instance());
	//	}
	
	protected void setInstance(B instance) {
		this.instance = instance;
	}
	
	/**
	 * This method should be called by subclasses.
	 * If unset, build() will throw an exception.
	 * @param valueType
	 * @return 
	 */
	public B valueType(ValueType<T> valueType) {
		this._valueType = valueType;
		return instance;
	}
	
	/**
	 * Assigns the whitespace trimmer that is used on the raw value.
	 * 
	 * The default trimmer for each Property subclass should generally be acceptable -
	 * See individual Property instances for details on the defaults.
	 * @param trimmer
	 * @return 
	 */
	public B trimmer(Trimmer trimmer) {
		this._trimmer = trimmer;
		return instance;
	}
	
	/**
	 * 
	 * TODO:  It would be nice to add a String version that can parse
	 * @param defaultValue
	 * @return 
	 */
	public B defaultValue(T defaultValue) {
		this._defaultValue = defaultValue;
		return instance;
	}
		
	public B setRequired(boolean required) {
		this._required = required;
		return instance;
	}
	
	public B required() {
		this._required = true;
		return instance;
	}
	
	/**
	 * Same as description
	 * 
	 * @param shortDesc
	 * @return 
	 */
	public B desc(String shortDesc) {
		return description(shortDesc);
	}
	
	public B description(String shortDesc) {
		this._shortDesc = shortDesc;
		return instance;
	}
	
	/**
	 * Adds a validation to the list of validators,
	 * 
	 * @param validator
	 * @return 
	 */
	public B validation(Validator<T> validator) {
		_validators.add(validator);
		return instance;
	}
	
	/**
	 * Adds a list of Validators to the list of validators being built.
	 * 
	 * @param validators
	 * @return 
	 */
	public B validations(List<Validator<T>> validators) {
		this._validators.addAll(validators);
		return instance;
	}
	
	public B helpText(String helpText) {
		this._helpText = helpText;
		return instance;
	}
	
	/**
	 * Build the Property instance.
	 * 
	 * @return 
	 */
	public abstract P build();
}
