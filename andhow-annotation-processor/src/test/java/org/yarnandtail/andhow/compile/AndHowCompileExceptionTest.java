package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author ericeverman
 */
public class AndHowCompileExceptionTest {

	@Test
	public void testAllForProblemConstructor() {
		
		//Setup
		List<CompileProblem> problems = new ArrayList();
		CompileProblem.PropMissingStaticFinal prob1 = new CompileProblem.PropMissingStaticFinal("g1", "p1");
		CompileProblem.PropMissingStaticFinal prob2 = new CompileProblem.PropMissingStaticFinal("g1", "p1");
		problems.clear();
		problems.add(prob1);
		problems.add(prob2);
		
		
		AndHowCompileException ahce = new AndHowCompileException(problems);
		assertNull(ahce.getCause());
		assertEquals(AndHowCompileException.DEFAULT_MSG, ahce.getMessage());
		assertTrue(problems.containsAll(ahce.getProblems()));
		assertTrue(ahce.getProblems().containsAll(problems));
	}
	
	@Test
	public void testNullProblemsShouldNotBombr() {

		AndHowCompileException ahce = new AndHowCompileException(null);
		assertEquals(0, ahce.getProblems().size());
	}

	@Test
	public void testAllForMessageThrowableConstructor() {

		final String MSG = "ABC";
		final Exception E = new Exception();
		
		AndHowCompileException ahce = new AndHowCompileException(MSG, E);
		assertEquals(E, ahce.getCause());
		assertEquals(MSG, ahce.getMessage());
		assertEquals(0, ahce.getProblems().size());
	}
}
