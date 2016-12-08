package yarnandtail.andhow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eeverman
 */
public interface ConfigPointGroup {

	

	/**
	 * Builds a list of all ConfigPoints and their canonical names contained in
	 * the passed group.
	 * 
	 * Exceptions may be thrown if a security manager blocks access to members.
	 * 
	 * @param group
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	static List<NameAndPoint> getConfigPoints(Class<? extends ConfigPointGroup> group) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		List<NameAndPoint> points = new ArrayList();
		
		Field[] fields = group.getDeclaredFields();

		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) && ConfigPoint.class.isAssignableFrom(f.getType())) {

				ConfigPoint cp = null;

				try {
					cp = (ConfigPoint) f.get(null);
				} catch (Exception ex) {	
					f.setAccessible(true);
					cp = (ConfigPoint) f.get(null);
				}
				
				String name = group.getCanonicalName() + "." + f.getName();	
				points.add(new NameAndPoint(name, cp));
				
			}

		}
		
		return points;
	}
	
	
	/**
	 * Gets the field name for a point in the group,
	 * which is just the last portion of the canonical name.
	 * 
	 * The canonical name is of the form:<br/>
	 * [group canonical name].[field name of the point within the group]<br/>
	 * thus, it is require that the point be a field within the group, otherwise
	 * null is returned.
	 * 
	 * Exceptions may be thrown if a security manager blocks access to members.
	 * 
	 * @param group
	 * @param point
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	static String getFieldName(Class<? extends ConfigPointGroup> group, ConfigPoint<?> point) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		Field[] fields = group.getDeclaredFields();

		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) && ConfigPoint.class.isAssignableFrom(f.getType())) {

				ConfigPoint cp = null;

				try {
					cp = (ConfigPoint) f.get(null);
				} catch (Exception ex) {	
					f.setAccessible(true);
					cp = (ConfigPoint) f.get(null);
				}
				
				if (cp.equals(point)) {
					return f.getName();
				}
			}

		}
		
		return null;
	}
	
	/**
	 * Gets the canonical name for a point in the group.
	 * 
	 * The canonical name is of the form:<br/>
	 * [group canonical name].[field name of the point within the group]<br/>
	 * thus, it is require that the point be a field within the group, otherwise
	 * null is returned.
	 * 
	 * Exceptions may be thrown if a security manager blocks access to members.
	 * 
	 * @param group
	 * @param point
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	static String getCanonicalName(Class<? extends ConfigPointGroup> group, ConfigPoint<?> point) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		String fieldName = getFieldName(group, point);
		
		if (fieldName != null) {
			return group.getCanonicalName() + "." + fieldName;
		} else {
			return null;
		}
	}
	
	/**
	 * Simple way to pass the canonical name and associated point around
	 */
	public static class NameAndPoint {
		public String canonName;
		public ConfigPoint<?> point;
		
		public NameAndPoint(String canonName, ConfigPoint<?> point) {
			this.canonName = canonName;
			this.point = point;
		}
	}
}
