package yarnandtail.andhow.point;

import java.util.List;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 * A True/False switch that is never null and behaves similarly to a unix cmd line switch.
 * 
 * If unspecified, a Flag defaults to false, however, the default can be set to
 * true.  Best used for on/off flags, particularly if used from command line,
 * where a <i>null</i> value makes no sense.
 * 
 * A FlagConfigPoint is similar to a Boolean point, but with these differences:
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
public class FlagConfigPoint extends ConfigPointBase<Boolean> {
	
	public FlagConfigPoint() {
		this(null, false, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(Boolean defaultValue, boolean required) {
		this(defaultValue, required, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, null, paramType, valueType, helpText, aliases);
	}
	
	@Override
	public Boolean getValue(AppConfigValues values) {
		Boolean b = super.getValue(values);
		
		if (b != null) {
			return b;
		} else {
			return false;
		}
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}
	
	public static FlagPointBuilder builder() {
		return new FlagPointBuilder();
	}
	
	public static class FlagPointBuilder extends ConfigPointBuilder<FlagPointBuilder, FlagConfigPoint, Boolean> {

		public FlagPointBuilder () {
			instance = this;
			setValueType(FlagType.instance());
		}

		@Override
		public FlagConfigPoint build() {

			return new FlagConfigPoint(defaultValue, required, shortDesc, 
				paramType, valueType,
				helpText, aliases.toArray(new String[aliases.size()]));

		}

	}

}
