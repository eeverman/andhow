package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.Alias;
import static yarnandtail.andhow.util.TextUtil.EMPTY_STRING_LIST;
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
		
		List<String> effectiveAliases = effectiveAliases = EMPTY_STRING_LIST;
		
		if (prop.getConfiguredAliases().size() > 0) {
			effectiveAliases = new ArrayList();
			
			List<Alias> aliases = prop.getConfiguredAliases();
			
			for (Alias a : aliases) {
				if (a.isIn()) effectiveAliases.add(a.getName());
			}
		}
		
		Naming naming = new Naming(canonicalName, effectiveAliases);
		return naming;
	}
	
}
