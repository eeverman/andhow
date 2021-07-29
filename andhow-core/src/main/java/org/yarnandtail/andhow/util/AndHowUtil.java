package org.yarnandtail.andhow.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.StaticPropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.internal.ConstructionProblem.TooManyAndHowInitInstances;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.service.InitLoader;

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
	public static StaticPropertyConfigurationMutable buildDefinition(
			List<GroupProxy> groups, List<Loader> loaders,
			NamingStrategy naming, ProblemList<Problem> problems) {

		StaticPropertyConfigurationMutable appDef = new StaticPropertyConfigurationMutable(naming);

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

				} catch (InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
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
						problems.addAll(registerGroup(appDef, AndHowUtil.buildGroupProxy(loader.getClassConfig(), false)));
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

	protected static ProblemList<ConstructionProblem> registerGroup(StaticPropertyConfigurationMutable appDef,
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
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		ArrayList<Exporter> exps = new ArrayList();

		GroupExport[] groupExports = group.getProxiedGroup().getAnnotationsByType(GroupExport.class);

		for (GroupExport ge : groupExports) {
			Class<? extends Exporter> expClass = ge.exporter();

			Exporter exporter = expClass.getDeclaredConstructor().newInstance();

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
	
	/**
	 * Invokes buildGroupProxy(group, true).
	 * 
	 * @param group
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	public static GroupProxy buildGroupProxy(Class<?> group)
			throws IllegalArgumentException, IllegalAccessException, SecurityException {

		return buildGroupProxy(group, true);
	}
	
	/**
	 * Wraps a class that contains AndHow Properties in Proxy class for use within AndHow.
	 * 
	 * A user specified group is the 'standard' type of group where are user
	 * creates a property and that class containing that property is then registered
	 * as a group.  A non-userGroup one which is added automatically by AndHow
	 * to configure AndHow itself or one of the loaders.
	 * 
	 * @param group
	 * @param userGroup If true, this group is a user specified group
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	public static GroupProxy buildGroupProxy(Class<?> group, boolean userGroup)
			throws IllegalArgumentException, IllegalAccessException, SecurityException {
		
		List<NameAndProperty> naps = getProperties(group);
		GroupProxy groupProxy = new GroupProxyImmutable(NameUtil.getAndHowName(group), 
				NameUtil.getJavaName(group), naps, userGroup);
		
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
	
	/**
	 * Hunts up the inheritance tree to find the specified method.
	 * 
	 * setAccessible(true) is called on the method if found.
	 * 
	 * If the method cannot be found, null is returned.  If a security restriction
	 * prevents accessing the method, that SecurityException is thrown.
	 * 
	 * @param target The class to start searching from
	 * @param name
	 * @param parameterTypes
	 * @return
	 * @throws SecurityException 
	 */
	public static Method findMethod(Class<?> target, String name, Class<?>... parameterTypes) 
			throws SecurityException {
		
		while (target != null) {
			try {
				Method m = target.getDeclaredMethod(name, parameterTypes);
				m.setAccessible(true);
				return m;
			} catch (NoSuchMethodException ex) {
				//expected
			}
			
			target = target.getSuperclass();
		}
		
		return null;
	}
	
	/**
	 * Returns true if the specified class name is on the classpath.
	 * 
	 * This never throws an exception - all exception conditions just return false.
	 * 
	 * @param className
	 * @return 
	 */
	public static boolean classExists(String className) {
		return getClassForName(className) != null;
	}
	
	/**
	 * Loads a class by name with no errors, returning null if the class cannot be found.
	 * 
	 * @param className
	 * @return 
	 */
	public static Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (Throwable ex) {
			return null;
		}
	}
	
	/**
	 * Creates a new Object instance from the named class using the default
	 * no-arg constructor.
	 * 
	 * No errors are thrown, null if just returned if the class does not exist,
	 * there is no no-arg constructor, so some other exception occurs.
	 * 
	 * @param className
	 * @return 
	 */
	public static Object getClassInstanceForName(String className) {
		Class<?> c = getClassForName(className);
		
		if (c != null) {
			try {
				return c.getDeclaredConstructor().newInstance();
			} catch (Throwable ex) {
				//ignore
			}
		}
		
		return null;
	}
	
	public static AndHowConfiguration<? extends AndHowConfiguration>
			findConfiguration(AndHowConfiguration<? extends AndHowConfiguration> defaultConfig)
			throws AppFatalException {
		
		InitLoader prodLoader = new InitLoader();
		InitLoader testLoader = (InitLoader) getClassInstanceForName(
				"org.yarnandtail.andhow.service.TestInitLoader");

		if (! prodLoader.isValidState()) {
			throw new AppFatalException(
					"Unexpected multiple AndHowInit classes on the classpath", 
					new TooManyAndHowInitInstances(prodLoader.getInitInstances()));
		}
		
		if (testLoader != null && ! testLoader.isValidState()) {
			throw new AppFatalException(
					"Unexpected multiple AndHowTestInit classes on the classpath", 
					new TooManyAndHowInitInstances(testLoader.getInitInstances()));
		}
		
		if (testLoader != null && testLoader.hasConfig()) {
			return testLoader.getAndHowConfiguration(defaultConfig);
		} else {
			return prodLoader.getAndHowConfiguration(defaultConfig);
		}

	}


}
