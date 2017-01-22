package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;

import static yarnandtail.andhow.util.TextUtil.EMPTY_STRING_LIST;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategy implements NamingStrategy {

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
	
	@Override
	public String transformIncomingClasspathName(String name) {
		if (name != null) {
			return name.toUpperCase();
		} else {
			return null;
		}
	}
	
	public PropertyNaming buildNamesFromCanonical(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String canonicalName) {
		
		List<String> inAliases = inAliases = EMPTY_STRING_LIST;
		
		if (prop.getRequestedAliases().size() > 0) {
			inAliases = new ArrayList();
			
			List<Alias> aliases = prop.getRequestedAliases();
			
			for (Alias a : aliases) {
				if (a.isIn()) inAliases.add(a.getName());
			}
		}
		
		ArrayList<String> allNames = new ArrayList();
		allNames.add(canonicalName.toUpperCase());
		inAliases.stream().forEachOrdered(a -> allNames.add(a.toUpperCase()));
		
		PropertyNaming naming = new PropertyNaming(canonicalName, inAliases, allNames);
		return naming;
	}
	
}
