package org.yarnandtail.andhow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An exception that causes configuration processing to stop b/c it cannot proceed.
 * 
 * @author eeverman
 */
public class AppFatalException extends RuntimeException {
	
	
	private final ProblemList<Problem> problems;
	
	/**
	 * Used for errors while the AndHow builder is still in use - we don't have
	 * full context b/c AndHow has not been constructed yet.
	 * @param message 
	 */
	public AppFatalException(String message) {
		super(message);
		this.problems = new ProblemList();
	}
	
	public AppFatalException(String message, ProblemList<Problem> problems) {
		super(message);
		
		if (problems != null) {
			this.problems = problems;
		} else {
			this.problems = new ProblemList();
		}
	}
	
	public ProblemList<Problem> getProblems() {
		return problems;
	}
	
}
