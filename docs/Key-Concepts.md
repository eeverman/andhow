*	Don't worry about configuration names.  
	With most configuration strategies, there is a name or key that is used to fetch the configuration value out of a hashmap.  AndHow doesn't do this.  Instead, configuration properties work like Java constants.
*	Configuration `Properties` are declared as `public static final` constants.  Use them in code like this: `MY_INTEGER_PROPERTY.getValue()`, which would return an Integer.
*	Properties have deterministic canonical names, just like Java classes.  Use these canonical names to specify values in a configuration source, such as JNDI, property files, cmd line, etc..  If you don't like the names or need to match legacy code, you can add aliases to properties.
*	A group of related properties is a `PropertyGroup`.  A PropertyGroup is declared as an interface and contains `public static final` `Properties`.
*	An application must have  single, defined *entry point* where configuration is loaded.  AndHow enforces this by blowing up if it is initiated a second time which is a feature called *failing fast*.  This situation is risky, since it means that the configuration is non-deterministic, perhaps based on the order of loading.
*	Configuration values are fixed (unchanging) after initiation.  Perhaps dynamic properties will be added at some future time, but that seems like it might fall within the domain of application data, which is not the same as configuration.

## Minor design considerations
*	Frequently used classes, like Property type, use very short names.  Examples: `StrProp` and `LngProp`
*	Less commonly used classes that might have unclear meanings, use fully explicit names, like `EnviromentVariableLoader` and `PropertyFileOnFilesystemLoader`.
*	All property values are currently single value, however, opening this to multi-value configuration values is something that is planned.  This would make it possible to, for instance, configure an arbitrary length list of service endpoints that fail-over from one to the next.