_This is just a start w/ bits added as they come up_

## Property Names are CaSe InSeNsItIvE
For ease of use and compatibility with Windows environment variable names, property names are case insensitive\*.  Proper case names are still used internally for reporting and when creating configuration samples, but values are read and recognize from configuration sources regardless of case.

There is one exception:  JNDI is inherently case sensitive.  When loading from most configuration sources, loaders read through the list of available property names from the source to see if any names match up with any known names.  This isn't possible with JNDI, which may tie into other systems (like LDAP) and have a nearly limitless number of values.  Instead, the JNDI loader attempts to lookup the names of known properties in JNDI to see if they exist.  The loader cannot try every combination of capitalization, thus, the JNDI loader must remain case sensitive.

\* If you know your code will not be running on a Windows system, it is possible to replace the `BasicNamingStrategy` with a custom implementation that would retain case sensitivity.

## Unrecognized Property Names
Where possible, loaders consider unrecognized property names an error and the application startup will abort.  This is generally a _good_ thing because its possible an unrecognized name is a mistake.  Its also fairly easy to add parameters to the list of expected parameters when dealing with legacy systems.

See the documentation for each loader to see which loaders consider unrecognized properties an error vs ignoring them.

## Whitespace Handling
Whitespace is generally removed from values and is handled by each Property by a `Trimmer` class.  The `StrProp` uses the `QuotedSpacePreservingTrimmer` which will preserve whitespace inside of double quotes.  All other standard properties use the `TrimToNullTrimmer`.

The `TrimToNullTrimmer` simply removes all whitespace on either end of a value.  If the result is a zero length string, the value becomes null.  This is appropriate for almost every type of value except for strings where you might actually want to configure a string of spaces (or a tab, which would be better) as line indents for a code formatter.

Since whitespace is handled differently across source formats, the `QuotedSpacePreservingTrimmer` (_QSPT_) uses quoted text to 'solve' whitespace in most situations for string values.  The _QSPT_ first trims to null, then, if the remaining text begins and ends with double quotes, those quotes are removed and the string inside the quotes, including whitespace, is preserved as the actual value.

Here are a few examples of the _QSPT_ trimming behavior, using ... to represent whitespace and :: to separate the raw value on the left from the trimmed value on the right:
* ...&nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp;\[\[null]] &nbsp;&nbsp;&nbsp;_(An all whitespace raw value is trimmed to null)_
* \[\[null]] &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; \[\[null]]
* ...abc... &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; abc &nbsp;&nbsp;&nbsp;_(whitespace on either side of text removed)_
* "...abc..." &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; ...abc... &nbsp;&nbsp;&nbsp;_(Quotes are removed and all characters inside preserved)_
* ..."...abc..."... &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; ...abc... &nbsp;&nbsp;&nbsp;_(same result - whitespace outside the quotes is removed)_
* ...some "words" you said... &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; some "words" you said &nbsp;&nbsp;&nbsp;_(No special quote handling here)_
* ..".some "words" you said.".. &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; .some "words" you said.
* .."".. &nbsp;&nbsp;&nbsp;&nbsp;::&nbsp;&nbsp;&nbsp;&nbsp; \[\[Empty String]] &nbsp;&nbsp;&nbsp;_(Using quotes, it is possible to assign an empty string)_

## Null Handling
In most of the property sources (property files, cmd line, etc), there is no direct way to indicate a null, or to distinguish it from an empty string.  The currently handling is this:
* Attempting to set a property (other than a Flag type property) to an empty or all whitespace will be ignored.  The Trimmer trims the value to null, and null values are ignored.
* Setting a Flag type property to an empty string or all whitespace is considered turning the flag 'on' and is equivalent to setting it equal to true.
* _A pending feature is to add a special String value that would allow explicitly setting nulls._

## Working with Loaders
Loaders handle the details of reading properties from different types of sources.  Loaders try to isolate you from the quirks of those sources, but that is not always possible.  

Loaders consider it an error to find a duplicate entry for a property - This is by design.  It is expected that multiple loaders will be used and that those loaders may each find a value for the same property - In that case the first loader to find a property 'wins' for assigning that value.  By contrast, if the command line arguments contain two entries for the same property, the `CmdLineLoader` sees that as ambiguous and will stop the application startup.

Loaders have minor quirks due to their underlying data sources.  Most are minor and won't affect you unless you are dealing with edge cases, such as attempting to assign empty strings or whitespace as values.  Below is a summary of known Loader quirks.

