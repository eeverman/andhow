package yarnandtail.andhow.point;

import yarnandtail.andhow.valuetype.FlagType;

/**
 *
 * @author eeverman
 */
public class FlagPointBuilder extends ConfigPointBuilder<FlagPointBuilder, FlagConfigPoint, Boolean> {

	public static FlagPointBuilder init() {
		FlagPointBuilder b = new FlagPointBuilder();
		b.setInstance(b);
		b.setValueType(FlagType.instance());
		return b;
	}

	@Override
	public FlagConfigPoint build() {

		return new FlagConfigPoint(defaultValue, required, shortDesc, 
			paramType, valueType,
			helpText, aliases.toArray(new String[aliases.size()]));

	}
	
}
