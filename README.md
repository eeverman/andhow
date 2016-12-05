AndHow
======

Simple typed and validated configuration loading for web apps, command line or 
any environment application.

Goals
--------------
*	Strong Typing.  Each Configuration Point *only* returns its appropriate type.
	String Config Points have a getValue() method that returns a String.
	Integer Points have a getValue() method that returns an Int.
*	Flexible and early validation.
*	Expressions.
*	No 'magic strings' used to access configuration params.  In fact, the application
	or library does not know or care what the parameter name is.
	Instead, `ConfigPoints` are completely defined as a type of
	**static final ConfigPoint MY_CONFIG_POINT = ...** and accessing its configured value is simply:
	**MY_CONFIG_POINT.getValue();**
	which is also used directly to access its value.
*	Fail-Fast for required configuration points.  Config Points can be marked
	required.  If a required point is not provided, AndHow will report the issue
	the application will report the issue at startup (and stop), rather than
	waiting until the bit of code using that parameter discovers its missing.
*	Support for library/module usage.  A module can declare its configuration
	points and mark them as required, again, catching these missing parameters
	at startup.
*	Easy documentation of config params, example usage and clear error messages.
