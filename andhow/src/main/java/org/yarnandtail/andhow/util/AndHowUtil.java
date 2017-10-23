package org.yarnandtail.andhow.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.GlobalScopeConfigurationMutable;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * Utilities used by AndHow during initial construction.
 *
 * @author eeverman
 */
public class AndHowUtil {

	/**
	 * Build a fully populated ConstructionDefinition from the passed Groups,
	 * using the NamingStrategy to generate names for each.
	 *
	 * @param groups The PropertyGroups from which to find Properties. May be
	 * null.
	 * @param loaders The Loaders, which may their own configurable
	 * PropertyGroups.
	 * @param naming A naming strategy to use when reading the properties during
	 * loading
	 * @param problems If construction problems are found, add to this list.
	 * @return A fully configured instance
	 */
	public static GlobalScopeConfigurationMutable buildDefinition(
			List<GroupProxy> groups, List<Loader> loaders,
			NamingStrategy naming, ProblemList<Problem> problems) {

		GlobalScopeConfigurationMutable appDef = new GlobalScopeConfigurationMutable(naming);

		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (GroupProxy group : groups) {

				problems.addAll(registerGroup(appDef, group));

				try {
					List<Exporter> exps = getExporters(group);

					for (Exporter e : exps) {
						ExportGroup eg = new ExportGroup(e, group);
						appDef.addExportGroup(eg);
					}

				} catch (InstantiationException ex) {
					ConstructionProblem.ExportException ee
							= new ConstructionProblem.ExportException(ex, group,
									"Unable to created a new instance of one of the Exporters for this group.  "
									+ "Do they all have zero argument constructors?");
					problems.add(ee);
				} catch (IllegalAccessException ex) {
					ConstructionProblem.SecurityException se
							= new ConstructionProblem.SecurityException(ex, group.getProxiedGroup());
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
					try {
						problems.addAll(registerGroup(appDef, AndHowUtil.buildGroupProxy(loader.getClassConfig())));
					} catch (Exception ex) {
						ConstructionProblem.SecurityException ee
								= new ConstructionProblem.SecurityException(ex, loader.getClassConfig());
						problems.add(ee);
					}
				}

				//Check that user specified config properties for this loader are registered
				for (Property p : loader.getInstanceConfig()) {
					if (p == null) {
						problems.add(new ConstructionProblem.LoaderPropertyIsNull(loader));
					} else if (appDef.getCanonicalName(p) == null) {
						problems.add(new ConstructionProblem.LoaderPropertyNotRegistered(loader, p));
					}
				}
			}
		}

		return appDef;
	}

	protected static ProblemList<ConstructionProblem> registerGroup(GlobalScopeConfigurationMutable appDef,
			GroupProxy group) {

		ProblemList<ConstructionProblem> problems = new ProblemList();

		try {
			List<NameAndProperty> nameAndProperties = group.getProperties();

			for (NameAndProperty nameAndProp : nameAndProperties) {
				problems.add(appDef.addProperty(group, nameAndProp.property));
			}

		} catch (Exception ex) {
			ConstructionProblem.SecurityException se
					= new ConstructionProblem.SecurityException(ex, group.getProxiedGroup());
			problems.add(se);
		}

		return problems;
	}

	public static AppFatalException buildFatalException(ProblemList<Problem> problems) {

		return new AppFatalException(
				"Unable to complete application configuration due to problems. "
				+ "See the System.err out or the log files for complete details.",
				problems);

	}

	/**
	 * Returns the list of Exporters that are annotated for a BasePropertyGroup.
	 *
	 * @param group
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static List<Exporter> getExporters(GroupProxy group)
			throws InstantiationException, IllegalAccessException {

		ArrayList<Exporter> exps = new ArrayList();

		GroupExport[] groupExports = group.getProxiedGroup().getAnnotationsByType(GroupExport.class);

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
	 * Builds a list of all Properties and their field names contained in the
	 * passed class.
	 *
	 * Exceptions may be thrown if a security manager blocks access to members.
	 *
	 * @param group
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	public static List<NameAndProperty> getProperties(Class<?> group)
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
	
	public static GroupProxy buildGroupProxy(Class<?> group)
			throws IllegalArgumentException, IllegalAccessException, SecurityException {
		
		List<NameAndProperty> naps = getProperties(group);
		GroupProxy groupProxy = new GroupProxyImmutable(NameUtil.getAndHowName(group), NameUtil.getJavaName(group), naps);
		
		return groupProxy;
	}
	
	public static List<GroupProxy> buildGroupProxies(Collection<Class<?>> registeredGroups)
			throws AppFatalException {

		final ProblemList<Problem> problems = new ProblemList();
		final List<GroupProxy> groupProxies = new ArrayList();

		for (Class<?> clazz : registeredGroups) {

			try {
				GroupProxy gp = AndHowUtil.buildGroupProxy(clazz);
				groupProxies.add(gp);
			} catch (Exception ex) {
				problems.add(new ConstructionProblem.SecurityException(ex, clazz));
			}

		}

		if (problems.isEmpty()) {
			return groupProxies;
		} else {
			AppFatalException afe = new AppFatalException(
					"There is a problem converting the AndHow Properties contained in the registered "
					+ "groups - likely this is a security issue.",
					problems);
			throw afe;
		}

	}
	

	/**
	 * Gets the field name for a property in a class, which is just the last
	 * portion of the canonical name.
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
	public static String getFieldName(Class<?> group, Property<?> property)
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


}
