package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.load.KeyObjectPair;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.TextUtil;

/**
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

			//simple check for duplicates doesn't consider KOP values
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
			_fixedVals.removeIf(f -> f.getProperty().equals(property));
			return (S) this;
		}

		@Override
		public S addFixedValue(final String propertyNameOrAlias, final Object value) {

			try {
				KeyObjectPair kop = new KeyObjectPair(propertyNameOrAlias, value);

				//Simple check for duplicates
				if (_fixedKeyObjectPairVals.stream().map(k -> k.getName()).anyMatch(n -> n.equals(kop.getName()))) {
					throw new IllegalArgumentException(
							"A fixed value for the Property '" + kop.getName() + "' has been assigned twice.");
				}

				_fixedKeyObjectPairVals.add(kop);

				return (S) this;

			} catch (ParsingException e) {
				throw new IllegalArgumentException(e);
			}

		}

		@Override
		public S removeFixedValue(final String propertyNameOrAlias) {
			final String cleanName = TextUtil.trimToNull(propertyNameOrAlias);
			_fixedKeyObjectPairVals.removeIf(k -> k.getName().equals(cleanName));
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
		 * '/andhow.properties' is used.<br>
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

			if (
					classpathPropFilePathString != null &&
					classpathPropFilePathString.contains(".") &&
					!classpathPropFilePathString.startsWith("/")
			) {
				throw new IllegalArgumentException("A path to a property file on the classpath "
						+ "must start with a '/' if the filename contains a dot.");
			}

			this.classpathPropFilePathStr = classpathPropFilePathString;

			return (S) this;
		}

		/**
		 * Sets the classpath path via a StrProp (a Property of String type) to a
		 * properties file for the StdPropFileOnClasspathLoader to load.
		 *
		 * If no path is specified via either a String or StrProp, the path
		 * '/andhow.properties' is used.<br>
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
		 * Sets the System environment vars that AndHow will use to load Property values
		 * from for the {@Code StdEnvVarLoader} loader.
		 *
		 * If this method is not called or is called with a null Map, the actual env vars
		 * from {@Code System.getenv()} will be used.  Calling this method with an empty
		 * Map will effectively prevent AndHow from receiving configuration from env vars.
		 *
		 * <em></em>This does not actually change actual environment variables or what is
		 * returned from {@Code System.getenv()}. It only replaces what AndHow will see for env vars.
		 *
		 * Note: There is no reason to use this method:  Use one of the {@Code addFixedValue()}
		 * methods instead.  Those methods are more clear, don't have to parse values, and
		 * (unlike this method) are not deprecated.
		 *
		 * @deprecated This method will be removed in a future release - it has no meaningful
		 * 	usage.  Use the addFixedValue() methods instead.
		 * @param newEnvProperties
		 * @return
		 */
		public S setEnvironmentProperties(Map<String, String> newEnvProperties) {

			if (newEnvProperties != null) {
				this.envProperties = new HashMap<>();
				this.envProperties.putAll(newEnvProperties);
			} else {
				this.envProperties = null;
			}
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
