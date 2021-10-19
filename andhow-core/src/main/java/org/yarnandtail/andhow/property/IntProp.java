package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.IntValidator;
import org.yarnandtail.andhow.valuetype.IntType;

import java.util.List;

/**
 * An {@link Integer} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link IntType}, which parses values using the
 * {@link Integer#parseInt(String)}.  Note that the {@code parseInt} accepts integer numbers
 * with the option of a leading '+' or '-'.
 * <p>
 * Before parsing String values, the {@link TrimToNullTrimmer} is applied by default.
 */
public class IntProp extends PropertyBase<Integer> {

	public IntProp(
			Integer defaultValue, boolean required, String shortDesc, List<Validator<Integer>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<Integer> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code IntProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static IntBuilder builder() {
		return new IntBuilder();
	}

	public static class IntBuilder extends PropertyBuilderBase<IntBuilder, IntProp, Integer> {

		public IntBuilder() {
			instance = this;
			valueType(IntType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public IntProp build() {

			return new IntProp(_defaultValue, _nonNull, _desc, _validators,
					_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}

		/**
		 * @deprecated Use {@code IntBuilder.greaterThan()}
		 */
		@Deprecated
		public IntBuilder mustBeGreaterThan(int reference) {
			return this.greaterThan(reference);
		}

		public IntBuilder greaterThan(int reference) {
			validation(new IntValidator.GreaterThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code IntBuilder.greaterThanOrEqualTo()}
		 */
		@Deprecated
		public IntBuilder mustBeGreaterThanOrEqualTo(int reference) {
			return this.greaterThanOrEqualTo(reference);
		}

		public IntBuilder greaterThanOrEqualTo(int reference) {
			validation(new IntValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code IntBuilder.lessThan()}
		 */
		@Deprecated
		public IntBuilder mustBeLessThan(int reference) {
			return this.lessThan(reference);
		}

		public IntBuilder lessThan(int reference) {
			validation(new IntValidator.LessThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code IntBuilder.lessThanOrEqualTo()}
		 */
		@Deprecated
		public IntBuilder mustBeLessThanOrEqualTo(int reference) {
			return this.lessThanOrEqualTo(reference);
		}

		public IntBuilder lessThanOrEqualTo(int reference) {
			validation(new IntValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
