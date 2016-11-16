package yarnandtail.andhow.name;

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
	public Naming buildNames(ConfigPoint configPoint, Class<? extends ConfigPointGroup> parentGroup, String fieldName) {
		String name = parentGroup.getCanonicalName() + "." + fieldName;
		Naming naming = new Naming(name, EMPTY_STRING_LIST);
		return naming;
	}
	
}
