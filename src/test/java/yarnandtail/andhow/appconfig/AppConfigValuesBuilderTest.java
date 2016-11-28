package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.PropFileLoader;
import yarnandtail.andhow.name.AsIsAliasNamingStrategy;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfigValuesBuilderTest {

	@Test
	public void testBuilder() {
		
		AppConfigStructuredValuesImpl builder = new AppConfigStructuredValuesImpl();
		
		Loader cmdLineLoad = new CmdLineLoader();
		Loader propFileLoad = new PropFileLoader();
		
		Map<ConfigPoint<?>, Object> firstSet = new HashMap();
		
		firstSet.put(SimpleParamsWAlias.KVP_BOB, "test");
		//firstSet.put(SimpleParamsWAlias.KVP_NULL, "not_null");
		firstSet.put(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE);
		firstSet.put(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE);
		firstSet.put(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE);
		
		Map<ConfigPoint<?>, Object> secondSet = new HashMap();
		secondSet.put(SimpleParamsWAlias.KVP_BOB, "blah");
		secondSet.put(SimpleParamsWAlias.KVP_NULL, "blah");
		secondSet.put(SimpleParamsWAlias.FLAG_TRUE, Boolean.TRUE);
		secondSet.put(SimpleParamsWAlias.FLAG_FALSE, Boolean.FALSE);
		secondSet.put(SimpleParamsWAlias.FLAG_NULL, Boolean.FALSE);
		
		builder.addValues(cmdLineLoad, firstSet);
		builder.addValues(propFileLoad, secondSet);
		
		//These should all be the values from the firstSet except KVP_NULL
		assertEquals("test", builder.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", builder.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(false, builder.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, builder.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, builder.getValue(SimpleParamsWAlias.FLAG_NULL));
		
		
		//This should contain all the same as above except KVP_NULL
		Map<ConfigPoint<?>, Object> cmdPoints = builder.getAllValuesLoadedByLoader(cmdLineLoad);
		assertEquals(4, cmdPoints.size());
		assertEquals("test", cmdPoints.get(SimpleParamsWAlias.KVP_BOB));
		assertEquals(false, cmdPoints.get(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, cmdPoints.get(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, cmdPoints.get(SimpleParamsWAlias.FLAG_NULL));
		
		//Again - all the same stuff except KVP_NULL
		cmdPoints = builder.getEffectiveValuesLoadedByLoader(cmdLineLoad);
		assertEquals(4, cmdPoints.size());
		assertEquals("test", cmdPoints.get(SimpleParamsWAlias.KVP_BOB));
		assertEquals(false, cmdPoints.get(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, cmdPoints.get(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, cmdPoints.get(SimpleParamsWAlias.FLAG_NULL));
		
		//
		//The values for just the propFileLoader
		Map<ConfigPoint<?>, Object> propPoints = builder.getAllValuesLoadedByLoader(propFileLoad);
		assertEquals(5, propPoints.size());
		assertEquals("blah", propPoints.get(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", propPoints.get(SimpleParamsWAlias.KVP_NULL));
		assertEquals(true, propPoints.get(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(false, propPoints.get(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(false, propPoints.get(SimpleParamsWAlias.FLAG_NULL));
		
		//The effective values for just the propFileLoader (only one not overridden)
		propPoints = builder.getEffectiveValuesLoadedByLoader(propFileLoad);
		assertEquals(1, propPoints.size());
		assertEquals("blah", propPoints.get(SimpleParamsWAlias.KVP_NULL));
		
		
		//
		//Convert to the production version
		AppConfigValuesImpl prodValues = builder.build();
		assertEquals("test", prodValues.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("blah", prodValues.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(false, prodValues.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(true, prodValues.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(true, prodValues.getValue(SimpleParamsWAlias.FLAG_NULL));
	}
	
}
