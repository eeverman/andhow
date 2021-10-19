package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.FlagType;

/**
 * A True/False switch that is never null and behaves similarly to a 'nix cmd line switch.
 * <p>
 * This would be used for command line arguments that are true by being presence, e.g.:<br>
 * {@code java MyClass launch}<br>
 * If {@code launch} is the name or alias of a {@code FlagProp}, launch will true by its presence,
 * without requiring {@code launch=true} (although that will also work).
 * <p>
 * <em>NOTE:  The behavior of this class may be changing</em><br>
 * The existing behavior causes unexpected values in properties files and will likely
 * <a href="https://github.com/eeverman/andhow/issues/656">change in the 0.5.0 release</a>
 * so that it behaves like a BolProp everywhere except on command line.
 * <p>
 * If unspecified, a Flag defaults to false, however, the default can be set to
 * true (but not to null).
 * <p>
 * A FlagProp is similar to a BolProp, but with these differences:
 * <ul>
 * <li>A Flag is never null - it will always return true or false.  Thus,
 * A FlagProp will never throw a RequirementProblem error during configuration -
 * it always has a value.</li>
 * <li>FlagProp.getDefaultValue() never returns null and defaults to false.
 * Via the constructor or builder, it is possible to set the default to be true.
 * In that case a loader would need to find it explicitly set to false to change its value,
 * e.g. {@code propertyName=false}.
 * <li>Currently (prior to 0.5.0), referencing a FlagProp in any configuration source (not just
 * command line args) will set it to true.  For instance, a 'launch' flag will be set true simply
 * by including:<br>
 * {@code launch =}<br>
 * in a properties file.  That will likely change in the 0.5.0 release (see note above).</li>
 * </ul>
 *
 * <h3>The technical details</h3>
 * When the name refering to a FlagProp is found, the value is first trimmed by the
 * TrimToNullTrimmer, which removes all whitespace and ultimately turns the value
 * into null if the value is all whitespace.  Since simply being present counts
 * as 'true' for a flag, finding a null value sets the Flag to true.
 * <p>
 * If after trimming the value is not null, it is parsed by
 * {@link org.yarnandtail.andhow.util.TextUtil#toBoolean(java.lang.String)} to
 * determine if the String is considered true or false.
 */
public class FlagProp extends PropertyBase<Boolean> {

	public FlagProp(
			Boolean defaultValue, String shortDesc, List<Name> aliases,
			PropertyType paramType, ValueType<Boolean> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, true, shortDesc, null, aliases, paramType, valueType, trimmer, helpText);
	}

	@Override
	public Boolean getValue() {
		Boolean b = super.getValue();

		if (b != null) {
			return b;
		} else {
			return false;
		}
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code FlagProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static FlagBuilder builder() {
		return new FlagBuilder();
	}

	public static class FlagBuilder extends PropertyBuilderBase<FlagBuilder, FlagProp, Boolean> {

		public FlagBuilder () {
			instance = this;
			valueType(FlagType.instance());
			trimmer(TrimToNullTrimmer.instance());
			_defaultValue = false;	//always assumed to be false unless speced otherwise.
			_nonNull = true;	//always non-null, though the constructor doesn't even use this
		}

		@Override
		public FlagProp build() {

			return new FlagProp(_defaultValue, _desc, _aliases,
				PropertyType.FLAG, _valueType, _trimmer, _helpText);

		}

	}

}
