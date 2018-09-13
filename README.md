[![Build Status](https://travis-ci.org/eeverman/andhow.svg?branch=master)](https://travis-ci.org/eeverman/andhow) [![codecov](https://codecov.io/gh/eeverman/andhow/branch/master/graph/badge.svg)](https://codecov.io/gh/eeverman/andhow)
![Andhow Visual](andhow.gif)
AndHow!  strong.valid.simple.AppConfiguration
======
AndHow is an easy to use configuration framework with strong typing and detailed 
validation for web apps, command line or any application environment.

_**Learn more at the [AndHow main site](https://sites.google.com/view/andhow)**_

Key Features
--------------
* **Strong Typing**
* **Detailed validation**
* **Simple to use**
* **Use Java _public_ & _private_ to control configuration visibility**
* **Validates _all_ property values at startup to _[Fail Fast](http://www.practical-programming.org/ppl/docs/articles/fail_fast_principle/fail_fast_principle.html)_**
* **Loads values from multiple sources (JNDI, env vars, prop files, etc)**
* **Generates configuration sample file based on  application properties**

Questions / Discussion / Contact
--------------
[Join the discussion](https://sites.google.com/view/andhow/join-the-discussion)
on the [user forum](https://groups.google.com/d/forum/andhowuser)
or the *Slack* group (See details on the
[Join](https://sites.google.com/view/andhow/join-the-discussion) page).

Use it via Maven (available on Maven Central)
--------------
```xml
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.4.0</version>
</dependency>
```
**AndHow can be used in projects with Java 8 and above, however, Java 9 and above have [some restrictions](https://sites.google.com/view/andhow/projects-using-java-9)**

Complete Usage Example
--------------
_**More usage examples and documentation
are available at the [AndHow main site](https://sites.google.com/view/andhow)**_
```java
package org.simple;

import org.yarnandtail.andhow.property.*;

public class GettingStarted {
  
	//1
	final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull()
		.desc("Start the countdown from this number")
		.mustBeGreaterThanOrEqualTo(1).defaultValue(2).build();
 
	private final static StrProp LAUNCH_CMD = StrProp.builder().mustBeNonNull()
		.desc("What to say when its time to launch")
		.mustMatchRegex(".*Go.*").defaultValue("GoGoGo!").build();
 
	public String launch() {
		String launch = "";
  
		//2
		for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
			launch = launch += i + "...";
		}
  
		return launch + LAUNCH_CMD.getValue();
	}

	public static void main(String[] args) {
		GettingStarted gs = new GettingStarted();
		System.out.println(gs.launch());
	}
}
```
Walking through the example:
### Section 1 : Declaring AndHow Properties
Properties must be `final static`, but may be `private` or any other scope.
`builder` methods simplify adding validation, description, defaults and
other metadata.
Properties are strongly typed, so default values and validation are specific to
the type, for instance, `StrProp` has regex validation rules for `String`s
while the `IntProp` has greater-than and less-than rules available.

### Section 2 : Using AndHow Properties
AndHow Properties are used just like static final constants with an added
`.getValue()` on the end to fetch the value.  
Strong typing means that calling `COUNT_DOWN_START.getValue()`
returns an `Integer` while calling `LAUNCH_CMD.getValue()` returns a `String`.

### How do I actually configure some values?
We're getting there.
The example has defaults for each property so with no other configuration available, 
the main method uses the defaults and prints:  `3...2...1...GoGoGo!`    
Things are more interesting if the default values are removed from the code above.
Both properties must be non-null, so removing the defaults causes the validation 
rules to be violated at startup.  Here is an excerpt of the console output when that happens:
```
========================================================================
Drat! There were AndHow startup errors.
Sample configuration files will be written to: '/some_local_tmp_directory/andhow-samples/'
========================================================================
REQUIRMENT PROBLEMS - When a required property is not provided
Detailed list of Requirements Problems:
Property org.simple.GettingStarted.COUNT_DOWN_START: This Property must be non-null - It must have a non-null default or be loaded by one of the loaders to a non-null value
Property org.simple.GettingStarted.LAUNCH_CMD: This Property must be non-null - It must have a non-null default or be loaded by one of the loaders to a non-null value
========================================================================
```

**Validation happens at startup and happens for all properties in the entire application.**
Properties, even those defined in 3rd party jars, are discovered and values for 
them are loaded and validated.  
If that fails (as it did above), AndHow throws a RuntimeException to stop 
application startup and uses property metadata to generate specific error 
messages and (helpfully) sample configuration files. 
Here is an excerpt of the Java Properties file created when the example code failed validation:
```
# ######################################################################
# Property Group org.simple.GettingStarted

# COUNT_DOWN_START (Integer) NON-NULL - Start the countdown from this number
# The property value must be greater than or equal to 1
org.simple.GettingStarted.COUNT_DOWN_START = [Integer]

# LAUNCH_CMD (String) NON-NULL - What to say when its time to launch
# The property value must match the regex expression '.*Go.*'
org.simple.GettingStarted.LAUNCH_CMD = [String]
```
AndHow uses all of the provided metadata to create a detailed and well commented 
configuration file for your project.  
Insert some real values into that file and place it on your classpath at 
/andhow.properties and it will automatically be discovered and loaded at startup.
By default, AndHow automatically discovers and loads configuration from seven common sources.  
The default list of configuration loading, in order, is:
1. Fixed values (explicitly set in code for AndHow to use)
2. String[] arguments from the static void main() method
3. System Properties
4. Environmental Variables
5. JNDI
6. Java properties file on the filesystem (path specified as an AndHow property)
7. Java properties file on the classpath (defaults to /andhow.properties)

Property values are set on a first-win basis, so if a property is set as fixed value,
that will take precedence over values passed in to the main method.  
Values passed to the main method take precedence over system properties as so on.

_**For more examples and documentation, visit the [AndHow main site](https://sites.google.com/view/andhow)**_

_**&?!**_
