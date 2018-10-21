package org.yarnandtail.andhow.compile;

/**
 * Exception thrown by the AndHowCompileProcessor for any type of problem
 * during compile.
 * 
 * This exception can accumulate a list of Problems so that all problems can
 * be found in a single pass and for easier testing (this exception can be 
 * inspected to determine the exact problem).
 * 
 * @author ericeverman
 */
public class AndHowCompileException extends RuntimeException {

	public AndHowCompileException() {
	}


	@Override
	public String getMessage() {
		return super.getMessage(); //To change body of generated methods, choose Tools | Templates.
	}
	
}
