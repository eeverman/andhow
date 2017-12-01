package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.BolType;

/**
 * A True/False value that can be null, just like a Java Boolean Object.
 * 
 * If after trimming the value is not null, it is parsed by
 * {@link org.yarnandtail.andhow.util.TextUtil#toBoolean(java.lang.String)} to
 * determine if the String is considered true or false.
 * <p>
 * If the value is null after trimming, the value is considered null.
 * 
 * @author eeverman
 */
public class BolProp extends PropertyBase<Boolean> {
	
	public BolProp(
			Boolean defaultValue, boolean nonNull, String shortDesc, List<Name> aliases,
			PropertyType paramType, ValueType<Boolean> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, nonNull, shortDesc, null, aliases, paramType, valueType, trimmer, helpText);
	}
	
	public static BolBuilder builder() {
		return new BolBuilder();
	}
	
	public static class BolBuilder extends PropertyBuilderBase<BolBuilder, BolProp, Boolean> {

		public BolBuilder () {
			instance = this;
			valueType(BolType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public BolProp build() {

			return new BolProp(_defaultValue, _nonNull, _desc, _aliases,
				PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}

	}

}
