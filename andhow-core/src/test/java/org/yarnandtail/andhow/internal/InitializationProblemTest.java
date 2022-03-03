package org.yarnandtail.andhow.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Most of the testing for this class and its subclasses are in AndHowTest.
 * A few scraps of functionality are tested here.
 */
class InitializationProblemTest {

	@Test
	public void problemDescriptionShouldReturnText() {
		InitializationProblem initProb = new InitializationProblem() {
			@Override
			public String getProblemDescription() {
				return "desc";
			}
		};

		assertEquals("AndHow was unable to initialize", initProb.getProblemContext());
		assertEquals("desc", initProb.getProblemDescription());
		assertEquals("AndHow was unable to initialize: desc", initProb.getFullMessage());
	}
}