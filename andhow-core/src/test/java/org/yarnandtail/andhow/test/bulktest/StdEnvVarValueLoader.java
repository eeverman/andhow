package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.AndHowTestConfig.AndHowTestConfigImpl;
import org.yarnandtail.andhow.api.Property;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class StdEnvVarValueLoader extends PropValueLoader<SimpleEntry<String, String>> {

	private Map<String, String> args = new HashMap<>();

	public void addPropertyValue(Property property, String effectiveName,
			String canonName, String rawValString, boolean verbose) {

		if (rawValString.equals(RawValueType.NO_VALUE.toString()) ||
				rawValString.equals(RawValueType.NO_VALUE_OR_DELIMITER.toString())) {

			// Env Vars have no delimiters and cannot contain a null value, so the only corollary
			// to NO_VALUE or NO_VALUE_OR_DELIMITER is just to skip the value.  Otherwise
			// it would be an empty string value, which currently the EnvVar loader does not trim.

			if (verbose) {
				System.out.println("Prop " + canonName + " [ !!SKIPPING by request!! ]");
			}

		} else {

			args.put(effectiveName, rawValString);

			if (verbose) {
				System.out.println("Prop " + canonName + " source: [" + effectiveName + "/" + rawValString + "]");
			}
		}

	}

	@Override
	public void addExtraValues(List<SimpleEntry<String, String>> extraValues, boolean verbose) {
		for (SimpleEntry e : extraValues) {
			args.put((String)e.getKey(), (String)e.getValue());
			if (verbose) {
				System.out.println(
						"Adding extra value:  key/value: [" + e.getKey() + "/" + e.getValue() + "]");
			}
		}
	}

	public void completeConfiguration(AndHowTestConfigImpl config) {
		config.setEnvironmentProperties(args);
	}

}
