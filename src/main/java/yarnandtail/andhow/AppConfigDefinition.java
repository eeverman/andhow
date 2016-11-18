package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The defined set of ConfigPointGroups, child ConfigPoints and their names for use by the app.
 * 
 * This class is intended to be constructed populated by Loaders during initialization,
 * then should remain unchanged.  If an AppConfig needs to load a new state,
 * if should throw this definition away and start a new one because this classes
 * internal collections provides no transactions or protection from read/write
 * conflicts.
 * 
 * @author eeverman
 */
public class AppConfigDefinition {
	private final Map<Class<? extends ConfigPointGroup>, List<ConfigPoint<?>>> groupedPoints = new HashMap();
	private final List<Class<? extends ConfigPointGroup>> groups = new ArrayList();
	private final Map<String, ConfigPoint<?>> allPointNames = new HashMap();
	private final Map<ConfigPoint<?>, String> canonicalPointNames = new HashMap();
	private final List<ConfigPoint<?>> orderedPoints = new ArrayList();
	
	/**
	 * Adds a ConfigPointGroup, its ConfigPoint and the name and aliases for that point
	 * to all the collections.
	 * 
	 * @param group The ConfigPointGroup parent of the point
	 * @param point The ConfigPoint to be added
	 * @param nameAndAliases The canonical name (1st position) and any aliases
	 */
	public void addPoint(Class<? extends ConfigPointGroup> group, ConfigPoint<?> point, 
			List<String> nameAndAliases) {
		
		if (group == null || point == null || nameAndAliases == null || nameAndAliases.size() == 0) {
			throw new RuntimeException("Null values are not allowed when registering a configuration point.");
		}
		
		if (canonicalPointNames.containsKey(point)) {
			throw new RuntimeException("The ConfigPoint '" + nameAndAliases.get(0) +
					"' in ConfigPointGroup '" + group.getCanonicalName() +
					"' has already been added.  Duplicate entries are not allowed.");
		}
		
		canonicalPointNames.put(point, nameAndAliases.get(0));
		orderedPoints.add(point);
		

		for (String a : nameAndAliases) {
			if (! allPointNames.containsKey(a)) {
				allPointNames.put(a, point);
			} else {
				throw new RuntimeException("The canonical name or alias '" + a + 
						"' is already in use.  Cannot have duplicate names or aliases.");
			}
		}
		
		
		List<ConfigPoint<?>> list = groupedPoints.get(group);
		if (list != null) {
			list.add(point);
		} else {
			list = new ArrayList();
			list.add(point);
			groupedPoints.put(group, list);
			groups.add(group);
		}
		
	}
}
