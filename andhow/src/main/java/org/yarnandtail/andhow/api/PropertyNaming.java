package org.yarnandtail.andhow.api;

import java.util.*;

/**
 * Names by which a Property is recognized.
 * 
 * NamingStrategy returns an instance of this to create the names for
 * a property.  Names here are the canonical name and any aliases.
 * 
 * @author ericeverman
 */
public class PropertyNaming {

	private final EffectiveName canonicalName;
	private final List<EffectiveName> aliases;

	public PropertyNaming(EffectiveName canonicalName, List<EffectiveName> aliases) {
		this.canonicalName = canonicalName;
		
		if (aliases != null) {
			this.aliases = Collections.unmodifiableList(aliases);
		} else {
			this.aliases = Collections.emptyList();
		}
	}

	/**
	 * The canonical name of the property.
	 * @return 
	 */
	public EffectiveName getCanonicalName() {
		return canonicalName;
	}

	/**
	 * All aliases for the property.
	 * 
	 * The naming strategy may be directed to add or remove aliases from the ones
	 * requested for a property to resolve conflicts, but nominally this is the
	 * list of aliases (both in and out aliases) requested by the Property.
	 * @return 
	 */
	public List<EffectiveName> getAliases() {
		return aliases;
	}
	
	/**
	 * A list of the in-type aliases.
	 * 
	 * @return 
	 */
	public List<EffectiveName> getInAliases() {
		
		ArrayList<EffectiveName> ns = new ArrayList();
		aliases.stream().filter(a -> a.isIn()).forEachOrdered(a -> ns.add(a));
		return Collections.unmodifiableList(ns);
	}
	
	/**
	 * A list of the out-type aliases.
	 * 
	 * @return 
	 */
	public List<EffectiveName> getOutAliases() {
		
		ArrayList<EffectiveName> ns = new ArrayList();
		aliases.stream().filter(a -> a.isOut()).forEachOrdered(a -> ns.add(a));
		return Collections.unmodifiableList(ns);
	}

}
