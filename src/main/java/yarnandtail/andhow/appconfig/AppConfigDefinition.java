package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.ConstructionProblem;
import yarnandtail.andhow.NamingStrategy;
import yarnandtail.andhow.Validator;

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
	private final List<ConfigPoint<?>> points = new ArrayList();
	private final List<ConstructionProblem> constructProblems = new ArrayList();
	
	/**
	 * Adds a ConfigPointGroup, its ConfigPoint and the name and aliases for that point
	 * to all the collections.
	 * 
	 * @param group The ConfigPointGroup parent of the point
	 * @param point The ConfigPoint to be added
	 * @param names The names associated w/ this point
	 */
	public void addPoint(Class<? extends ConfigPointGroup> group, ConfigPoint<?> point, 
			NamingStrategy.Naming names) {
		
		if (group == null || point == null || names == null || names.getCanonicalName() == null) {
			throw new RuntimeException("Null values are not allowed when registering a configuration point.");
		}
		
		//Build a list of the canonical name and all the aliases (if any) to simplify later logic
		ArrayList<String> allNames = new ArrayList();
		allNames.add(names.getCanonicalName());
		allNames.addAll(names.getAliases());
		
		if (canonicalNameByPoint.containsKey(point)) {
			ConstructionProblem.DuplicatePoint dupPoint = new ConstructionProblem.DuplicatePoint(
					point, getGroupForPoint(point), canonicalNameByPoint.get(point),
					point, group, names.getCanonicalName());
			
			constructProblems.add(dupPoint);
			return;
		}
		
		//Check for duplicate names
		for (String a : allNames) {
			ConfigPoint<?> conflictPoint = pointsByNames.get(a);
			if (conflictPoint != null) {
				ConstructionProblem.NonUniqueNames notUniqueName = new ConstructionProblem.NonUniqueNames(
					conflictPoint, getGroupForPoint(conflictPoint), canonicalNameByPoint.get(conflictPoint),
					point, group, names.getCanonicalName(), a);
						
				constructProblems.add(notUniqueName);
				return;
			}
		}
		
		//Check for bad internal validation configuration (eg, bad regex string)
		for (Validator v : point.getValidators()) {
			if (! v.isSpecificationValid()) {
				ConstructionProblem.InvalidValidationConfiguration badValid = new
					ConstructionProblem.InvalidValidationConfiguration(
					point, group, names.getCanonicalName(), v);
				
				constructProblems.add(badValid);
				return;
			}
		}
		
		//Check the default value against validation
		if (checkForInvalidDefaultValue(point, group, names.getCanonicalName())) {
			return;
		}
		
		//
		//All checks pass, so add point
		
		canonicalNameByPoint.put(point, names.getCanonicalName());
		points.add(point);


		for (String a : allNames) {
			pointsByNames.put(a, point);
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
	
	/**
	 * Since code outside this class is adding points, external code also needs
	 * to be able to record when it cannot add points to the definition.
	 * 
	 * @param problem 
	 */
	public void addConstructionProblem(ConstructionProblem problem) {
		constructProblems.add(problem);
	}
	
	public ConfigPoint<?> getPoint(String name) {
		return pointsByNames.get(name);
	}
	
	public String getCanonicalName(ConfigPoint<?> point) {
		return canonicalNameByPoint.get(point);
	}
	
	public List<ConfigPoint<?>> getPoints() {
		return Collections.unmodifiableList(points);
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
	
	/**
	 * Finds the group containing the specified point.
	 * 
	 * @param point
	 * @return May return null if the Point is not in any group, or during construction,
	 * if the group has not finished registering all of its points.
	 */
	public Class<? extends ConfigPointGroup> getGroupForPoint(ConfigPoint<?> point) {
		for (Class<? extends ConfigPointGroup> group : groupList) {
			if (pointsByGroup.get(group).contains(point)) {
				return group;
			}
		}
		
		return null;
	}

	/**
	 * Returns a list of ConstructionProblems found while building the definition.
	 * 
	 * @return Never returns null - only an empty list.
	 */
	public List<ConstructionProblem> getConstructionProblems() {
		return Collections.unmodifiableList(constructProblems);
	}
	
	/**
	 * Checks a ConfigPoint's default value against its Validators and adds entries
	 * to constructProblems if there are issues.
	 * 
	 * @param <T>
	 * @param point
	 * @param group
	 * @param canonName
	 * @return True if the default value is invalid.
	 */
	protected final <T> boolean checkForInvalidDefaultValue(ConfigPoint<T> point, 
			Class<? extends ConfigPointGroup> group, String canonName) {
		
		boolean foundProblem = false;
		
		if (point.getDefaultValue() != null) {
			T t = point.getDefaultValue();
			
			if (t != null) {
			
				for (Validator<T> v : point.getValidators()) {
					if (! v.isValid(t)) {

						ConstructionProblem.InvalidDefaultValue problem = 
								new ConstructionProblem.InvalidDefaultValue(
										point, group, canonName, 
										v.getInvalidMessage(t));
						constructProblems.add(problem);
						foundProblem = true;
					}
				}
			}
		}
		
		return foundProblem;
	}
	
}
