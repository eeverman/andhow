package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.FlagType;

/**
 * A True/False switch that is never null and behaves similarly to a unix cmd line switch.
 * 
 * If unspecified, a Flag defaults to false, however, the default can be set to
 * true (but not null).  Best used for on/off flags, particularly if used from
 * command line where a <i>null</i> value makes no sense.
 * 
 * A FlagProp is similar to a BolProp, but with these differences:
 * <ul>
 * <li>A Flag is never null - it will always return true or false.  Thus,
 * A FlagProp will never throw a RequirementProblem error during configuration -
 * it always has a value.</li>
 * <li>FlagProp.isNonNullRequired() always returns true, as if mustNotBeNull()
 * was called on the builder.
 * <li>FlagProp.getDefaultValue() never returns null and defaults to false.
 * Via the constructor or builder, it is possible to set the default to be true.
 * In that case a loader would need to find it explicitly set to false to change its value.
 * <li>Loaders will interpret the presence of the flag as setting the flag to
 * True (where possible).  For instance, if a Flag is aliased as <em>-enableTorpedos</em>,
 * including <code>-enableTorpedos</code> as a command line argument will cause
 * the CmdLineLoader to set it to true.</li>
 * </ul>
 * 
 * <h3>The technical details</h3>
 * What a value is assigned to a FlagProp, it is first trimmed by the
 * TrimToNullTrimmer, which removes all whitespace and ultimately turns the value
 * into null if the value is all whitespace.  Since simply being present counts
 * as 'on' for a flag, finding a null value sets the Flag to true.
 * <p>
 * If after trimming the value is not null, it is parsed by
 * {@link org.yarnandtail.andhow.util.TextUtil#toBoolean(java.lang.String)} to
 * determine if the String is considered true or false.
 * 
 * @author eeverman
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
