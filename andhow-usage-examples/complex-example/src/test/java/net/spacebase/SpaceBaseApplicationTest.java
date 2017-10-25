/*
 */
package net.spacebase;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class SpaceBaseApplicationTest {
	
	public SpaceBaseApplicationTest() {
	}
	
	@Before
	public void setUp() {
	}

	/**
	 * Test of main method, of class SpaceBaseApplication.
	 */
	@Test
	public void testMain() {
		
		SpaceBaseApplication.main(new String[] {"app_name=Spacey Basey"});
		SpaceBaseApplication sba = SpaceBaseApplication.instance();
		
		//Try out the SpaceBaseApplication
		assertEquals("Spacey Basey", sba.getAppName());	//This value picked as an argument to main(String[])
		assertEquals("http://spacebase.net", sba.getAppUrl());	//This is the default value
		assertEquals(LocalDateTime.parse("1999-10-01T00:00"), sba.getInceptionDate());	//picked up from andhow.properties file
		
		
		//Try out the ReallyOldSatelliteService, WHICH ONLY READS SYSTEM PROPERTIES
		ReallyOldSatelliteService ross = new ReallyOldSatelliteService();
		assertEquals("http://satservice.com/item/", ross.getItemUrl());		//from andhow.properties file
		assertEquals("http://satservice.com/query/", ross.getQueryUrl());	//from andhow.properties file
		assertEquals(99, ross.getTimeout());	//from andhow.properties file
		
		//Try out the PlanetService, WHICH ONLY READS SYSTEM PROPERTIES
		PlanetService ps = new PlanetService();
		assertEquals("http://planetserice.org/this_one/", ps.getItemUrl());		//from andhow.properties file
		assertEquals("http://planetserice.org/what/", ps.getQueryUrl());	//from andhow.properties file
		assertEquals(50, ps.getTimeout());	//from andhow.properties file
	}

	
}
