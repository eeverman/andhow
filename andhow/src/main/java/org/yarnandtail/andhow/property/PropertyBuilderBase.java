package org.yarnandtail.andhow.property;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.api.*;

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
	protected boolean _nonNull = false;
	protected String _shortDesc;
	protected List<Validator<T>> _validators = new ArrayList();
	protected List<Name> _aliases = new ArrayList();
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
	 * Assigns a default value for the property.
	 * 
	 * The default value must pass all assigned validation rules, including
 being mustBeNonNull, if set.
	 * 
	 * @param defaultValue
	 * @return 
	 */
	public B defaultValue(T defaultValue) {
		this._defaultValue = defaultValue;
		return instance;
	}
	
	/**
	 * If set, the effective value must be non-null to be considered valid.
	 * 
	 * The effective value is the explicitly configured value, or if that is null,
	 * the default value.
	 * 
	 * @return 
	 */
	public B mustBeNonNull() {
		this._nonNull = true;
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
	
	/**
	 * Adds an alternate name for this property that will be recognized when a
	 * Loader reads a property in from a source, such as JNDI or a properties file.
	 * 
	 * If the alias name already exists, it will add 'in-ness' to it.
	 * Alias names cannot be null, contain whitespace, or these characters:
	 * <code>;/?:@=&"&lt;&gt;>#%{}|\^~[]`</code>
	 * @param name
	 * @return A builder for chaining build calls
	 */
	public B aliasIn(String name) {
		addAlias(new Name(name, true, false), _aliases);
		return instance;
	}
	
	/**
	 * Adds an alternate name for this property that can be used if this
	 * property is exported, such as exporting to System.properties.
	 * 
	 * If the alias name already exists, it will add 'out-ness' to it.
	 * Alias names cannot be null, contain whitespace, or these characters:
	 * <code>;/?:@=&"<>#%{}|\^~[]`</code>
	 * 
	 * @param name
	 * @return A builder for chaining build calls
	 */
	public B aliasOut(String name) {
		addAlias(new Name(name, false, true), _aliases);
		return instance;
	}
	
	/**
	 * Adds an alternate name for this property that will both be recognized
	 * when reading in properties and can be used when exporting properties.
	 * 
	 * If the alias name already exists, it will add 'in' and 'out' to it.
	 * Alias names cannot be null, contain whitespace, or these characters:
	 * <code>;/?:@=&"<>#%{}|\^~[]`</code>
	 * 
	 * @param name
	 * @return A builder for chaining build calls
	 */
	public B aliasInAndOut(String name) {
		addAlias(new Name(name, true, true), _aliases);
		return instance;
	}
	
	/**
	 * Used by public alias methods to actually add the Alias.
	 * 
	 * This method checks to see if the alias already exists and consolidates
	 * aliases of the same name, ORing their in/out settings.
	 * 
	 * The result is that if two aliases are added with the same name, one in and
	 * one out, there will be a single alias that is both in and out.
	 * 
	 * @param newAlias New alias to add
	 * @param addToList The list to add to
	 */
	protected void addAlias(Name newAlias, List<Name> addToList) {
		String name = newAlias.getActualName();
		
		for (int i = 0; i < addToList.size(); i++) {
			Name a = addToList.get(i);
			
			if (a.getActualName().equals(name)) {
				Name b = new Name(name, newAlias.isIn() || a.isIn(), newAlias.isOut() || a.isOut());
				addToList.set(i, b);
				return;
			}
		}
		
		addToList.add(newAlias);
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
