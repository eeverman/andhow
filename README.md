AndHow!  strong.valid.simple.AppConfiguration
======
Strongly typed configuration with detailed validation that is simple to use
for web apps, command line or any application environment.
Configuration by convention.

Key Features
--------------
**&?! Strong Typing**  
**&?! Simple to use**  
**&?! Detailed validation**  
**&?! Load values from any source**  
**&?! Fails Fast**  
**&?! Self Documenting & Sample Generating**  
[more details & features...](https://github.com/eeverman/andhow/wiki)  

Use it via Maven (available on Maven Central)
--------------
```xml
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.3.3</version>
</dependency>
```
Complete Usage Example
--------------
```java
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.load.*;

public class SimpleSample {
	
	public static void main(String[] args) {
		AndHow.builder() /* 1) Simple builder initializes framework */
				.loader(new SystemPropertyLoader())
				.loader(new JndiLoader())
				.loader(new PropertyFileOnClasspathLoader(MySetOfProps.CLASSPATH_PROP))
				.group(MySetOfProps.class) /* 2) MySetOfProps defined below */
				.build();
	
		//3) After initialization, Properties can be used to directly access their values.
		//Note the strongly typed return values.
		String queryUrl =
				MySetOfProps.SERVICE_URL.getValue() +
				MySetOfProps.QUERY_ENDPOINT.getValue();
		Integer timeout = MySetOfProps.TIMEOUT.getValue();
		
		System.out.println("The query url is: " + queryUrl);
		System.out.println("Timeout is : " + timeout);
	}
	
	//4) Normally PropertyGroups would be in separate files with the module they apply to
	@GroupInfo(name="Example Property group", desc="One logical set of properties")
	public interface MySetOfProps extends PropertyGroup {
		
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasIn("url").build(); // 5)
		IntProp TIMEOUT = IntProp.builder().defaultValue(50).build();
		StrProp QUERY_ENDPOINT = StrProp.builder().required()
				.desc("Service name added to end of url for the queries").build();
		// 6)		
		StrProp CLASSPATH_PROP = StrProp.builder().desc("Classpath location of properties file").build();
	}
}
```
### Walking through the example:
1.	`AndHow` initializes with a simple builder().  _Loaders_ read properties from
	various sources.  You can determine the order of the loaders - in this example,
	properties found in System.properties take precedence over values found in
	the JNDI context, and so on.
	The 3rd loader, the property file loader, will load from a Java properties file
	on the classpath.  Naturally, the location of the property file is itself a property.
	Loaders are available for most common scenarios, including reading from command line.
2.	Configuration properties are explicitly declared in groups to keep
	related properties together, then groups are added to the AndHow instance.
3.	`Property` values can be accessed directly from the Property itself - there is
	no _magic name string_ to fetch properties from a HashMap.
	Return values of `getValue()` are _**strongly typed**_.
4.	Properties are declared just like Java constants - In fact, they _are_ Java
	constants.  AndHow takes advantage of the fact that fields declared in
	interfaces are implicitly `public static final` and the `PropertyGroup` interface
	is the base for all sets of `Properties`.
	PropertyGroups are self-describing just by the name you choose for them, but
	you can also include descriptive annotations.
	Properties describe themselves in their builders, 
	including the value type (String, Integer, etc), required vs. optional,
	validation rules and description.
5.	Each property automatically generates a canonical property name that is
	used in configuration sources (like system environmental variables) to set
	that property.  Canonical names are what you would expect for standard Java
	dot notation.  For instance, the canonical name for SERVICE_URL would be:
	`SimpleSample.MySetOfProps.SERVICE_URL`
	However, aliases can be used to work with legacy systems that already have
	established property keys/names.  For instance, SERVICE_URL can also be
	set via the name "url".
6.  The CLASSPATH_PROP Property and its usage by the property loader shows how
	AndHow can be used to configure itself.  If you want to load from a properties
	file and specify the location on command line or as an environment variable,
	this is how you would do it.

This example will probably fail to start up because none of the Properties marked
as required will be configured.  This is a good thing - you don't want a miss-
configured application to appear to start.  Even better, it doesn't just fail,
it prints a sample configuration for each of the loaders in use.  For the Property
file loader, it would print something this:

```properties
##########################################################################################
# Sample properties file generated by AndHow!  strong.simple.valid.App_Configuration  ####
##################################################  https://github.com/eeverman/andhow ###
# Property Group 'Example Property group' - One logical set of properties
# Defined in interface org.simple.SimpleSample.MySetOfProps

# SERVICE_URL (String) 
# The configured value must end with '/'
org.simple.SimpleSample.MySetOfProps.SERVICE_URL = [String]

# TIMEOUT (Integer) 
# Default Value: 50
org.simple.SimpleSample.MySetOfProps.TIMEOUT = 50

# QUERY_ENDPOINT (String) REQUIRED - Service name added to end of url for the queries
org.simple.SimpleSample.MySetOfProps.QUERY_ENDPOINT = [String]
##########################################################################################
```	

Ready to try it?  Copy some code from the [usage examples](https://github.com/eeverman/andhow/tree/master/andhow-usage-examples).
Read more in depth about [AndHow!s features](https://github.com/eeverman/andhow/wiki).
[Contact me](https://github.com/eeverman) if you have questions or would like to help.
	
_**&?!**_