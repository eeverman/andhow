package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;

/**
 * Location of a Property value, as loaded by a specific loader..
 * <p>
 * The actual value itself is not included b/c its type varies depending on
 * the context of the problem and it is at least useful to unify where the
 * value came from, if not the value itself.
 *
 */
public class LoaderPropertyCoord extends PropertyCoord {

	Loader loader;

	public LoaderPropertyCoord(Loader loader, Class<?> group, Property<?> prop) {
		super(group, prop);
		this.loader = loader;
	}

	/**
	 * The loader attempting to load this property, if that can be determined.
	 * @return May return null.
	 */
	public Loader getLoader() {
		return loader;
	}
	
}
