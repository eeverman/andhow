package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.BolType;

import java.util.List;

/**
 * A {@link Boolean} configuration Property
 * <p>
 * <em>Note: The parsing behavior of this class may change in the 0.5.0 release</em>
 * to have an explicit list of False values.
 * See <a href="https://github.com/eeverman/andhow/issues/658"></a>Issue 658</a>.
 * <p>
 * Parsing values from strings is done by the {@link BolType}.
 * When parsing, the value is considered {@code True} if it case-insensitive matches one of:
 * <ul>
 *   <li>true</li>
 *   <li>t</li>
 *   <li>yes</li>
 *   <li>y</li>
 *   <li>on</li>
 * </ul>
 * <p>
 * If it does not match a value in that list and does not trim to null, it is {@code False}.
 * If the value is null after trimming, the value is considered unset.
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
