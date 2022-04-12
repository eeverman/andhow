package org.yarnandtail.andhow.internal;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class LoaderProblemTest extends LoaderProblem {

	@Override
	public String getProblemDescription() {
		return "description";
	}

	// Mocks
	Property prop;
	Loader loader;
	LoaderPropertyCoord badValueCoord;	//Real implementation that is spy'ed.

	LoaderPropertyCoord badValueCoordAllSpeced;	//Real implementation that is spy'ed.
	LoaderPropertyCoord badValueCoordAllNull;	//Real implementation that is spy'ed.

	/* Simple Non-abstract class for testing.  Can't be a mock b/c subclasses use internal fields. */
	public class LoaderProblemStub extends LoaderProblem {
		public void setBadValueCoord(LoaderPropertyCoord badValueCoord) {
			this.badValueCoord = badValueCoord;
		}

		@Override
		public String getProblemDescription() {
			return "description";
		}

		public Class<?> getGroup() {
			return this.badValueCoord.group;
		}
	}

	@BeforeEach
	void setUp() {
		prop = Mockito.mock(Property.class);
		loader = Mockito.mock(Loader.class);

		Mockito.when(loader.getSpecificLoadDescription()).thenReturn("LD");

		// Assume that tests will specify badValueCoord.getX return values as needed
		badValueCoord = new LoaderPropertyCoord(null, Integer.class, null);
		badValueCoord = Mockito.spy(badValueCoord);

		badValueCoordAllSpeced = new LoaderPropertyCoord(loader, Integer.class, prop);
		badValueCoordAllSpeced = Mockito.spy(badValueCoordAllSpeced);
		Mockito.when(badValueCoordAllSpeced.getPropName()).thenReturn("PropName");

		badValueCoordAllNull = new LoaderPropertyCoord(null, null, null);
		badValueCoordAllNull = Mockito.spy(badValueCoordAllNull);
	}

	@Test
	public void testNoUniqueNames() {
		GroupProxy refGroup = mock(GroupProxy.class);
		Class someUserClass = Object.class;
		when(refGroup.getProxiedGroup()).thenReturn(someUserClass);
		Property refProperty = mock(Property.class);
		GroupProxy badGroup = mock(GroupProxy.class);
		when(badGroup.getProxiedGroup()).thenReturn(someUserClass);
		Property badProperty = mock(Property.class);
		String conflictName = "conflict name test";
		ConstructionProblem.NonUniqueNames instance = new ConstructionProblem.
				NonUniqueNames(refGroup, refProperty, badGroup, badProperty, conflictName);

		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getConflictName(), conflictName);
		assertEquals(instance.getRefPropertyCoord().getGroup(), someUserClass);
		assertEquals(instance.getBadPropertyCoord().getGroup(), someUserClass);
		assertEquals(instance.getRefPropertyCoord().getProperty(), refProperty);
		assertEquals(instance.getBadPropertyCoord().getProperty(), badProperty);
	}

	@Test
	public void testDuplicateProperty() {
		Class someUserClass = Object.class;
		GroupProxy refGroup = mock(GroupProxy.class);
		when(refGroup.getProxiedGroup()).thenReturn(someUserClass);
		Property refProperty = mock(Property.class);
		GroupProxy badGroup = mock(GroupProxy.class);
		when(badGroup.getProxiedGroup()).thenReturn(someUserClass);
		Property badProperty = mock(Property.class);
		ConstructionProblem.DuplicateProperty instance = new ConstructionProblem.
				DuplicateProperty(refGroup, refProperty, badGroup, badProperty);

		assertNotNull(instance.getBadPropertyCoord());
		assertNotNull(instance.getRefPropertyCoord());
		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getRefPropertyCoord().getGroup(), someUserClass);
		assertEquals(instance.getBadPropertyCoord().getGroup(), someUserClass);
		assertEquals(instance.getRefPropertyCoord().getProperty(), refProperty);
		assertEquals(instance.getBadPropertyCoord().getProperty(), badProperty);
	}

	@Test
	public void testDuplicateLoader() {
		Loader loader = mock(Loader.class);
		ConstructionProblem.DuplicateLoader instance = new ConstructionProblem.DuplicateLoader(loader);

		assertNotNull(instance.getLoader());
		assertNotNull(instance.getProblemContext());
		assertNotNull(instance.getProblemDescription());
	}

	@Test
	public void testLoaderPropertyIsNull() {
		Loader loader = mock(Loader.class);
		ConstructionProblem.LoaderPropertyIsNull instance = new ConstructionProblem.LoaderPropertyIsNull(loader);

		assertNotNull(instance.getLoader());
		assertNotNull(instance.getProblemContext());
		assertNotNull(instance.getProblemContext());
	}

	@Test
	public void testLoaderPropertyNotRegistered() {
		Loader loader = mock(Loader.class);
		Property property = mock(Property.class);
		ConstructionProblem.LoaderPropertyNotRegistered instance = new ConstructionProblem.
				LoaderPropertyNotRegistered(loader, property);

		assertNotNull(instance.getLoader());
		assertNotNull(instance.getProperty());
		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getProperty(), property);
	}

	@Test
	public void testSecurityException() {
		String group = "test class";
		Exception exception = new Exception("test");
		ConstructionProblem.SecurityException instance = new ConstructionProblem.SecurityException(exception, group.getClass());

		assertNotNull(instance.getException());
		assertNotNull(instance.getProblemContext());
		assertNotNull(instance.getProblemDescription());
	}

	@Test
	public void testPropertyNotPartOfGroup() {
		GroupProxy group = mock(GroupProxy.class);
		Class testClass = Object.class;
		when(group.getProxiedGroup()).thenReturn(testClass);
		Property prop = mock(Property.class);

		ConstructionProblem.PropertyNotPartOfGroup instance = new ConstructionProblem.
				PropertyNotPartOfGroup(group, prop);

		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getBadPropertyCoord().getGroup(), testClass);
		assertEquals(instance.getBadPropertyCoord().getProperty(), prop);
	}

	@Test
	public void testExportException() {
		Exception exception = new Exception("test");
		GroupProxy group = mock(GroupProxy.class);
		Class testClass = Object.class;
		when(group.getProxiedGroup()).thenReturn(testClass);
		String message = "test message";
		ConstructionProblem.ExportException instance = new ConstructionProblem.
				ExportException(exception, group, message);

		assertEquals(instance.getException().getMessage(), "test");
		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getBadPropertyCoord().getGroup(), testClass);
	}

	@Test
	public void testInvalidDefaultValue() {
		GroupProxy group = mock(GroupProxy.class);
		Class testClass = Object.class;
		when(group.getProxiedGroup()).thenReturn(testClass);
		Property prop = mock(Property.class);
		String invalidMessage = "test invalid message";
		ConstructionProblem.InvalidDefaultValue instance = new ConstructionProblem.
				InvalidDefaultValue(group, prop, invalidMessage);

		assertEquals(instance.getInvalidMessage(), invalidMessage);
		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getBadPropertyCoord().getGroup(), testClass);
		assertEquals(instance.getBadPropertyCoord().getProperty(), prop);
	}

	@Test
	public void testInvalidValidationConfiguration() {
		GroupProxy group = mock(GroupProxy.class);
		Class testClass = Object.class;
		when(group.getProxiedGroup()).thenReturn(testClass);
		Property property = mock(Property.class);
		Validator valid = mock(Validator.class);

		ConstructionProblem.InvalidValidationConfiguration instance = new ConstructionProblem.
				InvalidValidationConfiguration(group, property, valid);

		assertEquals(instance.getValidator(), valid);
		assertNotNull(instance.getProblemDescription());
		assertEquals(instance.getBadPropertyCoord().getGroup(), testClass);
		assertEquals(instance.getBadPropertyCoord().getProperty(), property);
	}

	@Test
	public void testTooManyAndHowInitInstances() {
		AndHowInit item = mock(AndHowInit.class);
		List<AndHowInit> instances = new ArrayList<AndHowInit>();
		instances.add(item);

		InitializationProblem.TooManyAndHowInitInstances instance = new InitializationProblem.TooManyAndHowInitInstances(instances);

		assertNotNull(instance.getInstanceNames());
		assertNotNull(instance.getProblemDescription());
	}

	@Test
	public void testInitiationLoop() {
		AndHow.Initialization originalInit = mock(AndHow.Initialization.class);
		AndHow.Initialization secondInit = mock(AndHow.Initialization.class);

		InitializationProblem.InitiationLoop instance = new InitializationProblem.InitiationLoop(originalInit, secondInit);

		assertNotNull(instance.getOriginalInit());
		assertNotNull(instance.getSecondInit());
		assertNotNull(instance.getProblemDescription());
		assertNotNull(instance.getFullMessage());
	}

	// The testAbstractGetProblemContextXXX tests getContext() on the abstract LoaderProblem with
	// combinations of badValueCoord properties.  The returned value needs to be human readable,
	// but even more important is that it never throws an exception, which could mask the real problem
	// for the user.
	// Table of badValueCoord property values:
	//
	// badVal'Coord Prop    | Test 1   | Test 2   | Test 3   | Test 4   | Test 5   | Test 6   | Test 7   |
	// ==================================================================================================
	// badValueCoord        | not null | not null | not null | not null | not null | not null | null     |
	// bvc.Loader           | not null | not null | not null | not null | not null | null     | na       |
	// bvc.Group            | not null | null     | null     | null     | null     | null     | na       |
	// bvc.Property         | not null | not null | null     | null     | not null | null     | na       |
	// bvc.Loader.spec'Desc | not null | not null | not null | null     | null     | na       | na       |

	@Test
	public void testAbstractGetProblemContext1() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllSpeced);

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading property PropName from loader LD ({})", loader.getClass().getCanonicalName()),
				lp.getProblemContext());
	}

	@Test
	public void testAbstractGetProblemContext2() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllSpeced);

		Mockito.when(badValueCoordAllSpeced.getGroup()).thenReturn(null);

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading from LD ({})", loader.getClass().getCanonicalName()),
				lp.getProblemContext());
	}

	@Test
	public void testAbstractGetProblemContext3() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllNull);

		Mockito.when(badValueCoordAllNull.getLoader()).thenReturn(loader);

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading from LD ({})", loader.getClass().getCanonicalName()),
				lp.getProblemContext());
	}


	@Test
	public void testAbstractGetProblemContext4() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllNull);

		Mockito.when(badValueCoordAllNull.getLoader()).thenReturn(loader);
		Mockito.when(loader.getSpecificLoadDescription()).thenReturn(null);

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading from {}", loader.getClass().getCanonicalName()),
				lp.getProblemContext());
	}

	@Test
	public void testAbstractGetProblemContext5() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllNull);

		Mockito.when(badValueCoordAllNull.getLoader()).thenReturn(loader);
		Mockito.when(badValueCoordAllNull.getProperty()).thenReturn(prop);
		Mockito.when(loader.getSpecificLoadDescription()).thenReturn(null);

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading from {}", loader.getClass().getCanonicalName()),
				lp.getProblemContext());
	}

	@Test
	public void testAbstractGetProblemContext6() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoordAllNull);

		assertNotNull(lp.getProblemContext());
		assertEquals("Reading from [[ Unknown Loader ]]", lp.getProblemContext());
	}

	@Test
	public void testAbstractGetProblemContext7() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(null);

		assertNotNull(lp.getProblemContext());
		assertEquals("[[ Unknown context ]]", lp.getProblemContext());
	}

	//
	//

	@Test
	public void testAbstractGetMessage() {
		LoaderProblemStub lp = new LoaderProblemStub();
		lp.setBadValueCoord(badValueCoord);

		Mockito.when(badValueCoord.getLoader()).thenReturn(loader);
		Mockito.when(badValueCoord.getProperty()).thenReturn(prop);
		Mockito.when(badValueCoord.getPropName()).thenReturn("PropName");

		assertNotNull(lp.getProblemContext());
		assertEquals(
				TextUtil.format("Reading property PropName from loader LD ({}): description", loader.getClass().getCanonicalName()),
				lp.getFullMessage());
	}


	@Test
	public void testStringConversionLoaderProblem() {
		StringConversionLoaderProblem problem = new StringConversionLoaderProblem(loader, String.class, prop, "some String");
		ValueType vType = new ValueType() {
			@Override
			public Class getDestinationType() {
				return String.class;
			}

			@Override
			public Object parse(String sourceValue) throws ParsingException {
				return null;
			}

			@Override
			public String toString(Object value) {
				return null;
			}

			@Override
			public Object cast(Object o) throws RuntimeException {
				return null;
			}
		};
		LoaderProblemStub lp = Mockito.mock(LoaderProblemStub.class, CALLS_REAL_METHODS);
		lp.setBadValueCoord(badValueCoord);
		Mockito.when(lp.getBadValueCoord().getProperty()).thenReturn(prop);
		Mockito.when(badValueCoord.getProperty().getValueType()).thenReturn(vType);
		assertNotNull(problem.getProblemDescription());
		assertEquals("The string 'some String' could not be converted to type String", problem.getProblemDescription());
	}

	@Test
	public void testObjectConversionValueProblem() {
		ObjectConversionValueProblem problem = new ObjectConversionValueProblem(loader, String.class, prop, "some String");
		ValueType vType = new ValueType() {
			@Override
			public Class getDestinationType() {
				return String.class;
			}

			@Override
			public Object parse(String sourceValue) throws ParsingException {
				return null;
			}

			@Override
			public String toString(Object value) {
				return null;
			}

			@Override
			public Object cast(Object o) throws RuntimeException {
				return null;
			}
		};
		LoaderProblemStub lp = Mockito.mock(LoaderProblemStub.class, CALLS_REAL_METHODS);
		lp.setBadValueCoord(badValueCoord);
		Mockito.when(lp.getBadValueCoord().getProperty()).thenReturn(prop);
		Mockito.when(badValueCoord.getProperty().getValueType()).thenReturn(vType);
		assertNotNull(problem.getProblemDescription());
		assertEquals("The object 'some String' could not be converted to type String", problem.getProblemDescription());
	}

	@Test
	public void testJndiContextLoaderProblem() {
		JndiContextMissing problem = new JndiContextMissing(loader);
		assertNotNull(problem.getProblemDescription());
	}

	@Test
	public void testSourceNotFoundLoaderProblem() {
		String message = "custom message";
		SourceNotFoundLoaderProblem problem = new SourceNotFoundLoaderProblem(loader, message);
		String expected = "Expected for find data for this loader to load from: " + message;
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());

	}

	@Test
	public void testUnknownPropertyLoaderProblem() {
		String unknownPropName = "custom property";
		UnknownPropertyLoaderProblem problem = new UnknownPropertyLoaderProblem(loader, unknownPropName);
		String expected = TextUtil.format("The property '{}' is not recognized", unknownPropName);
		assertEquals(expected, problem.getProblemDescription());
		assertEquals(unknownPropName, problem.getUnknownPropertyName());
	}

	@Test
	public void testsDuplicatePropertyLoaderProblem() {
		DuplicatePropertyLoaderProblem problem = new DuplicatePropertyLoaderProblem(loader, Integer.class, prop);
		String expected = "There are multiple values assigned to this property";
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());
	}

	@Test
	public void testsParsingLoaderProblem() {
		String message = "custom message exception";
		Exception exception = new Exception(message);
		ParsingLoaderProblem problem = new ParsingLoaderProblem(loader, Integer.class, prop, exception);
		assertNotNull(problem.getProblemDescription());
		assertEquals(message, problem.getProblemDescription());
	}

	@Test
	public void testsIOLoaderProblem() {
		String message = "custom message exception";
		String resourcePath = "resource/path";
		Exception exception = new Exception(message);
		IOLoaderProblem problem = new IOLoaderProblem(loader, exception, resourcePath);
		String expected = "There was an IO error while reading from: " + resourcePath + " Original error message: " + exception.getMessage();
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());
	}
}
