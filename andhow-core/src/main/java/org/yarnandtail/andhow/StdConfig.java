package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * <S extends StdConfig<S>>
 * @author ericeverman
 */
public class StdConfig {
	
	public static StdConfigImpl instance() {
		return new StdConfigImpl();
	}
	
	/**
	 * Final class 'closes' the generics for an actual implementation.
	 */
	public static final class StdConfigImpl extends StdConfigAbstract<StdConfigImpl> {
		
	}
	
	/**
	 * Abstract class here lets this be extended maintaining the generic types
	 * @param <S> 
	 */
	public static abstract class StdConfigAbstract<S extends StdConfigAbstract<S>> extends BaseConfig<S> {

		@Override
		public <T> S addFixedValue(Property<T> property, T value) {

			if (property == null) {
				throw new IllegalArgumentException("The property cannot be null");
			}

			for (PropertyValue pv : _fixedVals) {
				if (property.equals(pv.getProperty())) {
					throw new IllegalArgumentException("A fixed value for this property has been assigned twice.");
				}
			}

			PropertyValue pv = new PropertyValue(property, value);
			_fixedVals.add(pv);

			return (S) this;
		}

		@Override
		public S removeFixedValue(Property<?> property) {

			if (property == null) {
				throw new IllegalArgumentException("The property cannot be null");
			}

			Iterator<PropertyValue> it = _fixedVals.iterator();
			while (it.hasNext()) {
				PropertyValue pv = it.next();
				if (property.equals(pv.getProperty())) {
					it.remove();
					break;
				}
			}

			return (S) this;
		}

		@Override
		public S setCmdLineArgs(String[] commandLineArgs) {

			if (commandLineArgs != null && commandLineArgs.length > 0) {
				_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
			}

			return (S) this;
		}

		/**
		 * Sets the classpath path to a properties file for the
		 * StdPropFileOnClasspathLoader to load.
		 *
		 * If no path is specified via either a String or StrProp, the path
		 * '/andhow.properties' is used.<br/>
		 *
		 * Paths should start with a forward slash and have packages delimited by
		 * forward slashes. If the file name contains a dot, the path <em>must</em>
		 * start with a forward slash.
		 *
		 * @param classpathPropFilePathString
		 * @return
		 */
		public S setClasspathPropFilePath(String classpathPropFilePathString) {

			classpathPropFilePathString = TextUtil.trimToNull(classpathPropFilePathString);

			if (classpathPropFilePathString != null && classpathPropFilePathProp != null) {
				throw new IllegalArgumentException("The property file classpath cannot "
						+ "be specified as both a String and StrProp");
			}

			if (classpathPropFilePathString != null && !classpathPropFilePathString.startsWith("/")
					&& (classpathPropFilePathString.endsWith(".properties") || classpathPropFilePathString.endsWith(".xml"))) {

				throw new IllegalArgumentException("The path to the property file on "
						+ "the classpath should start with a '/' if the filename contains a dot.");
			}
			this.classpathPropFilePathStr = classpathPropFilePathString;

			return (S) this;
		}

		/**
		 * Sets the classpath path via a StrProp (a Property of String type) to a
		 * properties file for the StdPropFileOnClasspathLoader to load.
		 *
		 * If no path is specified via either a String or StrProp, the path
		 * '/andhow.properties' is used.<br/>
		 *
		 * Paths should start with a forward slash and have packages delimited by
		 * forward slashes. If the file name contains a dot, the path <em>must</em>
		 * start with a forward slash. Thus, it is good practice to add a validation
		 * rule to the StrProp used here to ensure it
		 * <code>mustStartWith("/")</code>.
		 *
		 * @param classpathPropFilePathProperty
		 * @return
		 */
		public S setClasspathPropFilePath(StrProp classpathPropFilePathProperty) {

			if (classpathPropFilePathStr != null && classpathPropFilePathProperty != null) {
				throw new IllegalArgumentException("The property file classpath cannot "
						+ "be specified as both a String and StrProp");
			}

			this.classpathPropFilePathProp = classpathPropFilePathProperty;

			return (S) this;
		}

