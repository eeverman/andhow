package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.PropertyGroup;
import org.yarnandtail.andhow.api.ExportGroup;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.NamingStrategy;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.api.AppFatalException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.*;

/**
 * Utilities used by AndHow during initial construction.
 * @author eeverman
 */
public class AndHowUtil {
	

	/**
	 * Build a fully populated ConstructionDefinition from the passed Groups, 
	 * using the NamingStrategy to generate names for each.
	 * 
	 * @param groups The PropertyGroups from which to find Properties.  May be null.
	 * @param loaders The Loaders, which may their own configurable PropertyGroups.
	 * @param naming  A naming strategy to use when reading the properties during loading
	 * @param problems If construction problems are found, add to this list.
	 * @return A fully configured instance
	 */
	public static ConstructionDefinitionMutable buildDefinition(
			List<Class<? extends PropertyGroup>> groups, List<Loader> loaders, 
			NamingStrategy naming, ProblemList<Problem> problems) {

		ConstructionDefinitionMutable appDef = new ConstructionDefinitionMutable(naming);
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (Class<? extends PropertyGroup> group : groups) {

				problems.addAll(registerGroup(appDef, group));
				
				try {
					List<Exporter> exps = getExporters(group);
					
					for (Exporter e : exps) {
						ExportGroup eg = new ExportGroup(e, group);
						appDef.addExportGroup(eg);
					}
					
				} catch (InstantiationException ex) {
					ConstructionProblem.ExportException ee = 
							new ConstructionProblem.ExportException(ex, group,
							"Unable to created a new instance of one of the Exporters for this group.  "
									+ "Do they all have zero argument constructors?");
					problems.add(ee);
				} catch (IllegalAccessException ex) {
					ConstructionProblem.SecurityException se = 
						new ConstructionProblem.SecurityException(ex, group);
					problems.add(se);
				}
				
			}
		}
		
		//Loaders must be after properties b/c the loaders may look for registered
		//Properties.
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				
				//Add any implicit properties used to configure this loader
				if (loader.getClassConfig() != null) {
					problems.addAll(registerGroup(appDef, loader.getClassConfig()));
				}
				
				//Check that user specified config properties for this loader are registered
				for (Property p : loader.getInstanceConfig()) {
					if (appDef.getCanonicalName(p) == null) {
						problems.add(new ConstructionProblem.LoaderPropertyNotRegistered(loader, p));
					}
				}
			}
		}
		
		return appDef;
	}
		
	protected static ProblemList<ConstructionProblem> registerGroup(ConstructionDefinitionMutable appDef,
			Class<? extends PropertyGroup> group) {
		
		ProblemList<ConstructionProblem> problems = new ProblemList();

		try {
			List<NameAndProperty> nameAndProperties = getProperties(group);
			
			for (NameAndProperty nameAndProp : nameAndProperties) {
				problems.add(appDef.addProperty(group, nameAndProp.property));
			}
			
		} catch (Exception ex) {
			ConstructionProblem.SecurityException se = 
					new ConstructionProblem.SecurityException(ex, group);
			problems.add(se);
		}
		
		return problems;
	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
	
	public static AppFatalException buildFatalException(ProblemList<Problem> problems) {
		
		
		return new AppFatalException(
				"Unable to complete application configuration due to problems. " +
				"See the System.err out or the log files for complete details.",
				problems);
		
	}
	

	/**
	 * Returns the list of Exporters that are annotated for a PropertyGroup.
	 * @param group
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException 
	 */
	public static List<Exporter> getExporters(Class<? extends PropertyGroup> group)
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
	public static List<NameAndProperty> getProperties(Class<? extends PropertyGroup> group) 
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
	public static String getFieldName(Class<? extends PropertyGroup> group, Property<?> property) 
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
	public static String getCanonicalName(Class<? extends PropertyGroup> group, Property<?> property) 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		String fieldName = getFieldName(group, property);
		
		if (fieldName != null) {
			return group.getCanonicalName() + "." + fieldName;
		} else {
			return null;
		}
	}
}
