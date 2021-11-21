package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valuetype.FlagType;

/**
 * A True/False switch that is never null and behaves similarly to a 'nix cmd line switch.
 * <p>
 * Use a FlagProp when you want a command line argument to turn 'on' just by being present, e.g.:<br>
 * {@code java MyClass enableAwesomeMode}<br>
 * If {@code enableAwesomeMode} is the name or alias of a {@code FlagProp}, that FlagProp will set
 * {@code True} simply by having its name as an argument.  You can also explicitly set its value,
 * e.g. {@code enableAwesomeMode=true} or {@code enableAwesomeMode=false}.  When set to a value,
 * a FlagProp behaves exactly like a {@link BolProp}.
 * <p>
 * <em>Note:</em> A FlagProp has it's special <i>present == true</i> behavior <strong>only</strong>
 * for the command line loader (i.e. {@link org.yarnandtail.andhow.load.std.StdMainStringArgsLoader}.
 * <em>(see note below)</em>
 * <p>
 * A FlagProp is similar to a BolProp, but with these differences:
 * <ul>
 * <li>A Flag is never null - {@code FlagProp.getValue()} always return true or false.  Thus,
 * A FlagProp will never throw a RequirementProblem error during configuration because
 * it always has a value.</li>
 * <li>FlagProp.getDefaultValue() never returns null and defaults to false.
 * Via the constructor or builder, it is possible to set the default to be true.
 * In that case a loader would need to find it explicitly set to false to change its value,
 * e.g. {@code propertyName=false}.
 * </ul>
 *
 * <h3>The technical details</h3>
 * If no name is found by any loader that refers to the FlagProp, it defaults to false.
 * If a name referring to a FlagProp is found in any loader other than the command line loader,
 * the value is parsed and handled exactly as if it were a BolProp.
 * If a name referring to a FlagProp is found on command line loader:
 * <ul>
 * <li>If the argument is of the form {@code key=value} and the value contains any non-whitespace,
 * the value is handled just as a BolProp would handle the value.</li>
 * <li>If the value is all whitespace or there is no '=' delimiter, the presence of the key
 * without a value set the FlagProp to True.</li>
 * </ul>
 * <p>
 * <em>NOTE:  The behavior of the FlagProp changed in release 0.5.0</em><br>
 * Prior to 0.5.0, FlagProps' special <i>present == true</i> behavior applied to all loaders,
 * so a properties file that included the name of a FlagProp with no value would set it True.
 * This non-desirable behavior <a href="https://github.com/eeverman/andhow/issues/656">was fixed
 * for the 0.5.0 release</a>.
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
