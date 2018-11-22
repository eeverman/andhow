/*
 */
package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import javax.lang.model.element.Element;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.yarnandtail.andhow.compile.CompileProblem.*;
import static org.mockito.Mockito.*;
import org.yarnandtail.andhow.compile.AndHowCompileProcessor.CauseEffect;

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
		
		assertTrue(prob1.isPropertyProblem());
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
		
		assertTrue(prob1.isPropertyProblem());
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
		
		assertTrue(prob1.isPropertyProblem());
		assertEquals("g1", prob1.getGroupName());
		assertEquals("p1", prob1.getPropertyName());
		assertTrue(prob1.getFullMessage().contains("g1"));
		assertTrue(prob1.getFullMessage().contains("p1"));
		
		assertEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}

	@Test
	public void testTooManyInitClasses() {
		Element element1 = mock(Element.class);
		Element element2 = mock(Element.class);
		
		CauseEffect ce1 = new CauseEffect("org.MyClass1", element1);
		CauseEffect ce2 = new CauseEffect("org.MyClass2", element2);
		ArrayList<CauseEffect> ces1 = new ArrayList();
		ces1.add(ce1);
		ces1.add(ce2);
		
		final String INIT_NAME = "SOME_INTERFACE_NAME";
		
		TooManyInitClasses tmi1a = new TooManyInitClasses(INIT_NAME, ces1);
		TooManyInitClasses tmi1null = new TooManyInitClasses(INIT_NAME, null);
		
		assertFalse(tmi1a.isPropertyProblem());
		assertEquals(INIT_NAME, tmi1a.getInitClassName());
		assertTrue(tmi1a.getFullMessage().contains(INIT_NAME));
		assertTrue(tmi1a.getFullMessage().contains(ce1.fullClassName));
		assertTrue(tmi1a.getFullMessage().contains(ce2.fullClassName));
		assertEquals(2, tmi1a.getInstanceNames().size());
		assertTrue(tmi1a.getInstanceNames().contains(ce1.fullClassName));
		assertTrue(tmi1a.getInstanceNames().contains(ce2.fullClassName));	
		assertEquals(0, tmi1null.getInstanceNames().size());

		TooManyInitClasses tmi1b = new TooManyInitClasses(INIT_NAME, ces1);
		
		ArrayList<CauseEffect> ces2 = new ArrayList();
		ces2.add(ce1);
		TooManyInitClasses tmi2 = new TooManyInitClasses(INIT_NAME, ces2);
		
		assertEquals(tmi1a, tmi1b);
		assertEquals(tmi1b, tmi1a);
		assertNotEquals(tmi1a, tmi1null);
		assertNotEquals(tmi1a, null);
		assertNotEquals(tmi1a, new PropMissingFinal("g1", "p1"));
		assertNotEquals(tmi1a, tmi2);
		assertNotEquals(tmi2, tmi1a);
	}
	
	
	@Test
	public void testEqualsAcrossTypes() {
		Integer i1 = 1;
		CompileProblem prob1 = new PropMissingStatic("g1", "p1");
		CompileProblem prob2 = new PropMissingFinal("g1", "p1");
		CompileProblem prob3 = new PropMissingStaticFinal("g1", "p1");
		
		//Just checking
		assertEquals(prob1, prob1);
		assertEquals(prob2, prob2);
		assertEquals(prob3, prob3);
		
		assertNotEquals(prob1, null);
		assertNotEquals(prob1, i1);
		assertNotEquals(prob1, prob2);
		assertNotEquals(prob1, prob3);
		assertNotEquals(prob2, prob3);
	}

}
