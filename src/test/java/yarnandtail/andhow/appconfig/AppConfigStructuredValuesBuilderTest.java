package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import yarnandtail.andhow.LoaderValues.PointValue;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.PropFileLoader;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesBuilderTest {

	@Test
	public void testBuilder() {
		
		AppConfigStructuredValuesBuilder builder = new AppConfigStructuredValuesBuilder();
		
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
		
		//These should all be the values from the firstSet except KVP_NULL
		assertEquals("test", builder.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", builder.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(false, builder.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, builder.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, builder.getValue(SimpleParamsWAlias.FLAG_NULL));
		
		
		//This should contain all the same as above except KVP_NULL
		LoaderValues cmdLoaderValues = builder.getAllValuesLoadedByLoader(cmdLineLoad);
		assertEquals(4, cmdLoaderValues.getValues().size());
		assertEquals("test", cmdLoaderValues.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals(false, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_NULL));
		
		//Again - all the same stuff except KVP_NULL
		cmdLoaderValues = builder.getEffectiveValuesLoadedByLoader(cmdLineLoad);
		assertEquals(4, cmdLoaderValues.getValues().size());
		assertEquals("test", cmdLoaderValues.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals(false, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, cmdLoaderValues.getValue(SimpleParamsWAlias.FLAG_NULL));
		
		//
		//The values for just the propFileLoader
		LoaderValues propLoaderValues = builder.getAllValuesLoadedByLoader(propFileLoad);
		assertEquals(5, propLoaderValues.getValues().size());
		assertEquals("blah", propLoaderValues.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", propLoaderValues.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(true, propLoaderValues.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(false, propLoaderValues.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(false, propLoaderValues.getValue(SimpleParamsWAlias.FLAG_NULL));
		
		//The effective values for just the propFileLoader (only one not overridden)
		propLoaderValues = builder.getEffectiveValuesLoadedByLoader(propFileLoad);
		assertEquals(1, propLoaderValues.getValues().size());
		assertEquals("blah", propLoaderValues.getValue(SimpleParamsWAlias.KVP_NULL));
		
		
		//
		//Convert to the production version
		AppConfigValues prodValues = builder.getAppConfigValues();
		assertEquals("test", prodValues.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", prodValues.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(false, prodValues.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, prodValues.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, prodValues.getValue(SimpleParamsWAlias.FLAG_NULL));
	}
	
}
