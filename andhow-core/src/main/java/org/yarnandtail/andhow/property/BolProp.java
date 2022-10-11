package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.BolType;

import java.util.List;

/**
 * A {@link Boolean} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link BolType}.
 * When parsing, a string is considered {@code True} if it matches one of the 'true-ish' values or
 * {@code false} if it matches one of the 'false-ish' values (case-insensitive).
 * Recognized {@code True} strings:
 * <ul>
 *   <li>true</li>
 *   <li>t</li>
 *   <li>yes</li>
 *   <li>y</li>
 *   <li>on</li>
 * </ul>
 * <p>
 * Recognized {@code False} strings:
 * <ul>
 *   <li>false</li>
 *   <li>f</li>
 *   <li>no</li>
 *   <li>n</li>
 *   <li>off</li>
 * </ul>
 * Unrecognized values will throw a {@code RuntimeException} at startup, values that are empty or
 * have been trimmed to empty are considered null.
 * <p>
 * <em>Note: The parsing behavior of this class changed in the 0.5.0 release</em>
 * to have an explicit list of {@code False} values.
 * See <a href="https://github.com/eeverman/andhow/issues/658"></a>Issue 658</a>.
 */
public class BolProp extends PropertyBase<Boolean> {

	public BolProp(
			Boolean defaultValue, boolean nonNull, String shortDesc, List<Name> aliases,
			PropertyType paramType, ValueType<Boolean> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, nonNull, shortDesc, null, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as: {@code BolProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static BolBuilder builder() {
		return new BolBuilder();
	}

	public static class BolBuilder extends PropertyBuilderBase<BolBuilder, BolProp, Boolean> {

		public BolBuilder() {
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
