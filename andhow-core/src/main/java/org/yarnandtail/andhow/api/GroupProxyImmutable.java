package org.yarnandtail.andhow.api;

import java.util.*;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * A non-modifiable GroupProxy.
 * 
 * @author ericeverman
 */
public class GroupProxyImmutable extends GroupProxyBase {

	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName AndHow name of the group
	 * @param javaCanonicalName Java name of the group (inner classes use a $ instead of a dot)
	 * @param props A list of contained Properties
	 * @param userGroup If true, this is a typical group.
	 *   If false, it is an internal property use to configure AndHow.
	 */
	public GroupProxyImmutable(String canonicalName, String javaCanonicalName, 
			List<NameAndProperty> props, boolean userGroup) {
		super(canonicalName, javaCanonicalName, props, userGroup);
	}
	
}
