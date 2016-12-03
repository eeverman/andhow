package yarnandtail.andhow;

import java.util.List;

/**
 * An exception that causes configuration processing to stop b/c it cannot proceed.
 * 
 * @author eeverman
 */
public class AppFatalException extends RuntimeException {
		
	private final List<PointValueProblem> pointValueProblems;
	private final List<RequirmentProblem> requirementsProblems;
	
	public AppFatalException(String message,
			List<PointValueProblem> pointValueProblems, List<RequirmentProblem> requirementsProblems) {
		super(message);
		
		this.pointValueProblems = pointValueProblems;
		this.requirementsProblems = requirementsProblems;
	}

	public List<PointValueProblem> getPointValueProblems() {
		return pointValueProblems;
	}

	public List<RequirmentProblem> getRequirementsProblems() {
		return requirementsProblems;
	}

}
