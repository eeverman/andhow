package org.yarnandtail.andhow.internal;


import org.junit.jupiter.api.Test;
import org.mockito.*;
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

	@Mock
	Property prop;

	@Mock
	Loader loader;

	@Mock
	LoaderPropertyCoord badValueCoord = new LoaderPropertyCoord(loader, Integer.class, prop);

	public class LoaderProblemStub extends LoaderProblem {
		public void setBadValueCoord(LoaderPropertyCoord badValueCoord) {
			this.badValueCoord = badValueCoord;
		}

		@Override
		public String getProblemDescription() {
			return "description";
		}

		public void setGroup() {
			this.badValueCoord.group = (Class<?>) Integer.class;
		}

		public Class<?> getGroup() {
			return this.badValueCoord.group;
		}
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
		ConstructionProblem.SecurityException instance = new ConstructionProblem.
				SecurityException(exception, group.getClass());

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

		ConstructionProblem.TooManyAndHowInitInstances instance = new ConstructionProblem.
				TooManyAndHowInitInstances(instances);

		assertNotNull(instance.getInstanceNames());
		assertNotNull(instance.getProblemDescription());
	}

	@Test
	public void testInitiationLoop() {
		AndHow.Initialization originalInit = mock(AndHow.Initialization.class);
		AndHow.Initialization secondInit = mock(AndHow.Initialization.class);

		ConstructionProblem.InitiationLoopException instance = new ConstructionProblem.
				InitiationLoopException(originalInit, secondInit);

		assertNotNull(instance.getOriginalInit());
		assertNotNull(instance.getSecondInit());
		assertNotNull(instance.getProblemDescription());
		assertNotNull(instance.getFullMessage());
	}

	@Test
	public void testGetProblemContextInput1() {
		MockitoAnnotations.initMocks(this);
		LoaderProblemStub lp = Mockito.mock(LoaderProblemStub.class, CALLS_REAL_METHODS);
		lp.setBadValueCoord(badValueCoord);
		Mockito.when(lp.getBadValueCoord()).thenReturn(badValueCoord);
		Mockito.when(badValueCoord.getLoader()).thenReturn(loader);
		Mockito.when(loader.getSpecificLoadDescription()).thenReturn("some description");
		assertNotNull(lp.getProblemContext());
		assertEquals(lp.getProblemContext(), TextUtil.format("Reading from {}", "some description"));
	}

	@Test
	public void testGetProblemContextInput2() {
		MockitoAnnotations.initMocks(this);
		LoaderProblemStub lp = Mockito.mock(LoaderProblemStub.class, CALLS_REAL_METHODS);
		lp.setBadValueCoord(badValueCoord);
		Mockito.when(lp.getBadValueCoord()).thenReturn(badValueCoord);
		Mockito.when(badValueCoord.getLoader()).thenReturn(loader);
		Mockito.when(lp.getBadValueCoord().getProperty()).thenReturn(prop);
		Mockito.when(badValueCoord.getLoader().getSpecificLoadDescription()).thenReturn("some loader description");
		assertNotNull(lp.getProblemContext());
		assertEquals(lp.getProblemContext(), TextUtil.format("Reading from {}", "some loader description"));
	}

	@Test
	public void testGetProblemContextInput4() {
		MockitoAnnotations.initMocks(this);
		LoaderProblem lp = Mockito.mock(LoaderProblem.class, CALLS_REAL_METHODS);
		assertNotNull(lp.getProblemContext());
		assertEquals(lp.getProblemContext(), "[[Unknown]] context");
	}

	@Test
	public void testGetProblemContextInput5() {
		MockitoAnnotations.initMocks(this);
		LoaderProblemStub lp = Mockito.mock(LoaderProblemStub.class, CALLS_REAL_METHODS);
		lp.setBadValueCoord(badValueCoord);
		Mockito.when(lp.getBadValueCoord()).thenReturn(badValueCoord);
		Mockito.when(lp.getBadValueCoord().getProperty()).thenReturn(prop);
		Mockito.when(badValueCoord.getLoader()).thenReturn(loader);
		Mockito.when(badValueCoord.getPropName()).thenReturn("some property name");
		assertNotNull(lp.getProblemContext());
		assertEquals(lp.getProblemContext(), "Reading via loader [[NULL]]");
	}

	@Test
	public void testStringConversionLoaderProblem() {
		MockitoAnnotations.initMocks(this);
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
		MockitoAnnotations.initMocks(this);
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
		MockitoAnnotations.initMocks(this);
		JndiContextLoaderProblem problem = new JndiContextLoaderProblem(loader);
		String expected = "Attempting to read from the JNDI InitialContext threw an unexpected exception.  " +
				"If there is no JNDI Context available for this application entry point, " +
				"ensure it is marked as optional (the default) or removed from the list of Loaders.";
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());

	}

	@Test
	public void testSourceNotFoundLoaderProblem() {
		MockitoAnnotations.initMocks(this);
		String message = "custom message";
		SourceNotFoundLoaderProblem problem = new SourceNotFoundLoaderProblem(loader, message);
		String expected = "Expected for find data for this loader to load from: " + message;
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());

	}

	@Test
	public void testUnknownPropertyLoaderProblem() {
		MockitoAnnotations.initMocks(this);
		String unknownPropName = "custom property";
		UnknownPropertyLoaderProblem problem = new UnknownPropertyLoaderProblem(loader, unknownPropName);
		String expected = TextUtil.format("The property '{}' is not recognized", unknownPropName);
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());

	}

	@Test
	public void testsDuplicatePropertyLoaderProblem() {
		MockitoAnnotations.initMocks(this);
		DuplicatePropertyLoaderProblem problem = new DuplicatePropertyLoaderProblem(loader, Integer.class, prop);
		String expected = "There are multiple values assigned to this property";
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());
	}

	@Test
	public void testsParsingLoaderProblem() {
		MockitoAnnotations.initMocks(this);
		String message = "custom message exception";
		Exception exception = new Exception(message);
		ParsingLoaderProblem problem = new ParsingLoaderProblem(loader, Integer.class, prop, exception);
		assertNotNull(problem.getProblemDescription());
		assertEquals(message, problem.getProblemDescription());
	}

	@Test
	public void testsIOLoaderProblem() {
		MockitoAnnotations.initMocks(this);
		String message = "custom message exception";
		String resourcePath = "resource/path";
		Exception exception = new Exception(message);
		IOLoaderProblem problem = new IOLoaderProblem(loader, exception, resourcePath);
		String expected = "There was an IO error while reading from: " + resourcePath + " Original error message: " + exception.getMessage();
		assertNotNull(problem.getProblemDescription());
		assertEquals(expected, problem.getProblemDescription());
	}
}
