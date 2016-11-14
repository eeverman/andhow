AndHow
======

Simple typed and validated configuration loading for web apps, command line or 
any environment application.

ToDo
----
*	There needs to be a way to use AH for ServletConfig in an isolated scope.
	These will need to be initiated out of sequence from the main AppConfig.
	They may have no relation to the AppConfig.  Perhaps a separate instance-
	based AppConfig (one that you create and hold a reference too, instead of
	a static single instance one).
*	Direct individual parms to be written to SytemProps (or other).  This would
	expose writable system properties to the system.  For instance, if there
	is a system level connection timeout that reads a system property to find
	a default value, creating a ConfigPoint and marking is as 'write to sys props'
	with a specific name would provide a way to config that thru AH.
*	Add a CONFIG_POINT.default(value) that creates an argument for the AppConfig
	that sets a default at creation time.
*	Create a JNDI loader.  This will expose some issues w/ dealing w/ configed
	values that are non-string.  For instance, a JNDI param may already be an
	Integer, instead of needing to be converted from a string.
*	The AppConfig should initial creating should have a key that is returned
	to allow resets for testing.


