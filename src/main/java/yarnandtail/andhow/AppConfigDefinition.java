package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
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
	public static final List<ConfigPoint<?>> EMPTY_CONFIGPOINT_LIST = Collections.unmodifiableList(new ArrayList());
	
	
	private final Map<Class<? extends ConfigPointGroup>, List<ConfigPoint<?>>> pointsByGroup = new HashMap();
	private final List<Class<? extends ConfigPointGroup>> groupList = new ArrayList();
	private final Map<String, ConfigPoint<?>> pointsByNames = new HashMap();
	private final Map<ConfigPoint<?>, String> canonicalNameByPoint = new HashMap();
	private final List<ConfigPoint<?>> pointList = new ArrayList();
	
	/**
	 * Adds a ConfigPointGroup, its ConfigPoint and the name and aliases for that point
	 * to all the collections.
	 * 
	 * @param group The ConfigPointGroup parent of the point
	 * @param point The ConfigPoint to be added
	 * @param nameAndAliases The canonical name (1st position) and any aliases
	 */
	public void addPoint(Class<? extends ConfigPointGroup> group, ConfigPoint<?> point, 
			NamingStrategy.Naming names) {
		
		if (group == null || point == null || names == null || names.getCanonicalName() == null) {
			throw new RuntimeException("Null values are not allowed when registering a configuration point.");
		}
		
		if (canonicalNameByPoint.containsKey(point)) {
			throw new RuntimeException("The ConfigPoint '" + names.getCanonicalName() +
					"' in ConfigPointGroup '" + group.getCanonicalName() +
					"' has already been added.  Duplicate entries are not allowed.");
		}
		
		canonicalNameByPoint.put(point, names.getCanonicalName());
		pointList.add(point);
		
		//Build a list of the canonical name and all the aliases (if any) to simplify later logic
		ArrayList<String> allNames = new ArrayList();
		allNames.add(names.getCanonicalName());
		allNames.addAll(names.getAliases());
		

		for (String a : allNames) {
			if (! pointsByNames.containsKey(a)) {
				pointsByNames.put(a, point);
			} else {
				throw new RuntimeException("The canonical name or alias '" + a + 
						"' is already in use.  Cannot have duplicate names or aliases.");
			}
		}
		
		List<ConfigPoint<?>> list = pointsByGroup.get(group);
		if (list != null) {
			list.add(point);
		} else {
			list = new ArrayList();
			list.add(point);
			pointsByGroup.put(group, list);
			groupList.add(group);
		}
		
	}
	
	public ConfigPoint<?> getPoint(String name) {
		return pointsByNames.get(name);
	}
	
	public String getCanonicalName(ConfigPoint<?> point) {
		return canonicalNameByPoint.get(point);
	}
	
	public List<ConfigPoint<?>> getPoints() {
		return Collections.unmodifiableList(pointList);
	}
	
	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return Collections.unmodifiableList(groupList);
	}
	
	/**
	 * Returns a list of ConfigPoints registered with the passed group.
	 * If the group is unregistered or has no points, an empty list is returned.
	 * @param group
	 * @return 
	 */
	public List<ConfigPoint<?>> getPointsForGroup(Class<? extends ConfigPointGroup> group) {
		List<ConfigPoint<?>> pts = pointsByGroup.get(group);
		
		if (pts != null) {
			return Collections.unmodifiableList(pts);
		} else {
			return EMPTY_CONFIGPOINT_LIST;
		}
	}
}
