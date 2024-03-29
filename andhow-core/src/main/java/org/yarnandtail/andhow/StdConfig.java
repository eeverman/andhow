package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.*;
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
			loadEnvBuilder.addFixedValue(property, value);
			return (S) this;
		}

		@Override
		public S removeFixedValue(Property<?> property) {
			loadEnvBuilder.removeFixedValue(property);
			return (S) this;
		}

		@Override
		public S addFixedValue(final String propertyNameOrAlias, final Object value) {
			loadEnvBuilder.addFixedValue(propertyNameOrAlias, value);
			return (S) this;
		}

		@Override
		public S removeFixedValue(final String propertyNameOrAlias) {
			loadEnvBuilder.removeFixedValue(propertyNameOrAlias);
			return (S) this;
		}

		@Override
		public S setCmdLineArgs(String[] commandLineArgs) {
			loadEnvBuilder.setCmdLineArgs(commandLineArgs);
			return (S) this;
		}

		@Override
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

		@Override
		public S setClasspathPropFilePath(StrProp classpathPropFilePathProperty) {

			if (classpathPropFilePathStr != null && classpathPropFilePathProperty != null) {
				throw new IllegalArgumentException("The property file classpath cannot "
						+ "be specified as both a String and StrProp");
			}

			this.classpathPropFilePathProp = classpathPropFilePathProperty;

			return (S) this;
		}

		@Override
		public S classpathPropertiesRequired() {
			_missingClasspathPropFileAProblem = true;
			return (S) this;
		}

		@Override
		public S classpathPropertiesNotRequired() {
			_missingClasspathPropFileAProblem = false;
			return (S) this;
		}

		@Override
		public S setFilesystemPropFilePath(StrProp filesystemPropFilePath) {
			this.filesystemPropFilePathProp = filesystemPropFilePath;
			return (S) this;
		}

		@Override
		public S filesystemPropFileRequired() {
			_missingFilesystemPropFileAProblem = true;
			return (S) this;
		}

		@Override
		public S filesystemPropFileNotRequired() {
			_missingFilesystemPropFileAProblem = false;
			return (S) this;
		}

		@Override
		public S setStandardLoaders(List<Class<? extends StandardLoader>> newStandardLoaders) {

			standardLoaders.clear();
			standardLoaders.addAll(newStandardLoaders);

			return (S) this;
		}

		@Override
		public S setStandardLoaders(Class<? extends StandardLoader>... newStandardLoaders) {

			standardLoaders.clear();

			for(Class<? extends StandardLoader> sl : newStandardLoaders) {
				standardLoaders.add(sl);
			}

			return (S) this;
		}

		@Override
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

		@Override
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
