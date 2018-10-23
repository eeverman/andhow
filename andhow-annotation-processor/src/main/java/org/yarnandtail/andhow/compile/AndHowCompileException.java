package org.yarnandtail.andhow.compile;

import java.util.Collections;
import java.util.List;

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

	final List<CompileProblem> problems;
			
	public AndHowCompileException(List<CompileProblem> problems) {
		if (problems != null) {
			this.problems = problems;
		} else {
			this.problems = Collections.emptyList();
		}
	}


	@Override
	public String getMessage() {
		return "The AndHowCompileProcessor found a problem during compilation " +
				"and threw a fatal exception - See the error details listed above";
	}
	
	public List<CompileProblem> getProblems() {
		return problems;
	}
	
}
