# AndHow! strong.valid.simple.AppConfiguration #
Strongly typed configuration with detailed validation that is simple to use for web apps, command line or any application environment.

# Features #
*	Strong Typing.  Configuration **Property**s are typed and _only_ return that type.
	The getValue() method of the **StrProp** (String Property) returns a String.
	The getValue() method of an **IntProp** (Integer Property) returns an Integer.
*	Detailed validation.  Each Property can be required or optional.
	Strings can be matched to regex, bounds applied to numbers, etc..
*	Load property values from nearly any property source, including property files, JNDI, command line, environmental variables and system properties.
*	Simple construction via builders.
*	Simple initial configuration via auto-generated sample files.
*	Use configuration Properties in code just like Java constants, e.g.: **MY_CONFIG_PROPERTY.getValue();**
	This eliminates a problematic aspect of most configuration strategies, which require
	value keys buried in your source code, e.g.: `hashMap.get("HiddenMagicKey");`
*	Fail-Fast.  Type conversion and validation are applied at startup, rather than at use.
*	Managed session.  A single application configuration initiation point is enforced.
	Race conditions or missing initiation *fails* rather than returning null for properties.
*	Consolidated and structured application configuration API.  Configuration Properties include
	a type, description, and validation rules.  Properties can be grouped into logical sets
	with descriptions.
	Libraries/modules can present their entire configuration API as a PropertyGroup.
*	Extensible value types and configuration sources.  New types and sources of
	configuration (such as from a db) can be easily added.
*	Clear error messages.  Because Properties include metadata like name, description,
	type and validation rules, detailed error messages can be constructed.
*	Support for legacy applications using System.Properties.
	If your application or library code uses a non-ideal configuration strategy, there is a clean way to port it to AndHow with [minimal or no changes to existing code](https://github.com/eeverman/andhow/wiki/Working-with-Legacy-Applications).