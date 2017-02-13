package yarnandtail.andhow.internal;

import yarnandtail.andhow.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.example.restclient.SampleRestClientGroup;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.PropertyFileFromClasspathLoader;

/**
 *
 * @author eeverman
 */
public class ValueMapWithContextMutableTest {

	@Test
	public void testBuilder() {
		
		ValueMapWithContextMutable builder = new ValueMapWithContextMutable();
		
		Loader cmdLineLoad = new CmdLineLoader();
		Loader propFileLoad = new PropertyFileFromClasspathLoader(SampleRestClientGroup.CLASSPATH_PROP_FILE);
		
		List<PropertyValue> firstSet = new ArrayList();
		
		firstSet.add(new PropertyValue(SimpleParams.STR_BOB, "test"));
		//firstSet.put(SimpleParams.KVP_NULL, "not_null");
		firstSet.add(new PropertyValue(SimpleParams.FLAG_TRUE, Boolean.FALSE));
		firstSet.add(new PropertyValue(SimpleParams.FLAG_FALSE, Boolean.TRUE));
		firstSet.add(new PropertyValue(SimpleParams.FLAG_NULL, Boolean.TRUE));
		LoaderValues firstLoaderValues = new LoaderValues(cmdLineLoad, firstSet, new ProblemList<Problem>());
		
		List<PropertyValue> secondSet = new ArrayList();
		secondSet.add(new PropertyValue(SimpleParams.STR_BOB, "blah"));
		secondSet.add(new PropertyValue(SimpleParams.STR_NULL, "blah"));
		secondSet.add(new PropertyValue(SimpleParams.FLAG_TRUE, Boolean.TRUE));
		secondSet.add(new PropertyValue(SimpleParams.FLAG_FALSE, Boolean.FALSE));
		secondSet.add(new PropertyValue(SimpleParams.FLAG_NULL, Boolean.FALSE));
		LoaderValues secondLoaderValues = new LoaderValues(propFileLoad, secondSet, new ProblemList<Problem>());
		
		builder.addValues(firstLoaderValues);
		builder.addValues(secondLoaderValues);
		
		//
		//Lists of stuff to test b/c lots of the tests deal w/ ensuring that the
		//data is the same regardless of where we read it from
		ArrayList<LoaderValues> lvsToTest = new ArrayList();
		ArrayList<ValueMap> acvsToTest = new ArrayList();
		
		//
		//Test basic class types
		assertTrue(builder.getValueMapWithContextImmutable() instanceof ValueMapWithContextImmutable);
		assertTrue(builder.getValueMapImmutable() instanceof ValueMapImmutable);
		
		//These should all be the values from the firstSet except KVP_NULL
		acvsToTest.clear();
		acvsToTest.add(builder);
		acvsToTest.add(builder.getValueMapWithContextImmutable());
		acvsToTest.add(builder.getValueMapImmutable());
		for (ValueMap acv : acvsToTest) {
			assertEquals("test", acv.getExplicitValue(SimpleParams.STR_BOB));
			assertEquals("blah", acv.getExplicitValue(SimpleParams.STR_NULL));
			assertEquals(false, acv.getExplicitValue(SimpleParams.FLAG_TRUE));
			assertEquals(true, acv.getExplicitValue(SimpleParams.FLAG_FALSE));
			assertEquals(true, acv.getExplicitValue(SimpleParams.FLAG_NULL));
		}
		
		
		
		//This should contain all the same as above except KVP_NULL
		lvsToTest.clear();
		lvsToTest.add(builder.getAllValuesLoadedByLoader(cmdLineLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getAllValuesLoadedByLoader(cmdLineLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(4, lvs.getValues().size());
			assertEquals("test", lvs.getExplicitValue(SimpleParams.STR_BOB));
			assertEquals(false, lvs.getExplicitValue(SimpleParams.FLAG_TRUE));
			assertEquals(true, lvs.getExplicitValue(SimpleParams.FLAG_FALSE));
			assertEquals(true, lvs.getExplicitValue(SimpleParams.FLAG_NULL));
		}
		
		//Again - all the same stuff except KVP_NULL
		lvsToTest.clear();
		lvsToTest.add(builder.getEffectiveValuesLoadedByLoader(cmdLineLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getEffectiveValuesLoadedByLoader(cmdLineLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(4, lvs.getValues().size());
			assertEquals("test", lvs.getExplicitValue(SimpleParams.STR_BOB));
			assertEquals(false, lvs.getExplicitValue(SimpleParams.FLAG_TRUE));
			assertEquals(true, lvs.getExplicitValue(SimpleParams.FLAG_FALSE));
			assertEquals(true, lvs.getExplicitValue(SimpleParams.FLAG_NULL));
		}
		
		//
		//The values for just the propFileLoader - try from builder and unmodifiable
		lvsToTest.clear();
		lvsToTest.add(builder.getAllValuesLoadedByLoader(propFileLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getAllValuesLoadedByLoader(propFileLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(5, lvs.getValues().size());
			assertEquals("blah", lvs.getExplicitValue(SimpleParams.STR_BOB));
			assertEquals("blah", lvs.getExplicitValue(SimpleParams.STR_NULL));
			assertEquals(true, lvs.getExplicitValue(SimpleParams.FLAG_TRUE));
			assertEquals(false, lvs.getExplicitValue(SimpleParams.FLAG_FALSE));
			assertEquals(false, lvs.getExplicitValue(SimpleParams.FLAG_NULL));
		}
		
		//The effective values for just the propFileLoader (only one not overridden)
		lvsToTest.clear();
		lvsToTest.add(builder.getEffectiveValuesLoadedByLoader(propFileLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getEffectiveValuesLoadedByLoader(propFileLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(1, lvs.getValues().size());
			assertEquals("blah", lvs.getExplicitValue(SimpleParams.STR_NULL));
		}
		
	}
	
}
