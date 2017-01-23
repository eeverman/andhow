package yarnandtail.andhow.name;

import java.util.*;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PropertyNaming.Name;

import static yarnandtail.andhow.util.TextUtil.EMPTY_STRING_LIST;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategy implements NamingStrategy {
	
	List<Name> EMPTY_NAMES = Collections.emptyList();

	@Override
	public PropertyNaming buildNames(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String fieldName) {
		
		//In theory a NamingStrategy could generate a different form of
		//canonicalName, but it would be constrary to what users expect based
		//on the dot.path of a property.
		//if you really need to tack something on to canonical names, add it as
		//an auto-generated alias when the aliases are added, below.
		String canonicalName = parentGroup.getCanonicalName() + "." + fieldName;
		
		return buildNamesFromCanonical(prop, parentGroup, canonicalName);
	}
	
	public PropertyNaming buildNamesFromCanonical(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String canonicalName) {
		
		Name canon = new Name(canonicalName, canonicalName.toUpperCase());
		
		List<Name> inAliases = EMPTY_NAMES;
		
		if (prop.getRequestedAliases().size() > 0) {
			inAliases = new ArrayList();
			
			List<Alias> aliases = prop.getRequestedAliases();
			
			for (Alias a : aliases) {
				if (a.isIn()) inAliases.add(new Name(a.getName(), a.getName().toUpperCase()));
			}
		}
		

		PropertyNaming naming = new PropertyNaming(canon, inAliases);
		return naming;
	}
	
	@Override
	public String transformIncomingClasspathName(String name) {
		if (name != null) {
			return name.toUpperCase();
		} else {
			return null;
		}
	}
	
}
