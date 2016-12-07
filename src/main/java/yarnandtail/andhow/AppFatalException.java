package yarnandtail.andhow;

import java.util.Collections;
import java.util.List;

/**
 * An exception that causes configuration processing to stop b/c it cannot proceed.
 * 
 * @author eeverman
 */
public class AppFatalException extends RuntimeException {
	
	private final List<ConstructionProblem> constructProblems;	
	private final List<PointValueProblem> pointValueProblems;
	private final List<RequirementProblem> requirementsProblems;
	
	public AppFatalException(List<ConstructionProblem> constructProblems) {
		super("There is a problem with the basic setup of the AppConfig instance. " +
				"Since it is the AppConfig itself that is misconfigured, no attempt was made to load values. " +
				"See System.err, out or the log files for more details.");
		
		this.constructProblems = constructProblems;
		this.pointValueProblems = Collections.emptyList();
		this.requirementsProblems = Collections.emptyList();
	}
	
	public AppFatalException(String message,
			List<PointValueProblem> pointValueProblems, List<RequirementProblem> requirementsProblems) {
		super(message);
		
		this.constructProblems = Collections.emptyList();
		this.pointValueProblems = pointValueProblems;
		this.requirementsProblems = requirementsProblems;
	}
	
	public List<ConstructionProblem> getConstructionProblems() {
		return constructProblems;
	}

	public List<PointValueProblem> getPointValueProblems() {
		return pointValueProblems;
	}

	public List<RequirementProblem> getRequirementsProblems() {
		return requirementsProblems;
	}

}
