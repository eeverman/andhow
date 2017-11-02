package org.yarnandtail.andhow.api;

/**
 * A Lookup Loader looks up values, possibly in an external system and may not
 * be able to (easily) enumerate all values.
 * 
 * Compare this to a Read Loader that reads properties from a resource, like a
 * properties file, for which it is possible to enumerate all values.  A
 * LookupLoader typically requires some type of environment (such as JNDI) or
 * external connection (such as a db connection).
 * 
 * @author ericeverman
 */
public interface LookupLoader {
	
	/**
	 * If true, then a failure to initiate or connect to the needed environment
	 * or external system will result in a failure and it will register a
	 * 'Propblem' during load which will cause application initiation to stop.
	 * 
	 * @return 
	 */
	boolean isFailedEnvironmentAProblem();
}
