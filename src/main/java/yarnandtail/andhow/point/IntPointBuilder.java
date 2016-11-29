package yarnandtail.andhow.point;

import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.IntType;

/**
 *
 * @author eeverman
 */
public class IntPointBuilder extends ConfigPointBuilder<IntPointBuilder, IntConfigPoint, Integer> {

	public static IntPointBuilder init() {
		IntPointBuilder b = new IntPointBuilder();
		b.setInstance(b);
		b.setValueType(IntType.instance());
		return b;
	}

	@Override
	public IntConfigPoint build() {

		return new IntConfigPoint(defaultValue, required, shortDesc, validators,
			paramType, valueType,
			helpText, aliases.toArray(new String[aliases.size()]));

	}
	
}
