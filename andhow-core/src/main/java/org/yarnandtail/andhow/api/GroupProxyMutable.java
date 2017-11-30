package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public class GroupProxyMutable extends GroupProxyBase {

	
	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName
	 * @param javaCanonicalName
	 */
	public GroupProxyMutable(String canonicalName, String javaCanonicalName) {
		super(canonicalName, javaCanonicalName);
	}
	
	public GroupProxyMutable(String canonicalName, String javaCanonicalName, boolean userGroup) {
		super(canonicalName, javaCanonicalName, userGroup);
	}
	
	public void addProperty(NameAndProperty prop) {
		props.add(prop);
	}
	

}