### `StringArgumentLoader` - Implicitly used to load Properties from command line arguments
This loader is used implicitly when `AndHow.builder().cmdLineArgs(String[] args)` is called.
It can also be used explicitly by adding it as a loader with a list of string arguments to be parsed in its constructor.
After the StringArgumentLoader receives the value, each individual Property will use its Trimmer to remove whitespace according to its own rules.  Generally that means the `QuotedSpacePreservingTrimmer` for strings and the `TrimToNullTrimmer` for everything else.
_TODO: Need examples here, but they will need to be separate for bash and windows_

### `PropertyFileOnClasspathLoader` & `PropertyFileOnFilesystemLoader` - Loads Properties from a java .properties file
These two loaders use the `java.util.Properties` class to do read properties, so several behaviours are determined by that class. 

In rare cases, whitespace handling of the JVM Properties file parser may be an issue.  The property value is generally terminated by the end of the line. White space following the property value is not ignored, and is treated as part of the property value.  This is not a problem in most cases because by default, properties have Trimmers that remove whitespace.  Other Trimmer implementations can be assigned to properties, however, so be aware of the implementations if your are using non-default Trimmers.

These loaders are unable to detect duplicate properties (i.e., the same key value appearing more than once in a file).   Instead of aborting the application startup with an error, only the last of the property values in the file is assigned.  This is a basic limitation of the JVM `Properties` class, which silently ignores multiple entries, each value overwriting the last.

### `JndiLoader` - Loads Properties from the JNDI Context
Most web or service applications use JNDI for configuration.  The JNDI context is typically provided by the application container (such as Tomcat, JBoss or Jetty) and how JNDI parameters are configured varies by vendor.  Using Tomcat as an example, an entry in the context.xml file like this:
```
<Context>
  ...
  <Environment name="org.acme.RETRY_SETTING" value="10" type="java.lang.Integer" override="false"/>
  ...
</Context>
```
would create an JNDI resource with an `Integer` value of 10 at the URL `java:comp/env/org.acme.RETRY_SETTING`.
For similar configuration other vendors may place that value at `java:org.acme.RETRY_SETTING`.

Given the variability, both common locations are searched: `comp/env/` or `[NO ROOT]`.  The JndiLoader will also look for properties named with more _JNDI-ish_ style names, swapping the dots for slashes in both standard canonical property names and aliases.  From the example above, the loader would look for RETRY_SETTING at the following JNDI URLs:
* java:comp/env/org/acme/RETRY_SETTING
* java:org/acme/RETRY_SETTING
* java:comp/env/org.acme.RETRY_SETTING
* java:org.acme.RETRY_SETTING

You can change or augment the JNDI roots (the comp/env portion of the url) that are tried by setting `yarnandtail.andhow.load.JndiLoader.CONFIG` properties.  It is considered a fatal error to find multiple entries for a property at these URL variations, since they would be ambiguous.

JNDI configuration is already typed - In the example above, the "10" is already converted to an Integer of value 10.  Thus, the JndiLoader expects to find either a value of the exact same type as the destination Property, or, a string that can be converted to that type (just like other loaders).  No type conversions, such as from an Integer to a Long, are done and are considered fatal errors.  String values are assumed to already be in final form and no trimming is done of string values, thus, whitespace padding will be preserved.
Since JNDI is used widely for multiple types of configuration and resources, the JndiLoader does not complain if it finds unrecognized URLs in the JNDI context.

## `EnviromentVariableLoader` and `SystemPropertyLoader` - Load properties from the host environment / system properties
The host environment and system properties both function as big hashmaps.  Thus, string to type conversion takes place for these incoming properties.  Environment variables are ALL CAPS on Windows systems and case sensitive on all other systems - This is the primary reason why AndHow is case insensitive by default.
## `FixedValueLoader` - A debug and/or test loader not intended for production
To set a fixed configuration state for unit testing, this loader can be used to hard-code configuration values for tests.  Note that AndHow will complain (i.e., blow up with a fatal RuntimeException) if you try to re-initiate it with new values, as you would normally do in a series of unit tests.  To allow this type of testing to happen, you need an `AndHow.Reloader`.  To do that, create a test base class [like this](https://github.com/eeverman/andhow/blob/master/andhow/src/test/java/org/yarnandtail/andhow/AndHowTestBase.java), which includes the `AndHow.Reloader` as a static field.  In test classes, subclass your base class and use this pattern of building AndHow:
```Java
AndHow.builder()
	...
	.reloadForNonPropduction(reloader); //this instead of the typical .build();
```