package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import static yarnandtail.andhow.Property.EMPTY_STRING_LIST;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.NamingStrategy;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategy implements NamingStrategy {

	@Override
	public Naming buildNames(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String fieldName) {
		
		String canonicalName = parentGroup.getCanonicalName() + "." + fieldName;
		
		return buildNamesFromCanonical(prop, parentGroup, canonicalName);
	}
	
	@Override
	public Naming buildNamesFromCanonical(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String canonicalName) {
		
		List<String> effectiveAliases = null;
		
		if (prop.getBaseAliases().size() > 0) {
			List<String> aliases = prop.getBaseAliases();
			effectiveAliases = new ArrayList(aliases.size());
			
			for (String a : aliases) {
				effectiveAliases.add(parentGroup.getCanonicalName() + "." + a);
			}
		} else {
			effectiveAliases = EMPTY_STRING_LIST;
		}

		
		Naming naming = new Naming(canonicalName, effectiveAliases);
		return naming;
	}
	
}
