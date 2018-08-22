package org.yarnandtail.andhow.property;

import org.junit.Test;

import static org.junit.Assert.*;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.ValueProblem;

/**
 * Tests StrProp instances as they would be used in an app.
 * 
 * Focuses on builder functionality, validation and metadata.
 * 
 * @author ericeverman
 */
public class StrPropTest extends PropertyTestBase {

	@Test
	public void validationGroupGoodValuesTest() {
		this.buildConfig(this, "_validationGroupGood", ValidationGroup.class);
		
		assertEquals("abcdefg", ValidationGroup.USER_NAME.getValue());
		assertEquals("someone@GmAiL.CoM", ValidationGroup.GMAIL_ANY_CASE.getValue());
		assertEquals("someoneselse@gmail.com", ValidationGroup.GMAIL_LOWER_CASE.getValue());
		assertEquals("AbCdefg", ValidationGroup.ABC_ANY_CASE.getValue());
		assertEquals("ABCdefg", ValidationGroup.ABC_UPPER_CASE.getValue());
		
		assertEquals("Lowercase Only", ValidationGroup.USER_NAME.getDescription());
		assertEquals("Ends w/ @gmail.com", ValidationGroup.GMAIL_ANY_CASE.getDescription());
		assertEquals("helpMe", ValidationGroup.GMAIL_LOWER_CASE.getHelpText());
	}
	
	@Test
	public void validationGroupGoodValuesWithQuotesTest() {
		this.buildConfig(this, "_validationGroupGoodWithQuotes", ValidationGroup.class);
		
		assertEquals("abcdefg", ValidationGroup.USER_NAME.getValue());
		assertEquals(" someone@GmAiL.CoM", ValidationGroup.GMAIL_ANY_CASE.getValue());
		assertEquals(" someoneselse@gmail.com", ValidationGroup.GMAIL_LOWER_CASE.getValue());
		assertEquals("AbC    ", ValidationGroup.ABC_ANY_CASE.getValue());
		assertEquals("ABC    ", ValidationGroup.ABC_UPPER_CASE.getValue());
	}
	
	@Test
	public void validationGroupBadValuesTest() {
		
		try {
			this.buildConfig(this, "_validationGroupBad", ValidationGroup.class);
		} catch (AppFatalException e) {
			ProblemList<Problem> problems = e.getProblems();
			assertEquals(5, problems.size());
			
			assertTrue(problems.stream().allMatch(p -> p instanceof ValueProblem));
		}

	}
	
	public interface ValidationGroup {
		StrProp USER_NAME = StrProp.builder().aliasInAndOut("name").mustMatchRegex("[a-z]+")
				.mustBeNonNull().desc("Lowercase Only").build();
		
		StrProp GMAIL_ANY_CASE = StrProp.builder().mustEndWithIgnoreCase("@gmail.com")
				.description("Ends w/ @gmail.com").build();
		
		StrProp GMAIL_LOWER_CASE = StrProp.builder().mustEndWith("@gmail.com")
				.description("Ends w/ '@gmail.com' and is case sensitive")
				.helpText("helpMe").build();
		
		StrProp ABC_ANY_CASE = StrProp.builder().mustStartWithIgnoreCase("abc")
				.description("Starts with 'abc' of any case").build();
				
		StrProp ABC_UPPER_CASE = StrProp.builder().mustStartWith("ABC")
				.description("Starts with 'ABC' exactly").build();
	}
}
