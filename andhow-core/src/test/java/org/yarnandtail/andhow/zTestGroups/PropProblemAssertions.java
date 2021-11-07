package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.internal.PropertyProblem;
import org.yarnandtail.andhow.zTestGroups.PropExpectations.PropExpectation;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;

public class PropProblemAssertions {


	BaseConfig config;
	List<PropExpectations> expects;
	int expectIndex;
	boolean useTrimmedValues;

	public PropProblemAssertions(BaseConfig config, int expectIndex, boolean useTrimmedValues, PropExpectations... expects) {
		this.config = config;
		this.expectIndex = expectIndex;
		this.useTrimmedValues = useTrimmedValues;
		this.expects = Arrays.asList(expects);
	}

	public void assertErrors(boolean verbose) throws Exception {

		//Trick to allow access w/in the lambda
		final AppFatalException[] afeArray = new AppFatalException[1];

		String outText = tapSystemErr(() -> {
			afeArray[0] = assertThrows(AppFatalException.class,
					() -> buildCore(config));
		});

		AppFatalException afe = afeArray[0];

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

		List<PropertyProblem> actualPropProblems = afe.getProblems().stream()
				.filter(p -> p instanceof PropertyProblem).map(p -> (PropertyProblem)p).collect(Collectors.toList());

		assertTrue(actualPropProblems.size() >= expPropProblems.size(),
				"The actual number of PropertyProblem should >= the actual number" +
						"(some values may have multiple validation errors)");

		int actualProbsAccountedFor = 0;

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

			actualProbsAccountedFor+= actualProb.size();		// cross these off the list as accounted for

			if (verbose) {
				System.out.println("Expecting " + propName + " to have a problem of type "
						+ expProbClassName + " - actually found " + actualProb.size());
			}

			assertTrue(actualProb.size() > 0, "The Property '" + propName + "' should have had" +
					" a Problem of type " + expProbClassName + ", but none was found");

			assertTrue(outText.contains(propName), "The message displayed to the user should" +
					"contain the property class name '" + propName +"'");
		}

		assertEquals(actualPropProblems.size(), actualProbsAccountedFor,
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


	protected AndHowCore buildCore(BaseConfig aConfig) {
		AndHowCore core = new AndHowCore(
				aConfig.getNamingStrategy(),
				aConfig.buildLoaders(),
				aConfig.getRegisteredGroups());

		return core;
	}

}
