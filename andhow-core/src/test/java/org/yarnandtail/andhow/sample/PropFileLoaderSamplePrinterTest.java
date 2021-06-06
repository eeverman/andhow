/*
 */
package org.yarnandtail.andhow.sample;

import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.GroupProxyMutable;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.internal.StaticPropertyConfigurationMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.TextUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class PropFileLoaderSamplePrinterTest {
	
	StaticPropertyConfigurationMutable config;
	GroupProxyMutable groupProxy1;
	
	public static interface Config {
		IntProp MY_PROP1 = IntProp.builder().build();
		StrProp MY_PROP2 = StrProp.builder().defaultValue("La la la").desc("mp description")
				.helpText("Long text on how to use the property").mustStartWith("La").mustEndWith("la")
				.mustBeNonNull().aliasIn("mp2").aliasInAndOut("mp2_alias2").aliasOut("mp2_out").build();
	}
	
	@BeforeEach
	public void setup() {
		config = new StaticPropertyConfigurationMutable(new CaseInsensitiveNaming());
		
		groupProxy1 = new GroupProxyMutable(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName(),
				PropFileLoaderSamplePrinterTest.class.getCanonicalName() + "$Config"
		);
		groupProxy1.addProperty(new NameAndProperty("MY_PROP1", Config.MY_PROP1));
		groupProxy1.addProperty(new NameAndProperty("MY_PROP2", Config.MY_PROP2));
		
		assertNull(config.addProperty(groupProxy1, Config.MY_PROP1));
		assertNull(config.addProperty(groupProxy1, Config.MY_PROP2));

	}
	

	@Test
	public void generalTest() throws UnsupportedEncodingException {
		
		TestPrintStream out = new TestPrintStream();
		PropFileLoaderSamplePrinter printer = new PropFileLoaderSamplePrinter();
		String[] lines;	//The output line array
		
		
		//Print Sample start
		out.reset();
		printer.printSampleStart(config, out);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(6, lines.length);
		assertEquals("# " + TextUtil.repeat("##", 45), lines[0]);
		assertEquals("# Sample properties file generated by AndHow!", lines[1]);
		assertEquals("# strong.simple.valid.AppConfiguration  -  https://github.com/eeverman/andhow", lines[2]);
		assertEquals("# Note: When reading property names, matching is done in a case insensitive way, so 'Bob'", lines[3]);
		assertEquals("# 	would match 'bOB'.", lines[4]);
		assertEquals("# " + TextUtil.repeat("##", 45), lines[5]);
		
		//Print group header
		out.reset();
		printer.printPropertyGroupStart(config, out, groupProxy1);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(2, lines.length);
		assertEquals("# " + TextUtil.repeat("##", 45), lines[0]);
		assertEquals(
				"# Property Group " + PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName(),
				lines[1]);
		
		//Print MY_PROP1
		out.reset();
		printer.printProperty(config, out, groupProxy1, Config.MY_PROP1);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(3, lines.length);
		assertEquals("# ", lines[0]);
		assertEquals("# MY_PROP1 (Integer)", lines[1]);
		assertEquals(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName() +
						".MY_PROP1 = [Integer]",
				lines[2]);
		
		//Print MY_PROP2
		out.reset();
		printer.printProperty(config, out, groupProxy1, Config.MY_PROP2);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(9, lines.length);
		assertEquals("# ", lines[0]);
		assertEquals("# MY_PROP2 (String) NON-NULL - mp description", lines[1]);
		assertEquals("# Recognized aliases: mp2, mp2_alias2", lines[2]);
		assertEquals("# Default Value: La la la", lines[3]);
		assertEquals("# Long text on how to use the property", lines[4]);
		assertEquals("# The property value must:", lines[5]);
		assertEquals("# - start with 'La'", lines[6]);
		assertEquals("# - end with 'la'", lines[7]);
		assertEquals(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName() +
						".MY_PROP2 = La la la",
				lines[8]);
		
		//Print group closing (should be empty line)
		out.reset();
		printer.printPropertyGroupEnd(config, out, groupProxy1);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(1, lines.length);
		assertEquals("", lines[0]);
		
		//Print file closing (should be empty line)
		out.reset();
		printer.printSampleEnd(config, out);
		
		//System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(1, lines.length);
		assertEquals("", lines[0]);
	}

	/**
	 * Test of getFormat method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetFormat() {
	}

	/**
	 * Test of getSampleFileStart method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetSampleFileStart() {
	}

	/**
	 * Test of getSampleStartComment method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetSampleStartComment() {
	}

	/**
	 * Test of getInAliaseString method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetInAliaseString() {
	}

	/**
	 * Test of getActualProperty method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetActualProperty() throws Exception {
	}

	/**
	 * Test of getSampleFileEnd method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetSampleFileEnd() {
	}

	/**
	 * Test of getSampleFileExtension method, of class PropFileLoaderSamplePrinter.
	 */
	@Test
	public void testGetSampleFileExtension() {
	}
	
}
