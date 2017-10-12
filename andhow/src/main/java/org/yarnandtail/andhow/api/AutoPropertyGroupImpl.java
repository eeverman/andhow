package org.yarnandtail.andhow.api;

import java.util.*;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public class AutoPropertyGroupImpl implements AutoPropertyGroup {

	private final String canonicalName;
	private final String javaCanonicalName;
	private final List<NameAndProperty> props = new ArrayList();
	
	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName
	 * @param javaCanonicalName
	 */
	public AutoPropertyGroupImpl(String canonicalName, String javaCanonicalName) {
		this.canonicalName = canonicalName;
		this.javaCanonicalName = javaCanonicalName;
	}
	
	public void addProperty(NameAndProperty prop) {
		props.add(prop);
	}
	
	@Override
	public String getCanonicalName() {
		return canonicalName;
	}

	@Override
	public String getJavaCanonicalName() {
		return javaCanonicalName;
	}

	@Override
	public List<NameAndProperty> getProperties() {
		return Collections.unmodifiableList(props);
	}

}
