package yarnandtail.andhow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A logical and/or functional grouping of Properties.
 * 
 * @author eeverman
 */
public interface PropertyGroup {
	
	/**
	 * Returns the list of Exporters that are annotated for a PropertyGroup.
	 * @param group
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException 
	 */
	static List<Exporter> getExporters(Class<? extends PropertyGroup> group)
			throws InstantiationException, IllegalAccessException {
		
		ArrayList<Exporter> exps = new ArrayList();
		
		GroupExport[] groupExports = group.getAnnotationsByType(GroupExport.class);
		
		for (GroupExport ge : groupExports) {
			Class<? extends Exporter> expClass = ge.exporter();
			
			Exporter exporter = expClass.newInstance();
			
			exporter.setExportByCanonicalName(ge.exportByCanonicalName());
			exporter.setExportByOutAliases(ge.exportByOutAliases());
			
			exps.add(exporter);
		}
		
		exps.trimToSize();
		return Collections.unmodifiableList(exps);
		
	}
	
	/**
	 * Builds a list of all Properties and their field names contained in
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
	static List<NameAndProperty> getProperties(Class<? extends PropertyGroup> group) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		List<NameAndProperty> props = new ArrayList();
		
		Field[] fields = group.getDeclaredFields();

		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) && Property.class.isAssignableFrom(f.getType())) {

				Property cp = null;

				try {
					cp = (Property) f.get(null);
				} catch (Exception ex) {	
					f.setAccessible(true);
					cp = (Property) f.get(null);
				}
					
				props.add(new NameAndProperty(f.getName(), cp));
				
			}

		}
		
		return props;
	}
	
	
	/**
	 * Gets the field name for a property in the group,
	 * which is just the last portion of the canonical name.
	 * 
	 * The canonical name is of the form:<br/>
	 * [group canonical name].[field name of the Property within the group]<br/>
	 * thus, it is require that the Property be a field within the group, otherwise
	 * null is returned.
	 * 
	 * Exceptions may be thrown if a security manager blocks access to members.
	 * 
	 * @param group
	 * @param property
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	static String getFieldName(Class<? extends PropertyGroup> group, Property<?> property) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		Field[] fields = group.getDeclaredFields();

		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) && Property.class.isAssignableFrom(f.getType())) {

				Property cp = null;

				try {
					cp = (Property) f.get(null);
				} catch (Exception ex) {	
					f.setAccessible(true);
					cp = (Property) f.get(null);
				}
				
				if (cp.equals(property)) {
					return f.getName();
				}
			}

		}
		
		return null;
	}
	
	/**
	 * Gets the true canonical name for a Property in the group.
	 * 
	 * The canonical name is of the form:<br/>
	 * [group canonical name].[field name of the Property within the group]<br/>
	 * thus, it is require that the Property be a field within the group, otherwise
	 * null is returned.
	 * 
	 * Exceptions may be thrown if a security manager blocks access to members.
	 * 
	 * Technically the NameingStrategy is in charge of generating names, but the
	 * canonical name never changes and is based on the package path of a Property
	 * within a PropertyGroup.
	 * 
	 * @param group
	 * @param property
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	static String getCanonicalName(Class<? extends PropertyGroup> group, Property<?> property) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		String fieldName = getFieldName(group, property);
		
		if (fieldName != null) {
			return group.getCanonicalName() + "." + fieldName;
		} else {
			return null;
		}
	}
	
	/**
	 * Simple way to pass the canonical name and associated property around
	 */
	public static class NameAndProperty {
		public String fieldName;
		public Property<?> property;
		
		public NameAndProperty(String fieldName, Property<?> prop) {
			this.fieldName = fieldName;
			this.property = prop;
		}
	}
}