		/**
		 * If set, the properties file loaded by StdPropFileOnClasspathLoader must
		 * be found and a RuntimeException will be thrown if it is not found.
		 *
		 * This is not set by default, allowing the properties file to be optional.
		 *
		 * @return
		 */
		public S classpathPropertiesRequired() {
			_missingClasspathPropFileAProblem = true;
			return (S) this;
		}

		/**
		 * If set, the properties file loaded by StdPropFileOnClasspathLoader is
		 * optional and will not throw an error if it is not found.
		 *
		 * This is set by default, so there is no need to explicitly call it.
		 *
		 * @return
		 */
		public S classpathPropertiesNotRequired() {
			_missingClasspathPropFileAProblem = false;
			return (S) this;
		}

		/**
		 * Sets the filesystem path via a StrProp (a Property of String type) to a
		 * properties file for the StdPropFileOnFilesystemLoader to load.
		 *
		 * If no property is set to specify a path, or a property is set by has no
		 * value, this loader won't be used. If the property is specified but the
		 * specified file is missing, an error will be thrown based on the
		 * filesystemPropFileRequired flag.
		 *
		 * Paths should generally be absolute and correctly formed for the host
		 * environment.
		 *
		 * @param filesystemPropFilePath
		 * @return
		 */
		public S setFilesystemPropFilePath(StrProp filesystemPropFilePath) {
			this.filesystemPropFilePathProp = filesystemPropFilePath;
			return (S) this;
		}

		/**
		 * If set, the properties file loaded by StdPropFileOnFilesystemLoader must
		 * be found and a RuntimeException will be thrown if it is not found.
		 *
		 * This is not set by default, allowing the properties file to be optional.
		 *
		 * @return
		 */
		public S filesystemPropFileRequired() {
			_missingFilesystemPropFileAProblem = true;
			return (S) this;
		}

		/**
		 * If set, the properties file loaded by StdPropFileOnFilesystemLoader is
		 * optional and will not throw an error if it is not found.
		 *
		 * This is set by default, so there is no need to explicitly call it.
		 *
		 * @return
		 */
		public S filesystemPropFileNotRequired() {
			_missingFilesystemPropFileAProblem = false;
			return (S) this;
		}

		/**
		 * Allows system properties to be overridden.
		 *
		 * @param properties
		 */
		public S setSystemProperties(Properties properties) {
			systemProperties = properties;
			return (S) this;
		}

		/**
		 * Allows the System environment to be overridden.
		 *
		 * @param envProperties
		 * @return
		 */
		public S setEnvironmentProperties(Map<String, String> envProperties) {
			this.envProperties = envProperties;
			return (S) this;
		}

		public S setStandardLoaders(List<Class<? extends StandardLoader>> newStandardLoaders) {

			standardLoaders.clear();
			standardLoaders.addAll(newStandardLoaders);

			return (S) this;
		}

		public S setStandardLoaders(Class<? extends StandardLoader>... newStandardLoaders) {

			standardLoaders.clear();

			for(Class<? extends StandardLoader> sl : newStandardLoaders) {
				standardLoaders.add(sl);
			}

			return (S) this;
		}

		public S insertLoaderBefore(
				Class<? extends StandardLoader> insertBeforeThisLoader, Loader loaderToInsert) {

			if (insertBefore.containsKey(insertBeforeThisLoader)) {
				insertBefore.get(insertBeforeThisLoader).add(loaderToInsert);
			} else {
				List<Loader> list = new ArrayList(1);
				list.add(loaderToInsert);
				insertBefore.put(insertBeforeThisLoader, list);
			}

			return (S) this;
		}

		public S insertLoaderAfter(
				Class<? extends StandardLoader> insertAfterThisLoader, Loader loaderToInsert) {

			if (insertAfter.containsKey(insertAfterThisLoader)) {
				insertAfter.get(insertAfterThisLoader).add(loaderToInsert);
			} else {
				List<Loader> list = new ArrayList(1);
				list.add(loaderToInsert);
				insertAfter.put(insertAfterThisLoader, list);
			}

			return (S) this;
		}
	
	}
	
}
