The intention is that all Problems are just that:  Problem and not exceptions.  There will be a single AppFatalException, which will then have a list of all recorded Problems.  The reason for that is that in general the configuration and property load should continue as far as possible, so problems should not be considered Exceptions to be thrown, instead, they are facts (a problem) to be recorded and reported.

Currently this is _almost_ the case except currently nothing actually writes the list of problems out when the AppConfig startup stops, and the Loader code has not been converted over to this strategy and still uses its own Exception structure.

These are the different major types of problems w/ subtypes:
*	ConstructionProblem (App level construction issue)
	* NonUniqueNames exception (non-unique names)
	* Duplicate point addition
	* Duplicate Loader
	* Security exception - unable to read fields in ConfigGroups
	* Default value is invalid
	* Validation configuration is invalid
*	LoaderProblem
	* read IO error
	* Parse error where the point is unknown
	* Unfound file (but is indicated to be required)
	* Unrecognized point name  **NOT YET IMPLEMENTED**
*	ValueProblem (Problem and Loader context)
	* Not valid
	* String conversion error
	* Coersion error (from jndi objects)
*	RequirementsProblem (App level configuration issue)
	* Required Point exception
	* Req group