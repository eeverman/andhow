package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyProblem;
import org.yarnandtail.andhow.test.bulktest.PropExpectations.PropExpectation;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PropProblemAssertions {

	BaseConfig config;
	List<PropExpectations> expects;
	int expectIndex;
	boolean useTrimmedValues;

	public PropProblemAssertions(BaseConfig config, int expectIndex, boolean useTrimmedValues, List<PropExpectations> expects) {
		this.config = config;
		this.expectIndex = expectIndex;
		this.useTrimmedValues = useTrimmedValues;
		this.expects = expects;
	}

	public void assertErrors(AppFatalException afe, String outText, boolean verbose) throws Exception {

		List<PropExpectation> expPropProblems;

		if (useTrimmedValues) {
			expPropProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isPropertyProblemClass(e.getTrimResults().get(expectIndex))).collect(Collectors.toList());
		} else {
			expPropProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isPropertyProblemClass(e.getNoTrimResults().get(expectIndex))).collect(Collectors.toList());
		}

		if (verbose) {
			System.out.println("Expecting " + expPropProblems.size() + " PropertyProblem's");
		}

		List<PropertyProblem> orgActualPropProblems = afe.getProblems().stream()
				.filter(p -> p instanceof PropertyProblem).map(p -> (PropertyProblem)p).collect(Collectors.toList());

		List<PropertyProblem> actualPropProblems = new ArrayList<>();
		actualPropProblems.addAll(orgActualPropProblems);


		for (PropExpectation exp : expPropProblems) {

			Class<? extends PropertyProblem> expProbClass = (Class<? extends PropertyProblem>)
					((useTrimmedValues)?exp.getTrimResults().get(expectIndex):exp.getNoTrimResults().get(expectIndex));

			String expProbClassName = expProbClass.getSimpleName();

			Property prop = exp.getProperty();
			String propName = findPropName(prop, config);

			List<? extends PropertyProblem> actualProb =
					actualPropProblems.stream()
							.filter(p -> expProbClass.isInstance(p))
							.map(p -> (PropertyProblem)p)
							.filter(p -> p.getPropertyCoord().getProperty().equals(prop)).collect(Collectors.toList());

			// Remove the actual problems from the list so there is a short list at the end if mis-matched
			actualPropProblems.removeAll(actualProb);

			if (verbose) {
				System.out.println("Expecting " + propName + " to have a problem of type "
						+ expProbClassName + " - actually found " + actualProb.size());
			}

			assertTrue(actualProb.size() > 0, "The Property '" + propName + "' should have had " +
					"a Problem of type " + expProbClassName + ", but none was found");

			assertTrue(outText.contains(propName), "The message displayed to the user should " +
					"contain the property class name '" + propName +"'");
		}

		if (actualPropProblems.size() > 0) {
			// Uh oh, not all the actual problems were matches - Log the list of unmatched ones
			System.out.println("Some actual PropertyProblems were unexpected - here is the list of them:");
			logPropertyProblems(actualPropProblems);
		}

		assertEquals(0, actualPropProblems.size(),
				"Should have matched all the PropertyProblems, but some did not match expected errors");

	}

	protected String findPropName(final Property p, final BaseConfig config) {

		List<GroupProxy> groups = config.getRegisteredGroups();

		return groups.stream().map(g -> g.getCanonicalName(p))
				.filter(n -> n !=null).findFirst().orElse(null);
	}

	protected boolean isPropertyProblemClass(Object obj) {
		if (obj instanceof Class<?>) {
			Class<?> clazz = (Class<?>)obj;

			return PropertyProblem.class.isAssignableFrom(clazz);
		}

		return false;
	}

	protected boolean isStringConversionProblem(Object obj) {
		if (obj instanceof Class<?>) {
			Class<?> clazz = (Class<?>)obj;

			return LoaderProblem.StringConversionLoaderProblem.class.isAssignableFrom(clazz);
		}

		return false;
	}

	public void logPropertyProblems(List<? extends PropertyProblem> probs) {
		for (PropertyProblem p : probs) {
			System.out.println(" - " + p.getFullMessage());
		}
	}


}
