package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.AndHowUtil;

import java.util.ArrayList;
import java.util.List;

public class StdMainStringPropLoad {

	public String buildSingleSource(String key, String value) {

		// NO_VALUE & NO_VALUE_OR_DELIMITER are both valid ways to set a FlagProp, which activated by
		// the presence of the property name on cmd line.

		if (value.equals(RawValueType.NO_VALUE.toString())) {
			return key + "=";
		} else if (value.equals(RawValueType.NO_VALUE_OR_DELIMITER.toString())) {
			return key;
		} else {
			return key + "=" + value;
		}
	}

	public List<String> buildSources(Class<?> clazz, PropExpectations expects,
			int expectIndex, boolean useAliasIfAvailable, boolean verbose) throws IllegalAccessException {

		List<String> args = new ArrayList();

		GroupProxy proxy = AndHowUtil.buildGroupProxy(clazz);


		for (PropExpectations.PropExpectation expect : expects.getExpectations()) {

			String propCanonName = proxy.getCanonicalName(expect.getProperty());

			if (propCanonName == null) {
				throw new IllegalStateException(
						"The property in the passed group:" +
								"\n Group canonical name: " + clazz.getCanonicalName() +
								"\n PropExpectation toString: " + expect.toString());
			}

			String effectiveName = propCanonName;

			if (useAliasIfAvailable && expect.getProperty().getInAliases().size() > 0) {
				effectiveName = expect.getProperty().getInAliases().get(0);
			}

			String rawValString = expect.getRawStrings().get(expectIndex);


			if (rawValString != null && ! rawValString.equals(RawValueType.SKIP.toString())) {

				String sourceStr = buildSingleSource(effectiveName, rawValString);
				args.add(sourceStr);

				if (verbose) {
					System.out.println("Prop " + propCanonName + " source: [" + sourceStr + "]");
				}


			} else {
				// Skipping this one (likely testing what happens when its missing)
				if (verbose) {
					System.out.println("Prop " + propCanonName + " SKIPPED!  (As requested)");
				}
			}


		}

		return args;
	}




}
