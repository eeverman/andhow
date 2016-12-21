package yarnandtail.andhow.property;

import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Trimmer;
import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.ValueMap;

/**
 * A True/False switch that is never null and behaves similarly to a unix cmd line switch.
 * 
 * If unspecified, a Flag defaults to false, however, the default can be set to
 * true.  Best used for on/off flags, particularly if used from command line,
 * where a <i>null</i> value makes no sense.
 * 
 * A FlagProp is similar to a Boolean Property, but with these differences:
 * <ul>
 * <li>A Flag is never null - it will always return true or false.</li>
 * <li>Loaders will interpret the presence of the flag as setting the flag to
 * True (where possible).  For instance, if a Flag is aliased as <em>-enableTorpedos</em>,
 * including <code>-enableTorpedos</code> as a command line argument will cause
 * the CmdLineLoader to set it to true.</li>
 * </ul>
 * 
 * Explicitly setting a flag true or false on the command line is allowed, such
 * as <code>-enableTorpedos=true</code>.
 * 
 * @author eeverman
 */
public class FlagProp extends PropertyBase<Boolean> {
	
	public FlagProp(
			Boolean defaultValue, boolean required, String shortDesc,
			PropertyType paramType, ValueType<Boolean> valueType, Trimmer trimmer,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, null, paramType, valueType, trimmer, helpText, aliases);
	}
	
	@Override
	public Boolean getValue(ValueMap values) {
		Boolean b = super.getValue(values);
		
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
		}

		@Override
		public FlagProp build() {

			return new FlagProp(_defaultValue, _required, _shortDesc, 
				_paramType, _valueType, _trimmer,
				_helpText, _aliases.toArray(new String[_aliases.size()]));

		}

	}

}
