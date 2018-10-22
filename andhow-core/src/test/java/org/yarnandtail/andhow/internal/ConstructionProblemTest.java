package org.yarnandtail.andhow.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.AndHowInit;
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.AndHow;



public class ConstructionProblemTest {

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

}