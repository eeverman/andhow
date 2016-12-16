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
	private final List<ValueProblem> valueProblems;
	private final List<RequirementProblem> requirementProblems;
	
	public AppFatalException(List<ConstructionProblem> constructProblems) {
		super("There is a problem with the basic setup of the " + AndHow.ANDHOW_INLINE_NAME + " framework. " +
				"Since it is the framework itself that is misconfigured, no attempt was made to load values. " +
				"See System.err, out or the log files for more details.");
		
		this.constructProblems = constructProblems;
		this.valueProblems = Collections.emptyList();
		this.requirementProblems = Collections.emptyList();
	}
	
	public AppFatalException(String message,
			List<ValueProblem> propValueProblems, List<RequirementProblem> requirementProblems) {
		super(message);
		
		this.constructProblems = Collections.emptyList();
		this.valueProblems = propValueProblems;
		this.requirementProblems = requirementProblems;
	}
	
	public List<ConstructionProblem> getConstructionProblems() {
		return constructProblems;
	}

	public List<ValueProblem> getValueProblems() {
		return valueProblems;
	}

	public List<RequirementProblem> getRequirementProblems() {
		return requirementProblems;
	}

}
