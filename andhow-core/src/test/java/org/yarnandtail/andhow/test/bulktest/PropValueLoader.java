package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.AndHowTestConfig.AndHowTestConfigImpl;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.AndHowUtil;

import java.util.List;

public abstract class PropValueLoader {

	public abstract void addPropertyValue(Property property, String effectiveName,
			String canonName, String rawValString, boolean verbose);

	/**
	 * Inject non-recognized values into the loader.
	 *
	 * Used for testing unrecognized values being encountered by the Loader.
	 * Many loaders are supposed to ignore them, others are supposed to complain.
	 * @param extraValues
	 * @param verbose
	 */
	public abstract void addExtraValues(List<Object> extraValues, boolean verbose);

	public abstract void completeConfiguration(AndHowTestConfigImpl config);

	public void buildSources(AndHowTestConfigImpl config, List<PropExpectations> expectList, int expectIndex,
			boolean useAliasIfAvailable, List<Object> extraValues, boolean verbose) throws IllegalAccessException {

		for (PropExpectations expects : expectList) {

			GroupProxy proxy = AndHowUtil.buildGroupProxy(expects.getClazz());

			for (PropExpectations.PropExpectation expect : expects.getExpectations()) {

				String propCanonName = proxy.getCanonicalName(expect.getProperty());

				if (propCanonName == null) {
					throw new IllegalStateException(
							"Unable to find a canonical name for a property in the passed group:" +
									"\n Group canonical name: " + expects.getClazz().getCanonicalName() +
									"\n PropExpectation toString: " + expect.toString());
				}

				String effectiveName = propCanonName;

				if (useAliasIfAvailable) {
					List<Name> aliases = expect.getProperty().getRequestedAliases();
					Name alias = aliases.stream().filter(n -> n.isIn()).findFirst().orElse(null);

					if (alias != null) {
						effectiveName = alias.getActualName();
					}
				}

				String rawValString = expect.getRawStrings().get(expectIndex);

				if (rawValString != null && !rawValString.equals(RawValueType.SKIP.toString())) {

					addPropertyValue(expect.getProperty(), effectiveName, propCanonName, rawValString, verbose);

				} else {
					// Skipping this one (likely testing what happens when its missing)
					if (verbose) {
						System.out.println("Prop " + propCanonName + " SKIPPED!  (As requested)");
					}
				}
			}
		}

		if (extraValues != null && extraValues.size() > 0) {
			addExtraValues(extraValues, verbose);
		}

		completeConfiguration(config);
	}
}
