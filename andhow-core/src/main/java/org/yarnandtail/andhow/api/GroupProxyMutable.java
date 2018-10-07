package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * A modifiable GroupProxy.
 * 
 * @author ericeverman
 */
public class GroupProxyMutable extends GroupProxyBase {

	
	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName The AndHow name
	 * @param javaCanonicalName Java name of the group (inner classes use a $ instead of a dot)
	 */
	public GroupProxyMutable(String canonicalName, String javaCanonicalName) {
		super(canonicalName, javaCanonicalName);
	}
	
	
	public void addProperty(NameAndProperty prop) {
		props.add(prop);
	}
	

}
