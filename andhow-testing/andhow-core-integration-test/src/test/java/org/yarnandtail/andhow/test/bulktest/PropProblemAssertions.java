package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyProblem;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PropProblemAssertions {

	BaseConfig config;
	List<PropExpectations> expects;
	boolean useTrimmedValues;

	public PropProblemAssertions(BaseConfig config, boolean useTrimmedValues, List<PropExpectations> expects) {
		this.config = config;
		this.useTrimmedValues = useTrimmedValues;
		this.expects = expects;
	}

	public void assertPropertyErrors(AppFatalException afe, String outText, boolean verbose) throws Exception {

		List<PropExpectation> expPropProblems;

		if (useTrimmedValues) {
			expPropProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isPropertyProblemClass(e.getTrimResult())).collect(Collectors.toList());
		} else {
			expPropProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isPropertyProblemClass(e.getNoTrimResult())).collect(Collectors.toList());
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
					((useTrimmedValues)?exp.getTrimResult():exp.getNoTrimResult());

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


	public void assertLoaderErrors(AppFatalException afe, String outText, boolean verbose) throws Exception {

		List<PropExpectation> expLoadProblems;

		if (useTrimmedValues) {
			expLoadProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isLoaderProblemClass(e.getTrimResult())).collect(Collectors.toList());
		} else {
			expLoadProblems = expects.stream().flatMap(es -> es.getExpectations().stream())
					.filter(e -> isLoaderProblemClass(e.getNoTrimResult())).collect(Collectors.toList());
		}

		if (verbose) {
			System.out.println("Expecting " + expLoadProblems.size() + " LoaderProblems");
		}

		List<LoaderProblem> orgActualLoadProblems = afe.getProblems().stream()
				.filter(p -> p instanceof LoaderProblem).map(p -> (LoaderProblem)p).collect(Collectors.toList());

		List<LoaderProblem> actualLoadProblems = new ArrayList<>();
		actualLoadProblems.addAll(orgActualLoadProblems);


		for (PropExpectation exp : expLoadProblems) {

			Class<? extends LoaderProblem> expProbClass = (Class<? extends LoaderProblem>)
					((useTrimmedValues)?exp.getTrimResult():exp.getNoTrimResult());

			String expProbClassName = expProbClass.getSimpleName();

			Property prop = exp.getProperty();
			String propName = findPropName(prop, config);

			List<? extends LoaderProblem> actualProb =
					actualLoadProblems.stream()
							.filter(p -> expProbClass.isInstance(p))
							.map(p -> (LoaderProblem)p)
							.filter(p -> p.getBadValueCoord().getProperty().equals(prop)).collect(Collectors.toList());

			// Remove the actual problems from the list so there is a short list at the end if mis-matched
			actualLoadProblems.removeAll(actualProb);

			if (verbose) {
				System.out.println("Expecting " + propName + " to have a problem of type "
						+ expProbClassName + " - actually found " + actualProb.size());
			}

			assertTrue(actualProb.size() > 0, "The Property '" + propName + "' should have had " +
					"a Problem of type " + expProbClassName + ", but none was found");

			assertTrue(outText.contains(propName), "The message displayed to the user should " +
					"contain the property class name '" + propName +"'");
		}

		if (actualLoadProblems.size() > 0) {
			// Uh oh, not all the actual problems were matches - Log the list of unmatched ones
			System.out.println("Some actual LoaderProblems were unexpected - here is the list of them:");
			logPropertyProblems(actualLoadProblems);
		}

		assertEquals(0, actualLoadProblems.size(),
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

	protected boolean isLoaderProblemClass(Object obj) {
		if (obj instanceof Class<?>) {
			Class<?> clazz = (Class<?>)obj;

			return LoaderProblem.class.isAssignableFrom(clazz);
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

	public void logPropertyProblems(List<? extends Problem> probs) {
		for (Problem p : probs) {
			System.out.println(" - " + p.getFullMessage());
		}
	}


}
