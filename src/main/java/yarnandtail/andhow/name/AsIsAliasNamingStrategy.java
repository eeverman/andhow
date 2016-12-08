package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import static yarnandtail.andhow.ConfigPoint.EMPTY_STRING_LIST;
import yarnandtail.andhow.NamingStrategy;

/**
 * A RISKY and NON-PRODUCTION strategy that uses aliases as-is w/o any prefix.
 * 
 * This is a bad idea because its very likely that it will causes naming collisions,
 * especially as the application grows or is used as a library in a larger project.
 * 
 * The real purpose of this strategy is to unit test the effect of naming collisions
 * and (possibly) for smaller projects that have just a handful of ConfigPoints.
 * 
 * @author eeverman
 */
public class AsIsAliasNamingStrategy implements NamingStrategy {

	@Override
	public Naming buildNames(ConfigPoint configPoint, 
			Class<? extends ConfigPointGroup> parentGroup, String fieldName) {
		
		String canonicalName = parentGroup.getCanonicalName() + "." + fieldName;
		Naming naming = new Naming(canonicalName, configPoint.getBaseAliases());
		return naming;
	}
	
	@Override
	public Naming buildNamesFromCanonical(ConfigPoint configPoint, 
			Class<? extends ConfigPointGroup> parentGroup, String canonicalName) {
		
		Naming naming = new Naming(canonicalName, configPoint.getBaseAliases());
		return naming;
	}
	
}
