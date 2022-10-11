package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.AndHowTestConfig.AndHowTestConfigImpl;
import org.yarnandtail.andhow.api.Property;

import java.util.ArrayList;
import java.util.List;

public class StdMainStringValueLoader extends PropValueLoader<String> {

	private List<String> args = new ArrayList<>();

	public void addPropertyValue(Property property, String effectiveName,
			String canonName, Object rawValue, boolean verbose) {

		String singleSrcStr = buildSingleSource(effectiveName, rawValue);
		args.add(singleSrcStr);

		if (verbose) {
			System.out.println("Prop " + canonName + " source: [" + singleSrcStr + "]");
		}
	}

	@Override
	public void addExtraValues(List<String> extraValues, boolean verbose) {
		for (Object o : extraValues) {
			args.add(o.toString());
			if (verbose) {
				System.out.println("Adding extra value:  source: [" + o + "]");
			}
		}
	}

	public void completeConfiguration(AndHowTestConfigImpl config) {
		config.setCmdLineArgs(args.toArray(args.toArray(new String[args.size()])));
	}


	public String buildSingleSource(String key, Object rawValue) {

		// NO_VALUE & NO_VALUE_OR_DELIMITER are both valid ways to set a FlagProp,
		// which activated by the presence of the property name on cmd line.

		if (rawValue instanceof RawValueType) {
			RawValueType type = (RawValueType)rawValue;

			switch (type) {
				case NO_VALUE:
					return key + "=";
				case NO_VALUE_OR_DELIMITER:
					return key;
				default:
					throw new IllegalStateException("Unexpected RawValueType: " + type);
			}

		} else {
			return key + "=" + rawValue;
		}
	}

}
