package yarnandtail.andhow;

import java.util.ArrayList;

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
}