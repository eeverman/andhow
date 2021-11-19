package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Property;

import java.util.*;

public abstract class MapValueLoader extends PropValueLoader<AbstractMap.SimpleEntry<String, String>> {
	protected Map<String, String> args = new HashMap<>();

	public void addPropertyValue(Property property, String effectiveName,
			String canonName, Object rawValue, boolean verbose) {

		if (rawValue instanceof RawValueType) {
			RawValueType type = (RawValueType)rawValue;

			switch (type) {
				case NO_VALUE:
				case NO_VALUE_OR_DELIMITER:

					args.put(effectiveName, "");

					if (verbose) {
						System.out.println("Prop " + canonName + " [ Placing an empty value by request ]");
					}

					break;

				default:
					throw new IllegalStateException("Unexpected RawValueType: " + type);
			}

		} else {
			args.put(effectiveName, rawValue.toString());

			if (verbose) {
				System.out.println("Prop " + canonName + " source: [" + effectiveName + "/" + rawValue + "]");
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
