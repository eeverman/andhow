package yarnandtail.andhow.point;

import yarnandtail.andhow.ConfigPointType;
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
		this(null, false, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(Boolean defaultValue, boolean required) {
		this(defaultValue, required, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType, boolean priv,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, paramType, valueType, priv, helpText, aliases);
	}
	
	/**
	 * Override to provide the non-null behavior of the flag.
	 * @return 
	 */
	@Override
	public Boolean getValue() {
		Boolean b = super.getValue();
		
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

}
