[![Build Status](https://travis-ci.com/eeverman/andhow.svg?branch=master)](https://travis-ci.com/github/eeverman/andhow)
[![codecov](https://codecov.io/gh/eeverman/andhow/branch/master/graph/badge.svg)](https://codecov.io/gh/eeverman/andhow)
[![Javadocs](https://www.javadoc.io/badge/org.yarnandtail/andhow.svg)](https://www.javadoc.io/doc/org.yarnandtail/andhow)


## New Release:  0.4.2, September, 2021 ([notes](https://github.com/eeverman/andhow/releases/tag/andhow-0.4.2)).
<img src="https://github.com/eeverman/andhow/raw/master/logo/AndHow-empty-circle-combination.png" width="55" height="72" alt="AndHow's new logo"  align="left">

More notes...


![Andhow Visual](andhow.gif)
## AndHow!  strong.valid.simple.AppConfiguration
AndHow is an easy to use configuration framework with strong typing and detailed 
validation for web apps, command line or any application environment.

_**Learn more at the [AndHow main site](https://sites.google.com/view/andhow)**_

## Key Features
* **Strong Typing**
* **Detailed validation**
* **Simple to use**
* **Use Java _public_ & _private_ to control configuration visibility**
* **Validates _all_ property values at startup to _[Fail Fast](http://www.practical-programming.org/ppl/docs/articles/fail_fast_principle/fail_fast_principle.html)_**
* **Loads values from multiple sources (JNDI, env vars, prop files, etc)**
* **Generates configuration sample file based on  application properties**

## Questions / Discussion / Issues
* [User forum](https://groups.google.com/d/forum/andhowuser) for questions and help
* [GitHub Discussions](https://github.com/eeverman/andhow/discussions) for feature &amp; planning discussions
* [Issues](https://github.com/eeverman/andhow/issues) for bug reporting

## Use it via Maven (available on Maven Central)
```xml
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.4.2</version>
</dependency>
```
**AndHow can be used in projects with Java 8 - 16. There are
[some considerations](https://sites.google.com/view/andhow/user-guide/java9-and-above) for Java 9+
projects using Jigsaw Modules.**

_**More usage examples and documentation
are available at the [AndHow main site](https://sites.google.com/view/andhow)**_

## Complete Usage Example
```java
package simple;
import org.yarnandtail.andhow.property.*;

public class HelloWorld {

  // 1 Declare AndHow Properties
  private static final StrProp NAME = StrProp.builder().defaultValue("Dave").build();
  public static final IntProp REPEAT_COUNT = IntProp.builder().defaultValue(2).build();

  public static void main(String[] args) {

    // 2 Use AndHow Properties
    for (int i = 0; i < REPEAT_COUNT.getValue(); i++) {
      System.out.println("Hello, " + NAME.getValue());
    }
  }
}
```
[Full code in AndHow samples]()
#### // 1 : Declare AndHow Properties
`StrProp` &amp; `IntProp` are AndHow `Property`s. 
Properties and their values are constants, so they are always `static final`
but may be `private` or any scope.  Properties are _**strongly typed**_, so their value,
default value and validation are type specific.

#### // 2 : Using AndHow Properties
Properties are used just like static final constants with `.getValue()` tacked on:
`NAME.getValue()` returns a `String`, `REPEAT_COUNT.getValue()` returns an `Integer`.

The Properties in the example have defaults, so with no other configuration, running
`HelloWorld.main()` will print **`Hello, Dave`** twice.
One way (of many) to configure values is to add an `andhow.properties` file on the classpath like this:
``` properties
# andhow.properties file at the classpath root

simple.HelloWorld.NAME: Kathy
SIMPLE.HELLOWWORLD.repeat_count: 4
```
Resulting in **`Hello, Kathy`** x4 - AndHow ignores capitalization when reading Property values.
Unlike most configuration utilities, we didn't have to specify a 'name' for NAME.
AndHow builds a logical name for each property by combining the
canonical name of the containing class with the variable name, e.g.: \
`[Java canonical class name].[AndHow Property name]` --&gt; `simple.HelloWorld.NAME` \
Thus, naming isn't something you have to worry about and Java itself ensures name uniqueness.

Let's improve this example a bit...

## Usage Example with validation and command line arguments
```java
package simple;  // Imports left out for simplicity

public class HelloWorld2 {

  // 1 Declare
  private static interface Config {
    StrProp NAME = StrProp.builder().mustStartWith("D").defaultValue("Dave").build();
    IntProp REPEAT_COUNT = IntProp.builder()mustBeGreaterThan(0)
      .mustBeLessThan(5).defaultValue(2).build();
  }

  public static void main(String[] args) {
    
    AndHow.findConfig().setCmdLineArgs(args);  //2 Add cmd line arguments

    // Use
    for (int i = 0; i < Config.REPEAT_COUNT.getValue(); i++) {
      System.out.println("Hello, " + Config.NAME.getValue());
    }
  }
}
```
[Full code in AndHow samples]()
#### // 1 : Declare AndHow Properties
`Property` values can have validation.  At startup, AndHow _**discovers and validates
all Properties in your entire application**_, ensuring that a mis-configuration application
[fails fast](https://www.martinfowler.com/ieeeSoftware/failFast.pdf)_ at startup, rather than
mysteriously failing later.

Placing `Property`'s in an interface is best practice for organization and access control.
Only code able to 'see' a `Property` can retrieve its value - standard Java visibility rules.
Fields in an interface are implicitly `static final`, saving a bit of typing.

#### // 2 : Add command line arguments
AndHow loads Property values from several configuration sources in a
[well established order](https://sites.google.com/view/andhow/user-guide/value-loaders).
At startup, AndHow scans `System.Properties`, environment variables, JNDI values,
the `andhow.properties` file, etc., automatically.

Reading from commandline requires a bit of help from the application. The code
`AndHow.findConfig().setCmdLineArgs(args);` passes the command line arguments in to AndHow.

Running from cmd line to set `NAME` to 'Dar' would look like this:
```bash
  java -Dsimple.HelloWorld2.Config.NAME=Dar -cp [classpath] simple.HelloWorld2
```

What happens if we try to set NAME to "Bar" and violate the 'D' validation rule?
AndHow throws a `RuntimeException` to stop app startup and prints a clear message about the cause:
```text
================================================================================
== Problem report from AndHow!  ================================================
================================================================================
Property simple.HelloWorld2.Config.NAME loaded from string key value pairs:
  The value 'Bar' must start with 'D'
================================================================================
A configuration template for this application has been written to: [...tmp file location...]
```
AndHow uses Property metadata to generate precise error messages.  When errors prevent startup,
AndHow also creates a _configuration template_ with all your application's `Property`s,
validation requirements, types, defaults and more.  Here is what that would look like for this app:
```text
==/==/==/==/==/== Excerpt from a configuration template ==/==/==/==/==/==
# NAME (String)
# Default Value: Dave
# The property value must start with 'D'
simple.HelloWorld2.Config.NAME = Dave

# REPEAT_COUNT (Integer)
# Default Value: 2
# The property value must be less than 5
simple.HelloWorld2.Config.REPEAT_COUNT = 2
```
You can create a configuration template on demand by setting the `AHForceCreateSamples` flag: \
`java -DAHForceCreateSamples=true -cp [classpath] simple.HelloWorld2`

Let's look at a larger, more enterprise-y example.
## Example with a database connection, aliases and exports
In this example, assume we need to configure an ORM framework like [Hibernate](http://hibernate.org).
3rd party frameworks have their own configuration property names and typically accept properties as
a `Map` or `util.Properties`.
This example is in two parts:  A Handler class which might be an AWS Lambda, and a DAO (Data Access
Object) which stores data to a DB using Hibernate.

```java
package simple;  // Assume all three classes are same pkg.  Imports left out for simplicity.

public class SaleHandler {

  // 1 Declare configuration Property's for this class
  public static interface Config {
    BigDecProp TAX_RATE = BigDecProp.builder().mustBeGreaterThan(BigDecimal.ZERO)
        .mustBeNonNull().desc("Tax rate as a decimal, eg .10").aliasIn("tax").build();
  }
  
  public Object handle(BigDecimal saleAmount) throws Exception {

    // TAX_RATE.getValue() returns a BigDecimal
    BigDecimal tax = saleAmount.multiply(Config.TAX_RATE.getValue());
    return new SaleDao().storeSale(saleAmount.add(tax));
  }
}
```
[Full code in AndHow samples]() \
#### // 1 : Declare configuration Property's for this class
AndHow best practice:  **_Place Properties in the class that uses them_**. \
This makes intuitive sense and there is no need to gather them all into a central _Config_ class -
AndHow will find, load and validate them all **_and_** create a configuration template listing them all.

The `TAX_RATE` Property uses `.aliasIn("tax")`, which adds an alternate name recognized when reading
this property from a configuration source.  Handy for values that may need to be specified on cmd line.

Lets look at how the DAO class uses AndHow:
```java
public class SaleDao {

  // 2 Declare DB connection Properties
  @ManualExportAllowed
  private static interface Db {
    StrProp URL = StrProp.builder().mustStartWith("jdbc://").mustBeNonNull()
        .aliasInAndOut("hibernate.connection.url").build();
    StrProp PWD = StrProp.builder().mustBeNonNull()
        .aliasInAndOut("hibernate.connection.password").build();
  }

  // 3 Export Db properties to java Properties instance
  java.util.Properties getExportedConfig() throws Exception {
    Properties props = AndHow.instance().export(Db.class)
        .collect(ExportCollector.stringProperties(""));

    return props;
  }

  // Pretend database storage call
  Object storeSale(Object sale) throws Exception {
    Hibernate h = new Hibernate(getExportedConfig());
    return h.save(sale);
  }
}
```
[Full code in AndHow samples]() \
#### Section // 3 : Declare DB connection Properties
The Properties bundled together in `Db` are annotated with `@ManualExportAllowed`, allowing
them to be exported to a `Map` or other structure.  `.aliasInAndOut()` adds an _in_ name just like
'tax' above, but the name is also used when exporting (out).

#### Section // 4 : Export Db properties to a java.util.Properties instance
Property exports use the Java `stream()` API.  Exports are done at the class level:
`AndHow.instance().export(class...)` specifies one or more `@ManualExportAllowed` annotated classes
containing AndHow Properties.  `ExportCollector` has collectors that turn a stream of Properties
into collections.  `ExportCollector.stringProperties("")` is a collector that turns AndHow
Properties into `java.util.Properties` with String values (using "" for null values).

Since we specified alias _out_ names for the Db Properties, the aliases are used for the export.
AndHow provides validation at startup, configuration from multiple sources, and more...
and can do that for 3rd party frameworks!

## Configuration by Tier

## Testing Applications with AndHow
Testing can be complicated:  One configuration for unit testing, another for integration testing.
Ideally an application would be tested with many configurations.

AndHow makes this easy...


_**For more examples and documentation, visit the [AndHow main site](https://sites.google.com/view/andhow)**_

_**&?!**_

