package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.internal.ValueMapImmutable;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.internal.ValueMapWithContextImmutable;
import yarnandtail.andhow.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.PropFileLoader;

/**
 *
 * @author eeverman
 */
public class ValueMapWithContextMutableTest {

	@Test
	public void testBuilder() {
		
		ValueMapWithContextMutable builder = new ValueMapWithContextMutable();
		
		Loader cmdLineLoad = new CmdLineLoader();
		Loader propFileLoad = new PropFileLoader();
		
		List<PointValue> firstSet = new ArrayList();
		
		firstSet.add(new PointValue(SimpleParamsWAlias.KVP_BOB, "test"));
		//firstSet.put(SimpleParamsWAlias.KVP_NULL, "not_null");
		firstSet.add(new PointValue(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE));
		firstSet.add(new PointValue(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE));
		firstSet.add(new PointValue(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE));
		LoaderValues firstLoaderValues = new LoaderValues(cmdLineLoad, firstSet);
		
		List<PointValue> secondSet = new ArrayList();
		secondSet.add(new PointValue(SimpleParamsWAlias.KVP_BOB, "blah"));
		secondSet.add(new PointValue(SimpleParamsWAlias.KVP_NULL, "blah"));
		secondSet.add(new PointValue(SimpleParamsWAlias.FLAG_TRUE, Boolean.TRUE));
		secondSet.add(new PointValue(SimpleParamsWAlias.FLAG_FALSE, Boolean.FALSE));
		secondSet.add(new PointValue(SimpleParamsWAlias.FLAG_NULL, Boolean.FALSE));
		LoaderValues secondLoaderValues = new LoaderValues(propFileLoad, secondSet);
		
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
			assertEquals("test", acv.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
			assertEquals("blah", acv.getExplicitValue(SimpleParamsWAlias.KVP_NULL));
			assertEquals(false, acv.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
			assertEquals(true, acv.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
			assertEquals(true, acv.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
		}
		
		
		
		//This should contain all the same as above except KVP_NULL
		lvsToTest.clear();
		lvsToTest.add(builder.getAllValuesLoadedByLoader(cmdLineLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getAllValuesLoadedByLoader(cmdLineLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(4, lvs.getValues().size());
			assertEquals("test", lvs.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
			assertEquals(false, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
			assertEquals(true, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
			assertEquals(true, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
		}
		
		//Again - all the same stuff except KVP_NULL
		lvsToTest.clear();
		lvsToTest.add(builder.getEffectiveValuesLoadedByLoader(cmdLineLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getEffectiveValuesLoadedByLoader(cmdLineLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(4, lvs.getValues().size());
			assertEquals("test", lvs.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
			assertEquals(false, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
			assertEquals(true, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
			assertEquals(true, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
		}
		
		//
		//The values for just the propFileLoader - try from builder and unmodifiable
		lvsToTest.clear();
		lvsToTest.add(builder.getAllValuesLoadedByLoader(propFileLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getAllValuesLoadedByLoader(propFileLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(5, lvs.getValues().size());
			assertEquals("blah", lvs.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
			assertEquals("blah", lvs.getExplicitValue(SimpleParamsWAlias.KVP_NULL));
			assertEquals(true, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
			assertEquals(false, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
			assertEquals(false, lvs.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
		}
		
		//The effective values for just the propFileLoader (only one not overridden)
		lvsToTest.clear();
		lvsToTest.add(builder.getEffectiveValuesLoadedByLoader(propFileLoad));
		lvsToTest.add(builder.getValueMapWithContextImmutable().getEffectiveValuesLoadedByLoader(propFileLoad));
		for (LoaderValues lvs : lvsToTest) {
			assertEquals(1, lvs.getValues().size());
			assertEquals("blah", lvs.getExplicitValue(SimpleParamsWAlias.KVP_NULL));
		}
		
	}
	
}
