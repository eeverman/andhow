There are several alternative solutions to application configuration.  Many address different issues than AndHow.  Others don't solve configuration quite as nicely.

Spring
-------------------------
Spring has an implicit application strategy in which most needed classes are created at startup.  This helps toward the goal of _failing fast_ for invalid configuration, but it is up to the constructed classes to do type conversions and validate their configuration.  Documentation for application configuration is thus buried in the validation code of Spring bean class.

Spring will read properties found in its application.properties file and make those values available in xml files via EL like `${my.property.value}`.  System properties and environment variables use EL in the form `#{systemProperties['APP.LOG_PATH']}` and `#{systemEnvironment['HOME']}` respectively.  All of these assume you know (or care) where specific property values are to be loaded from.  SpringBoot has additional options which are an improvement and might be a realistic alternative to AndHow for applications that use it.

JNDI
-------------------------
JNDI provides a Java standard way to configure an app, but doesn’t have a built-in way to include other configuration sources, such as environment variables and system properties.  JNDI is a typed system, but doesn’t do validation.  Since code using JNDI tends to pull configuration on demand, it tends towards a _fail late_ design, where missing or invalid configuration is not known until it is encountered.

To application code, JNDI presents as an Object hash map.  Retrieving values requires using 'magic key strings' sprinkled in your code and casting the returned value to a specific type.  Short of a complete, correct and well commented application context file w/ JNDI properties, there is no central documentation.

For applications running in containers, most organizations use JNDI.  However, those same organizations likely struggle with the limitations of JNDI, particularly when dealing with passwords and environment configuration.  JNDI turns out to be an essential part of configuration, but just a single piece, alongside environmental variables, system properties and possibly properties files.  AndHow helps stitch it all back together.

Commons Configuration
-------------------------
An Apache project that loads properties from many sources, like AndHow.  It is a well established and active project with some features that AndHow does not have, including multi-value properties and updatable properties.

The two project have fundamentally different approaches, however.  In AndHow, all Properties are declared with a type and (optionally) can be given validation and marked as required.  If a property is missing or given an invalid value, the application will throw a RuntimeError at startup, thus _failing fast_.  In Commons Config, Configuration objects load the properties they find from various sources.  Since the properties are not declared, there is no defined type or validation possible, resulting in a _fail late_ design.  Commons Config's lack of property declaration and multiple property sources means that configuration documentation is difficult and not helped by the configuration framework.

To application code, Commons Config presents similar to JDBC with 'magic strings' used to specify the value you want to read.  Properties can be fetch by type, as in:
`config.getInt(“property.name”);	//similar to fetching value from JDBC`
but that conversion will fail at the time it is encountered, not at startup.

[Typesafehub Config](https://github.com/typesafehub/config) & [Kong](http://advantageous.github.io/konf/)
-------------------------
These both claim to be _typesafe_, but values are converted to the proper type at _retrieval_ time using a typed accessor method.  For example, retrieving a value from either one looks something like this (warning, not real code):
value, both require you to do something like this: 
`int inValue = config.getInt("my.magic.key");`

This is problematic for several reasons.  First, there is no guarantee that 'my.magic.key' is intended to be an integer.  The getInt method returns an int, but maybe this value is supposed to be a long or a string.  Second, conversion happens at _access time_, meaning that if the supplied value cannot be converted to an integer, you won't know it until your application fails during use - This is called _failing late_.  It is far better to _fail fast_ at startup by type checking your values when they are loaded.  Finally, 'my.magic.key' is a hardcoded string in your code, something coders know they should avoid for good reason.

By contrast, an AndHow property are declared with a type, validation and a description like this:
`IntProp MAX_TREADS = IntProp.builder().mustBeGreaterThan(0).required()
			.description("Max number of threads to allow for parallel calcs").build();`
Using an AndHow property looks like this:
`int maxThreads = MAX_TREADS.getValue();`

If `MAX_TREADS` is given a value that is not an integer, less than zero, or is not assigned a value, the application will throw a RuntimeException at startup (when you want it), rather than mysteriously fail at some later point when the portion of the application using the property is accessed.

DROP - A USGS configuration tool
-------------------------
DROP reads from system properties, JNDI and properties files.  It does not distinguish between null values and an uninitiated system.  It also doesn’t complain if it is initiated multiple times.  All DROP values are untyped (strings) with no validation.