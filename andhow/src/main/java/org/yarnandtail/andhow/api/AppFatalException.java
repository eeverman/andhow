package org.yarnandtail.andhow.api;

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
			this.problems = ProblemList.EMPTY_PROBLEM_LIST;
		}
	}
	
	public AppFatalException(String message, Problem problem) {
		super(message);
		
		if (problem != null) {
			this.problems = new ProblemList();
			this.problems.add(problem);
		} else {
			this.problems = ProblemList.EMPTY_PROBLEM_LIST;
		}
	}
	
	public ProblemList<Problem> getProblems() {
		return problems;
	}
	
}
