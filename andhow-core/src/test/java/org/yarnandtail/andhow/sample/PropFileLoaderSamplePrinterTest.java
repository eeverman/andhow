/*
 */
package org.yarnandtail.andhow.sample;

import java.io.UnsupportedEncodingException;
import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.api.GroupProxyMutable;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.andhow.internal.StaticPropertyConfigurationMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropFileLoaderSamplePrinterTest {
	
	StaticPropertyConfigurationMutable config;
	GroupProxyMutable groupProxy1;
	
	public static interface Config {
		StrProp MY_PROP1 = StrProp.builder().build();
		StrProp MY_PROP2 = StrProp.builder().defaultValue("La la la").desc("mp description").build();
	}
	
	@Before
	public void setup() {
		config = new StaticPropertyConfigurationMutable(new CaseInsensitiveNaming());
		
		groupProxy1 = new GroupProxyMutable(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName(),
				PropFileLoaderSamplePrinterTest.class.getCanonicalName() + "$Config"
		);
		groupProxy1.addProperty(new NameAndProperty("MY_PROP1", Config.MY_PROP1));
		groupProxy1.addProperty(new NameAndProperty("MY_PROP2", Config.MY_PROP2));
		
		assertNull("Error adding property", config.addProperty(groupProxy1, Config.MY_PROP1));
		assertNull("Error adding property", config.addProperty(groupProxy1, Config.MY_PROP2));

	}
	

	@Test
	public void generalTest() throws UnsupportedEncodingException {
		
		TestPrintStream out = new TestPrintStream();
		PropFileLoaderSamplePrinter printer = new PropFileLoaderSamplePrinter();
		
		//Print MY_PROP1
		printer.printProperty(config, out, groupProxy1, Config.MY_PROP1);
		
		System.out.println(out.getTextAsString());
		String[] lines = out.getTextAsLines();
		assertEquals(3, lines.length);
		assertEquals("# ", lines[0]);
		assertEquals("# MY_PROP1 (String)", lines[1]);
		assertEquals(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName() +
						".MY_PROP1 = [String]",
				lines[2]);
		
		//Print MY_PROP2
		out = new TestPrintStream();
		printer.printProperty(config, out, groupProxy1, Config.MY_PROP2);
		
		System.out.println(out.getTextAsString());
		lines = out.getTextAsLines();
		assertEquals(4, lines.length);
		assertEquals("# ", lines[0]);
		assertEquals("# MY_PROP2 (String)  - mp description", lines[1]);
		assertEquals("# Default Value: La la la", lines[2]);
		assertEquals(
				PropFileLoaderSamplePrinterTest.Config.class.getCanonicalName() +
						".MY_PROP2 = La la la",
				lines[3]);
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
