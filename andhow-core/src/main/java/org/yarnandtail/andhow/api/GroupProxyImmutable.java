package org.yarnandtail.andhow.api;

import java.util.*;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public class GroupProxyImmutable extends GroupProxyBase {

	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName
	 * @param javaCanonicalName
	 * @param props
	 */
	public GroupProxyImmutable(String canonicalName, String javaCanonicalName, List<NameAndProperty> props) {
		super(canonicalName, javaCanonicalName, props);
	}
	
	public GroupProxyImmutable(String canonicalName, String javaCanonicalName, 
			List<NameAndProperty> props, boolean userGroup) {
		super(canonicalName, javaCanonicalName, props, userGroup);
	}
	
}
