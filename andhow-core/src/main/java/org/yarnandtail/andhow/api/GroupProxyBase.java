package org.yarnandtail.andhow.api;

import java.util.*;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public abstract class GroupProxyBase implements GroupProxy {

	private final String canonicalName;
	private final String javaCanonicalName;
	protected final List<NameAndProperty> props;

	/**
	 * Constructs a new instance w/ final unmodifiable fields.
	 * 
	 * @param canonicalName
	 * @param javaCanonicalName
	 */
	public GroupProxyBase(String canonicalName, String javaCanonicalName) {
		this.canonicalName = canonicalName;
		this.javaCanonicalName = javaCanonicalName;
		props = new ArrayList();
	}
	
	public GroupProxyBase(String canonicalName, String javaCanonicalName, List<NameAndProperty> props) {
		this.canonicalName = canonicalName;
		this.javaCanonicalName = javaCanonicalName;
		this.props = props;
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

	@Override
	public Class<?> getProxiedGroup() {
		try {
			String jcan = getJavaCanonicalName();
			return Class.forName(jcan);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("The configured PropertyGroup '" + getJavaCanonicalName() + "' was not found on the classpath", ex);
		}
	}

	@Override
	public String getSimpleName(Property<?> memberProperty) {
		for (NameAndProperty nap : props) {
			if (nap.property.equals(memberProperty)) {
				return nap.fieldName;
			}
		}
		return null;
	}

	@Override
	public String getCanonicalName(Property<?> memberProperty) {
		for (NameAndProperty nap : props) {
			if (nap.property.equals(memberProperty)) {
				return canonicalName + "." + nap.fieldName;
			}
		}
		return null;
	}

}
