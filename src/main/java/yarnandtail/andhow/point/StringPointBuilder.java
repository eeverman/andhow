package yarnandtail.andhow.point;

import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StringPointBuilder extends ConfigPointBuilder<StringPointBuilder, StringConfigPoint, String> {

	public static StringPointBuilder init() {
		StringPointBuilder b = new StringPointBuilder();
		b.setInstance(b);
		b.setValueType(StringType.instance());
		return b;
	}

	@Override
	public StringConfigPoint build() {

		return new StringConfigPoint(defaultValue, required, shortDesc, 
			paramType, valueType, priv,
			helpText, aliases.toArray(new String[aliases.size()]));

	}
	
}
