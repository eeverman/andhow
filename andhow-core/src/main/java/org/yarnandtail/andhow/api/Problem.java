package org.yarnandtail.andhow.api;

/**
 *
 * @author ericeverman
 */
public interface Problem {
	
	public final static String UNKNOWN = "[[Unknown]]";

	/**
	 * A complete description of the problem w/ context and problem description.
	 * 
	 * Constructed by stringing the problem context and the problem description
	 * together:  [Context]: [Description]
	 *
	 * @return
	 */
	String getFullMessage();
	
	/**
	 * The context for the problem, for the user.
	 * 
	 * For a construction problem for a property, this would be the Property
	 * canonical name.  For an invalid value, it might be the Property name
	 * and where it was loaded from.
	 * 
	 * 
	 * @return 
	 */
	String getProblemContext();
	
	/**
	 * The problem description, for the user.
	 * 
	 * It should be able to be tacked on to the problem context.
	 * @return 
	 */
	String getProblemDescription();

}
