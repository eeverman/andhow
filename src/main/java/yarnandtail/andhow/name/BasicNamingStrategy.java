package yarnandtail.andhow.name;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.ConfigPointUtil;
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
		
		String canonicalName = parentGroup.getCanonicalName() + "." + fieldName;
		
		List<String> effectiveAliases = null;
		
		if (configPoint.getBaseAliases().size() > 0) {
			List<String> aliases = configPoint.getBaseAliases();
			effectiveAliases = new ArrayList(aliases.size());
			
			for (String a : aliases) {
				effectiveAliases.add(parentGroup.getCanonicalName() + "." + a);
			}
		} else {
			effectiveAliases = ConfigPointUtil.EMPTY_STRING_LIST;
		}

		
		Naming naming = new Naming(canonicalName, effectiveAliases);
		return naming;
	}
	
}
