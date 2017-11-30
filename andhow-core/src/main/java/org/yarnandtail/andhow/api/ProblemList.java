package org.yarnandtail.andhow.api;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
	public static final ProblemList<Problem> EMPTY_PROBLEM_LIST = new UnmodifiableProblemList<Problem>();
			
	public ProblemList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ProblemList(ProblemList<P> problems) {
		super(problems);
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
	
	public static class UnmodifiableProblemList<P extends Problem> extends ProblemList<P> {
		
		public UnmodifiableProblemList(ProblemList<P> problems) {
			super(problems);
			super.trimToSize();
		}
		
		public UnmodifiableProblemList() {
			super(0);
		}

		@Override
		public boolean add(P problem) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sort(Comparator<? super P> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(UnaryOperator<P> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(Predicate<? super P> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<P> iterator() {
			Iterator<P> delegate = super.iterator();
			return new Iterator<P>() {
				@Override
				public boolean hasNext() {
					return delegate.hasNext();
				}

				@Override
				public P next() {
					return delegate.next();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

        public ListIterator<P> listIterator()   {
			throw new UnsupportedOperationException();	//iterator allows mods, so block
		}

        public ListIterator<P> listIterator(final int index) {
            throw new UnsupportedOperationException();	//iterator allows mods, so block
        }

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends P> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends P> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public P remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, P element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public P set(int index, P element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void ensureCapacity(int minCapacity) {
			return;
		}

		@Override
		public void trimToSize() {
			return;
		}
		
		
		
	}
	
}