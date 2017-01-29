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
		
	/** Empty immutable */
	public static final ProblemList<Problem> EMPTY_LIST = new ProblemList() {
		@Override
		public boolean add(Problem problem) {
			throw new UnsupportedOperationException();
		}

	};	
			
	public ProblemList(int initialCapacity) {
		super(initialCapacity);
	}


	public ProblemList() {
	}

	@Override
	public boolean add(P problem) {
		if (problem != null) {
			return super.add(problem);
		} else {
			return false;
		}
	}
	
	public <P extends Problem> List<P> filter(Class<P> clazz) {
		return this.stream().filter(p -> clazz.isInstance(p))
				.map(c -> clazz.cast(c)).collect(Collectors.toList());
	}
}