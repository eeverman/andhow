package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ericeverman
 */
public class SampleGlobalPropertyGroupStub extends GlobalPropertyGroupStub {

	@Override
	public String getSimpleRootName() {
		return SampleNestedPropGroup.class.getSimpleName();
	}
	
	@Override
	public String[][] getGroupPaths() {
		return new String[][] {
			{}, {"Nest1", "Config"}, {"Nest2", "Config"},
		};
	}
	

}
