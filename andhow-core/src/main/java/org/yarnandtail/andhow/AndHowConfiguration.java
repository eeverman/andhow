package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration<C extends AndHowConfiguration> {
	List<Loader> buildLoaders();

	/**
	 * Return a custom list of 'Groups' (classes or interfaces containing AndHow Properties)
	 * to use instead of allowing the auto-discovery to find the Groups.
	 *
	 * AndHow will use a {@link java.util.ServiceLoader} to discover all classes containing
	 * AndHow Properties in conjunction with a generated manifest of those classes created at
	 * compile time.  Each class containing at least one AndHow Property is called a <em>Group</em>
	 * and is represented by a {@link GroupProxy} object.
	 * <p>
	 * If this method returns non-null, then AndHow does not use this auto-discovery mechanism
	 * and just uses the returned list of GroupProxies.  This is primarily for testing, where
	 * it is useful to test on only a small set of Groups and Properties.
	 * <p>
	 * Returning an empty list from this method would result in AndHow initializing with
	 * no registered Groups.
	 *
	 * @return A list of GroupProxies to use instead of the auto-discovery process.  May
	 *   return null (and normally does) to allow the normal auto-discovery to happen.
	 */
	List<GroupProxy> getRegisteredGroups();

	/**
	 * Return the naming strategy used to form Property names, match them to values and display them.
	 *
	 * Currently only {@link org.yarnandtail.andhow.name.CaseInsensitiveNaming} is implemented.
	 *
	 * @return The strategy to use.  Must not be null.
	 */
	NamingStrategy getNamingStrategy();
	
	/**
	 * Sets the command line arguments, removing any previously set commandline args.
	 *
	 * @param commandLineArgs
	 * @return
	 */
	C setCmdLineArgs(String[] commandLineArgs);
	
	
	/**
	 * Sets a fixed, non-configurable value for a Property.
	 *
	 * Property values set in this way use the FixedValueLoader to load values
	 * prior to any other loader. Since the first loaded value for a Property
	 * 'wins', this effectively fixes the value and makes it non-configurable.
	 *
	 * Values specified by the two <code>addFixedValue</code> methods will
	 * through a <code>DuplicatePropertyLoaderProblem</code> if they refer to
	 * the same Property.
	 *
	 * @param <T> The type of Property and value
	 * @param property The property to set a value for
	 * @param value The value to set.
	 * @return
	 */
	<T> C addFixedValue(Property<T> property, T value);
	
	/**
	 * Removes a Property value set <em>only</em> via addFixedValue(Property<T>, T value)
	 * from the list of fixed values.
	 *
	 * It is not an error to attempt to remove a property that is not in this fixed value list.
	 *
	 * @param property A non-null property.
	 * @return
	 */
	C removeFixedValue(Property<?> property);

	/**
	 * Sets a fixed, non-configurable value for a named Property.
	 *
	 * Property values set in this way use the FixedValueLoader to load values
	 * prior to any other loader. Since the first loaded value for a Property
	 * 'wins', this effectively fixes the value and makes it non-configurable.
	 *
	 * Values specified by the two <code>addFixedValue</code> methods will
	 * through a <code>DuplicatePropertyLoaderProblem</code> if they refer to
	 * the same Property.
	 *
	 * @param name The canonical or alias name of Property, which is trimmed to null.
	 * @param value The Object value to set, which must match the type of the Property.
	 * @return
	 */
	C addFixedValue(String name, Object value);

	/**
	 * Removes a Property value set <em>only</em> via addFixedValue(String name, Object value)
	 * from the list of fixed values.
	 *
	 * Note that to successfully remove a fixed value from this list, the name must exactly
	 * match the name used to set the property via addFixedValue(String, Object).  Since
	 * Properties can have aliases, you must know the exact name to set the property.
	 *
	 * It is not an error to attempt to remove a property that is not in this fixed value list,
	 * or to attempt to remove a property value that does not exist - these are just no-ops.
	 *
	 * @param propertyNameOrAlias The name or alias of a property.
	 * @return
	 */
	C removeFixedValue(String propertyNameOrAlias);

	/**
	 * Sets the classpath path to a properties file for the
	 * {@Code StdPropFileOnClasspathLoader} to read and load from.
	 *
	 * If no path is specified via one of the two {@Code setClasspathPropFilePath} methods,
	 * the default classpath of '/andhow.properties' is used.
	 * <p>
	 * As per Java convention, a path on the classpath can use dots or slashes to separate packages.
	 * However, if the file name itself contains dots, then the path must start with a slash and use
	 * slashes to separate packages.
	 * <p>
	 * Valid Examples:
	 * <ul>
	 * <li>/andhow.property - The default. The file is at the root and the name contains a dot</li>
	 * <li>/org/ngo/config.props - Similar to above, but in the org.ngo package, the file name is 'config.props'</li>
	 * <li>org.ngo.props - The package is org.ngo, the file name is 'props'.
	 * There are no dots in the file name, so its OK to use dots to separate the packages</li>
	 * </ul>
	 *
	 * @param classpathPropFilePathString
	 * @return
	 */
	C setClasspathPropFilePath(String classpathPropFilePathString);

	/**
	 * Sets the classpath path via a StrProp (a Property of String type) to a
	 * properties file for the {@Code StdPropFileOnClasspathLoader} to read and load from.
	 *
	 * If no path is specified via one of the two {@Code setClasspathPropFilePath} methods,
	 * the default classpath of '/andhow.properties' is used.
	 * <p>
	 * As per Java convention, a path on the classpath can use dots or slashes to separate packages.
	 * However, if the file name itself contains dots, then the path must start with a slash and use
	 * slashes to separate packages.  Its common to have a '.properties' extension on the properties
	 * file, so its good practice to add a validation rule to the StrProp used here to ensure it
	 * {@Code mustStartWith("/")}.
	 * <p>
	 * Valid Examples of configured values:
	 * <ul>
	 * <li>/andhow.property - The default. The file is at the root and the name contains a dot</li>
	 * <li>/org/ngo/config.props - Similar to above, but in the org.ngo package, the file name is 'config.props'</li>
	 * <li>org.ngo.props - The package is org.ngo, the file name is 'props'.
	 * There are no dots in the file name, so its OK to use dots to separate the packages</li>
	 * </ul>
	 *
	 * @param classpathPropFilePathProperty
	 * @return
	 */
	C setClasspathPropFilePath(StrProp classpathPropFilePathProperty);

	/**
	 * If called to set this to 'required', a classpath properties file must
	 * exist and be readable.  This flag is used by the {@Code StdPropFileOnClasspathLoader}.
	 *
	 * Since the {@Code StdPropFileOnClasspathLoader} has a default property file name,
	 * {@Code /andhow.properties}, setting this to 'required' means that either that
	 * default file name or another that you configure instead must exist.
	 *
	 * @See setClasspathPropFilePath methods for details on using a non-default
	 * classpath properties file.
	 *
	 * A RuntimeException will be thrown if this is set to 'required' and there
	 * is no classpath properties file that can be read.
	 * <br>
	 * This is NOT set by default, allowing the properties file to be optional.
	 *
	 * @return
	 */
	C classpathPropertiesRequired();

	/**
	 * Sets the properties file on the classpath to be optional, the default.
	 *
	 * @See classpathPropertiesRequired
	 *
	 * @return
	 */
	C classpathPropertiesNotRequired();

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
	C setFilesystemPropFilePath(StrProp filesystemPropFilePath);

	/**
	 * If called to set this to 'required', a non-null configured value for the
	 * filesystem properties file must point to an existing, readable properties
	 * file.  This flag is used by the {@Code StdPropFileOnFilesystemLoader}.
	 *
	 * A RuntimeException will be thrown if this is set to 'required' and there
	 * is a path specified which points to a file that does not exist.
	 * Configuring a filesystem path is a two step process:<ul>
	 * <li>First, a StrProp Property must be specified for this configuration
	 * via the {@Code setFilesystemPropFilePath} method</li>
	 * <li>Then, a value must be configured for in an any way that AndHow
	 * reads and loads values, such as environment vars, system properties, etc..</li>
	 * </ul>
	 * If and non-null value is configured, its doesn't point to a readable properties
	 * file, AND this required flag is set, a RuntimeException will be thrown at startup.
	 * <br>
	 * This is NOT set by default, allowing the properties file to be optional.
	 *
	 * @return
	 */
	C filesystemPropFileRequired();

	/**
	 * Sets the properties file on the filesystem to be optional, the default.
	 *
	 * @See setFilesystemPropFilePath
	 *
	 * @return
	 */
	C filesystemPropFileNotRequired();

	//
	//Loader related

	/**
	 * The default list of standard loaders, as a list of Classes that implement
	 * {@Code StandardLoader}
	 * <p>
	 * The returned list is disconnected from the actual list of loaders - it is
	 * intended to be a starting point for applications that want to modify the
	 * list, then call setStandardLoaders().
	 * <p>
	 * Unlike other methods of this class, it does not fluently return a method
	 * to itself, so your code will need a AndHowConfiguration instance reference to
	 * use it, eg:
	 * <pre>{@Code
	 * public class MyAppInitiation implements AndHowInit {
	 *    @Override
	 *  	public AndHowConfiguration getConfiguration() {
	 * 			AndHowConfiguration config = AndHow.findConfig();
	 * 			List<Class<? extends StandardLoader>> sll = config.getDefaultLoaderList();
	 * 			...do some rearranging of the list...
	 *
	 * 			config.setStandardLoaders(sll) ...and go on to call other methods on config...
	 * 		}
	 * }
	 * }</pre>
	 * <p>
	 * Note:  AndHow version up to and including 0.4.1 had this method as a static
	 * method.
	 * @return
	 */
	List<Class<? extends StandardLoader>> getDefaultLoaderList();

	C setStandardLoaders(List<Class<? extends StandardLoader>> newStandardLoaders);

	C setStandardLoaders(Class<? extends StandardLoader>... newStandardLoaders);

	C insertLoaderBefore(Class<? extends StandardLoader> insertBeforeThisLoader, Loader loaderToInsert);

	C insertLoaderAfter(Class<? extends StandardLoader> insertAfterThisLoader, Loader loaderToInsert);

	/**
	 * Force initialization of AndHow using this configuration instance.
	 *
	 * AndHow initialization is a one-time event, so further changes to this configuration will
	 * have no effect and additional calls to build() will throw runtime exceptions.
	 *
	 * @See org.yarnandtail.andhow.AndHow#instance
	 * @See org.yarnandtail.andhow.AndHow#initialize
	 * @deprecated This method will be removed in the next major release.
	 * Use AndHow.instance() or AndHow.initialize()
	 */
	@Deprecated
	void build();
}
