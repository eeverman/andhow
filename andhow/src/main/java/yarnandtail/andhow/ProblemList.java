package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple List of Problems that refuses to add nulls.
 * 
 * This simplifies logic in several places where Problems may be returned from
 * an operation and added to a list, but null is return if there is no problem.
 * 
 * @author ericeverman
 * @param <P> A subclass of Problem
 */
public class ProblemList<P extends Problem> extends ArrayList<P> {

	@Override
	public boolean add(P problem) {
		if (problem != null) {
			return super.add(problem);
		} else {
			return false;
		}
	}
	
	public List<ConstructionProblem> getConstructionProblems() {
		return this.stream().filter(p -> p instanceof ConstructionProblem)
				.map(c -> (ConstructionProblem)c).collect(Collectors.toList());
	}

	public List<LoaderProblem> getLoaderProblems() {
		return this.stream().filter(p -> p instanceof LoaderProblem)
				.map(c -> (LoaderProblem)c).collect(Collectors.toList());
	}

	public List<ValueProblem> getValueProblems() {
		return this.stream().filter(p -> p instanceof ValueProblem)
				.map(c -> (ValueProblem)c).collect(Collectors.toList());
	}

	public List<RequirementProblem> getRequirementProblems() {
		return this.stream().filter(p -> p instanceof RequirementProblem)
				.map(c -> (RequirementProblem)c).collect(Collectors.toList());
	}
}