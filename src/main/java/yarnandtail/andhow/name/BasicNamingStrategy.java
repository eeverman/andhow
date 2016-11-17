package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import static yarnandtail.andhow.ConfigPointUtil.EMPTY_STRING_LIST;
import yarnandtail.andhow.NamingStrategy;

/**
 *
 * @author eeverman
 */
public class BasicNamingStrategy implements NamingStrategy {

	@Override
	public Naming buildNames(ConfigPoint configPoint, 
			Class<? extends ConfigPointGroup> parentGroup, String fieldName) {
		
		String name = parentGroup.getCanonicalName() + "." + fieldName;
		
		List<String> aliasList = EMPTY_STRING_LIST;
		
		if (configPoint.getExplicitBaseName() != null || configPoint.getBaseAliases().size() > 0) {
			aliasList = new ArrayList();
			
			if (configPoint.getExplicitBaseName() != null) {
				aliasList.add(configPoint.getExplicitBaseName());
			}
			
			aliasList.addAll(configPoint.getBaseAliases());
		}
		
		Naming naming = new Naming(name, aliasList);
		return naming;
	}
	
}
