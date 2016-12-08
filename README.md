AndHow!  strong.valid.simple.App_Configuration
======

A strongly typed configuration framework with detailed validation that is simple to use, 
for web apps, command line or any application environment.

Goals
--------------
*	Strong Typing.  Each Configuration Point *only* returns its appropriate type.
	The getValue() method of a String ConfigPoint returns a String.
	The getValue() method of an Integer ConfigPoint returns an Integer.
*	Detailed validation.  Each ConfigPoint can be specified as required vs optional.
	Strings can be matched to regex, bounds applied to numbers.
*	Simple setup via builders.  Simple configuration via auto-generated sample files.
*	No properties names hidden in source code. Application code uses hard references
	to ConfigPoint objects which supply configured values, as in: **MY_CONFIG_POINT.getValue();**
*	Fail-Fast.  Type conversion and validation are applied at startup, rather than at use.
*	Managed session.  A single application configuration initiation point is enforced.
	Race conditions or missing initiation *fails* rather than returning null for properties.
*	Consolidated and structured application configuration API.  ConfigPoints include
	a type, description, and validation rules.  Points can be grouped into logical sets
	with descriptions.
	Libraries or modules can present their entire configuration API as a ConfigPointGroup.
*	Extensible value types and configuration sources.  New types and sources of
	configuration (such as from a db) can be easily added.
*	Clear error messages.  Using ConfigPoint validation rules and type data,
	detailed error messages can be constructed.