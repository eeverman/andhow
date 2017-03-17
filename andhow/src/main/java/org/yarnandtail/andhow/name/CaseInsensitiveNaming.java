package org.yarnandtail.andhow.name;

import org.yarnandtail.andhow.api.PropertyNaming;
import org.yarnandtail.andhow.api.PropertyGroup;
import org.yarnandtail.andhow.api.Name;
import org.yarnandtail.andhow.api.EffectiveName;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.NamingStrategy;
import java.util.*;
import org.yarnandtail.andhow.*;

/**
 * Case insensitive naming.
 * 
 * @author eeverman
 */
public class CaseInsensitiveNaming implements NamingStrategy {
	
	List<EffectiveName> EMPTY_NAMES = Collections.emptyList();

	@Override
	public PropertyNaming buildNames(Property prop, 
			Class<? extends PropertyGroup> parentGroup) throws Exception {

		String canonName = PropertyGroup.getCanonicalName(parentGroup, prop);
		return buildNamesFromCanonical(prop, parentGroup, canonName);

	}
	
	public PropertyNaming buildNamesFromCanonical(Property prop, 
			Class<? extends PropertyGroup> parentGroup, String canonicalName) {
		
		if (canonicalName == null) return null;
		
		EffectiveName canon = new EffectiveName(canonicalName, toEffectiveName(canonicalName), true, true);
		
		List<EffectiveName> effAliases = EMPTY_NAMES;
		
		if (prop.getRequestedAliases().size() > 0) {
			effAliases = new ArrayList();
			
			List<Name> reqAliases = prop.getRequestedAliases();
			
			for (Name a : reqAliases) {
				
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
	
	@Override
	public String getNameMatchingDescription() {
		return "When reading property names, matching is done in a case insensitive way, " +
				"so 'Bob' would match 'bOB'.";
	}
	
}
