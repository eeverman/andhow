package yarnandtail.andhow.name;

import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.NamingStrategy;

/**
 * A RISKY and NON-PRODUCTION strategy that uses aliases as-is w/o any prefix.
 * 
 * This is a bad idea because its very likely that it will causes naming collisions,
 * especially as the application grows or is used as a library in a larger project.
 * 
 * The real purpose of this strategy is to unit test the effect of naming collisions
 * and (possibly) for smaller projects that have just a handful of Properties.
 * 
 * @author eeverman
 */
public class AsIsAliasNamingStrategy implements NamingStrategy {

	@Override
	public Naming buildNames(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String fieldName) {
		
		String canonicalName = parentGroup.getCanonicalName() + "." + fieldName;
		Naming naming = new Naming(canonicalName, prop.getBaseAliases());
		return naming;
	}
	
	@Override
	public Naming buildNamesFromCanonical(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String canonicalName) {
		
		Naming naming = new Naming(canonicalName, prop.getBaseAliases());
		return naming;
	}
	
}
