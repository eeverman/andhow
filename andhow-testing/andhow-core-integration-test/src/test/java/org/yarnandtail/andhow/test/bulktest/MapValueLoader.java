package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Property;

import java.util.*;

public abstract class MapValueLoader extends PropValueLoader<AbstractMap.SimpleEntry<String, String>> {
	protected Map<String, String> args = new HashMap<>();

	public void addPropertyValue(Property property, String effectiveName,
			String canonName, String rawValString, boolean verbose) {

		if (rawValString.equals(RawValueType.NO_VALUE.toString()) ||
				rawValString.equals(RawValueType.NO_VALUE_OR_DELIMITER.toString())) {

			args.put(effectiveName, "");

			if (verbose) {
				System.out.println("Prop " + canonName + " [ Placing an empty value by request ]");
			}

		} else {

			args.put(effectiveName, rawValString);

			if (verbose) {
				System.out.println("Prop " + canonName + " source: [" + effectiveName + "/" + rawValString + "]");
			}
		}

	}

	@Override
	public void addExtraValues(List<AbstractMap.SimpleEntry<String, String>> extraValues, boolean verbose) {
		for (AbstractMap.SimpleEntry e : extraValues) {
			args.put((String) e.getKey(), (String) e.getValue());
			if (verbose) {
				System.out.println(
						"Adding extra value:  key/value: [" + e.getKey() + "/" + e.getValue() + "]");
			}
		}
	}
}
