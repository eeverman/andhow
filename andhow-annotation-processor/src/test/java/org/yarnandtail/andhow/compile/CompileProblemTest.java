/*
 */
package org.yarnandtail.andhow.compile;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.yarnandtail.andhow.compile.CompileProblem.*;

/**
 *
 * @author ericeverman
 */
public class CompileProblemTest {
	
	public CompileProblemTest() {
	}


	@Test
	public void testBasicPropsOfPropMissingStatic() {
		PropMissingStatic prob1 = new PropMissingStatic("g1", "p1");
		PropMissingStatic prob2 = new PropMissingStatic("g1", "p1");
		PropMissingStatic prob3 = new PropMissingStatic("Xg1", "Xp1");
		
		assertEquals("g1", prob1.getGroupName());
		assertEquals("p1", prob1.getPropertyName());
		assertTrue(prob1.getFullMessage().contains("g1"));
		assertTrue(prob1.getFullMessage().contains("p1"));
		
		assertEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}

	@Test
	public void testBasicPropsOfPropMissingFinal() {
		PropMissingFinal prob1 = new PropMissingFinal("g1", "p1");
		PropMissingFinal prob2 = new PropMissingFinal("g1", "p1");
		PropMissingFinal prob3 = new PropMissingFinal("Xg1", "Xp1");
		
		assertEquals("g1", prob1.getGroupName());
		assertEquals("p1", prob1.getPropertyName());
		assertTrue(prob1.getFullMessage().contains("g1"));
		assertTrue(prob1.getFullMessage().contains("p1"));
		
		assertEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}

	@Test
	public void testBasicPropsOfPropMissingStaticFinal() {
		PropMissingStaticFinal prob1 = new PropMissingStaticFinal("g1", "p1");
		PropMissingStaticFinal prob2 = new PropMissingStaticFinal("g1", "p1");
		PropMissingStaticFinal prob3 = new PropMissingStaticFinal("Xg1", "Xp1");
		
		assertEquals("g1", prob1.getGroupName());
		assertEquals("p1", prob1.getPropertyName());
		assertTrue(prob1.getFullMessage().contains("g1"));
		assertTrue(prob1.getFullMessage().contains("p1"));
		
		assertEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}
	
	@Test
	public void testEqualsAcrossTypes() {
		CompileProblem prob1 = new PropMissingStatic("g1", "p1");
		CompileProblem prob2 = new PropMissingFinal("g1", "p1");
		CompileProblem prob3 = new PropMissingStaticFinal("g1", "p1");
		
		assertNotEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}

}
