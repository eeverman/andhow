package org.yarnandtail.andhow;

import java.lang.reflect.*;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.load.KeyObjectPair;
import org.yarnandtail.andhow.load.LoaderEnvironmentBuilder;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Basic abstract implementation for AndHowConfiguration instances.
 *
 * @author ericeverman
 * @param <C> The class to return from each of the fluent builder methods, which
 * will be the implementation class in each case.  See StdConfig for an example.
 */
public abstract class BaseConfig<C extends BaseConfig<C>> implements AndHowConfiguration<C> {

	protected static final Class<?>[] DEFAULT_LOADER_LIST = new Class<?>[] {
		StdFixedValueLoader.class, StdMainStringArgsLoader.class,
		StdSysPropLoader.class, StdEnvVarLoader.class,
		StdJndiLoader.class, StdPropFileOnFilesystemLoader.class,
		StdPropFileOnClasspathLoader.class
	};
	
	protected List<Class<? extends StandardLoader>> standardLoaders = new ArrayList();

	/* Two lists of custom loader instances to insert before or after a specific
	standard loader.  Each before or after reference to a std loader may contain
	multiple custom instances to insert. */
	protected Map<Class<? extends StandardLoader>, List<Loader>> insertBefore = new HashMap();
	protected Map<Class<? extends StandardLoader>, List<Loader>> insertAfter = new HashMap();

	// Builder for the LoaderEnvironment
	protected LoaderEnvironmentBuilder loadEnvBuilder = new LoaderEnvironmentBuilder();

	//A list of command line arguments
	protected final List<String> _cmdLineArgs = new ArrayList();

	//Prop file on classpath
	protected String classpathPropFilePathStr;	//mutually XOR
	protected StrProp classpathPropFilePathProp;	//mutually XOR
	protected boolean _missingClasspathPropFileAProblem = false;

	//Prop file on filesystem path
	protected StrProp filesystemPropFilePathProp;
	protected boolean _missingFilesystemPropFileAProblem = false;

	//System Properties
	protected Properties systemProperties;

	//System Environment
	protected Map<String, String> envProperties;
	
	protected NamingStrategy naming = new CaseInsensitiveNaming();

	/**
	 * If non-null, overrides the default group discovery process with this override list.
	 * There is no set method here - subclasses may make editable for use in testing.
	 */
	protected List<Class<?>> overrideGroups = null;

	/**
	 * Construct a new instance.
	 */
	protected BaseConfig() {
		standardLoaders = getDefaultLoaderList();
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return naming;
	}

	/**
	 * Build a StdFixedValueLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdFixedValueLoader buildStdFixedValueLoader() {
		StdFixedValueLoader loader = new StdFixedValueLoader();
		return loader;
	}

	/**
	 * Build a StdMainStringArgsLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdMainStringArgsLoader buildStdMainStringArgsLoader() {
		StdMainStringArgsLoader loader = new StdMainStringArgsLoader();
		loader.setKeyValuePairs(_cmdLineArgs);
		return loader;
	}

	/**
	 * Build a StdSysPropLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdSysPropLoader buildStdSysPropLoader() {
		StdSysPropLoader loader = new StdSysPropLoader();
		loader.setMap(systemProperties);
		return loader;
	}

	/**
	 * Build a StdJndiLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdJndiLoader buildStdJndiLoader() {
		StdJndiLoader loader = new StdJndiLoader();
		return loader;
	}

	/**
	 * Build a StdEnvVarLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdEnvVarLoader buildStdEnvVarLoader() {
		StdEnvVarLoader loader = new StdEnvVarLoader();
		loader.setMap(envProperties);
		return loader;
	}

	/**
	 * Build a StdPropFileOnFilesystemLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdPropFileOnFilesystemLoader buildStdPropFileOnFilesystemLoader() {
		StdPropFileOnFilesystemLoader loader = new StdPropFileOnFilesystemLoader();
		loader.setFilePath(filesystemPropFilePathProp);
		loader.setMissingFileAProblem(_missingFilesystemPropFileAProblem);
		return loader;
	}

	/**
	 * Build a StdPropFileOnClasspathLoader, passing it the needed context.
	 *
	 * This method, like the other buildStd....Loader methods is called based on the class
	 * name of a {@link StandardLoader}.
	 * @return A new StandardLoader, initialized with proper context for its type.
	 */
	protected StdPropFileOnClasspathLoader buildStdPropFileOnClasspathLoader() {
		StdPropFileOnClasspathLoader loader = new StdPropFileOnClasspathLoader();
		loader.setMissingFileAProblem(_missingClasspathPropFileAProblem);
		if (classpathPropFilePathStr != null) {
			loader.setFilePath(classpathPropFilePathStr);
		} else if (classpathPropFilePathProp != null) {
			loader.setFilePath(classpathPropFilePathProp);
		}
		return loader;
	}
	
	@Override
	public List<Loader> buildLoaders() {
		
		List<Loader> loaders = new ArrayList();
		
		for (Class<? extends Loader> clazz : standardLoaders) {
			
			if (insertBefore.containsKey(clazz)) {
				loaders.addAll(insertBefore.get(clazz));
			}
			
			String buildMethod = "build" + clazz.getSimpleName();
			
			
			//Not sure if this searches superclasses - it may not (looks like not)
			Method method = AndHowUtil.findMethod(this.getClass(), buildMethod);

			if (method != null) {
				try {
					loaders.add((Loader)method.invoke(this));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new AppFatalException(
							"Unable to construct the '" + clazz.getCanonicalName() + "' loader", ex);
				}
			} else {
				throw new AppFatalException("There is no loader build method named '" + buildMethod + "'");
			}
			
			if (insertAfter.containsKey(clazz)) {
				loaders.addAll(insertAfter.get(clazz));
			}
		}
		
		return loaders;
	}

	@Override
	public List<GroupProxy> getRegisteredGroups() {
		if (overrideGroups != null) {
			return AndHowUtil.buildGroupProxies(overrideGroups);
		} else {
			return null;
		}
	}
	
	@Override
	public void build() {
		System.err.println(
				"Config.build() is deprecated and will be removed in the next major release. " +
						"See Javadocs for alternatives.");
		AndHow.instance(this);
	}

	@Override
	public List<Class<? extends StandardLoader>> getDefaultLoaderList() {
		
		List<Class<? extends StandardLoader>> loaders = new ArrayList();
		
		for (Class<?> clazz : DEFAULT_LOADER_LIST) {
			loaders.add((Class<? extends StandardLoader>)clazz);
		}
		
		return loaders;
	}

	@Override
	public LoaderEnvironment getLoaderEnvironment() {
		return loadEnvBuilder.toImmutable();
	}

}
