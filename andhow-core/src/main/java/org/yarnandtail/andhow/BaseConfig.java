package org.yarnandtail.andhow;

import java.lang.reflect.*;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.load.KeyObjectPair;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
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
	
	//A list of hardcoded values used by the StdFixedValueLoader.
	//Provided w/ live Property references
	protected final List<PropertyValue> _fixedVals = new ArrayList();

	//A list of hardcoded values used by the StdFixedValueLoader.
	//Provided as key name (string) and value (object)
	protected final List<KeyObjectPair> _fixedKeyObjectPairVals = new ArrayList();

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
	
	protected BaseConfig() {
		standardLoaders = getDefaultLoaderList();
	}
	
	protected BaseConfig(List<Class<? extends StandardLoader>> standardLoaders) {
		this.standardLoaders = standardLoaders;
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return naming;
	}
	
	protected StdFixedValueLoader buildStdFixedValueLoader() {
		StdFixedValueLoader loader = new StdFixedValueLoader();
		loader.setPropertyValues(_fixedVals);
		loader.setKeyObjectPairValues(_fixedKeyObjectPairVals);
		return loader;
	}
	
	protected StdMainStringArgsLoader buildStdMainStringArgsLoader() {
		StdMainStringArgsLoader loader = new StdMainStringArgsLoader();
		loader.setKeyValuePairs(_cmdLineArgs);
		return loader;
	}
	
	protected StdSysPropLoader buildStdSysPropLoader() {
		StdSysPropLoader loader = new StdSysPropLoader();
		loader.setMap(systemProperties);
		return loader;
	}
	
	protected StdJndiLoader buildStdJndiLoader() {
		StdJndiLoader loader = new StdJndiLoader();
		return loader;
	}
	
	protected StdEnvVarLoader buildStdEnvVarLoader() {
		StdEnvVarLoader loader = new StdEnvVarLoader();
		loader.setMap(envProperties);
		return loader;
	}
	
	protected StdPropFileOnFilesystemLoader buildStdPropFileOnFilesystemLoader() {
		StdPropFileOnFilesystemLoader loader = new StdPropFileOnFilesystemLoader();
		loader.setFilePath(filesystemPropFilePathProp);
		loader.setMissingFileAProblem(_missingFilesystemPropFileAProblem);
		return loader;
	}
	
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
		PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
		List<GroupProxy> registeredGroups = registrar.getGroups();
		return registeredGroups;
	}
	
	@Override
	public void build() {
		AndHow.instance(this);
	}
	
	/**
	 * The list of default loaders as a list.
	 * 
	 * This is a disconnected list from any instance of the BaseConfig.
	 * @return 
	 */
	public static List<Class<? extends StandardLoader>> getDefaultLoaderList() {
		
		List<Class<? extends StandardLoader>> loaders = new ArrayList();
		
		for (Class<?> clazz : DEFAULT_LOADER_LIST) {
			loaders.add((Class<? extends StandardLoader>)clazz);
		}
		
		return loaders;
	}

}
