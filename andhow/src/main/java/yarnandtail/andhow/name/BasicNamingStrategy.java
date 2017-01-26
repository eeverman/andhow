package yarnandtail.andhow.name;

import java.util.*;
import yarnandtail.andhow.*;

/**
 * Case insensitive naming.
 * 
 * @author eeverman
 */
public class BasicNamingStrategy implements NamingStrategy {
	
	List<EffectiveName> EMPTY_NAMES = Collections.emptyList();

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
		
		EffectiveName canon = new EffectiveName(canonicalName, toEffectiveName(canonicalName), true, true);
		
		List<EffectiveName> effAliases = EMPTY_NAMES;
		
		if (prop.getRequestedAliases().size() > 0) {
			effAliases = new ArrayList();
			
			List<AName> reqAliases = prop.getRequestedAliases();
			
			for (AName a : reqAliases) {
				
				EffectiveName en = new EffectiveName(
						a.getActualName(), toEffectiveName(a.getActualName()),
						a.isIn(), a.isOut());
				
				effAliases.add(en);
			}
		}
		

		PropertyNaming naming = new PropertyNaming(canon, effAliases);
		return naming;
	}
	
	@Override
	public String toEffectiveName(String name) {
		if (name != null) {
			return name.toUpperCase();
		} else {
			return null;
		}
	}
	
}
