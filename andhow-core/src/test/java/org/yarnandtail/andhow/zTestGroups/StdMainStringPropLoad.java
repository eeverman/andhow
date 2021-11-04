package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.AndHowUtil;

import java.util.ArrayList;
import java.util.List;

public class StdMainStringPropLoad {


	public List<String> buildSources(Class<?> clazz, PropExpectations expects, int expectIndex, boolean useAliasIfAvailable) throws IllegalAccessException {
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


			String assignment = expect.getRawStrings().get(expectIndex);
			assignment = assignment.replace("%NAME%", effectiveName);

			args.add(assignment);

		}



		return args;
	}


}
