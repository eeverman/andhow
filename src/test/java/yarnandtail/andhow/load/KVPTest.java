package yarnandtail.andhow.load;

import yarnandtail.andhow.load.KVP;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.ParsingException;
import yarnandtail.andhow.load.CmdLineLoader;

/**
 *
 * @author eeverman
 */
public class KVPTest {
	
	@Test
	public void splitKVPGoodEmptyArgs() throws Exception {
		assertEquals(KVP.NULL_KVP, KVP.splitKVP("", CmdLineLoader.KVP_DELIMITER));
		assertEquals(KVP.NULL_KVP, KVP.splitKVP("    ", CmdLineLoader.KVP_DELIMITER));
		assertEquals(KVP.NULL_KVP, KVP.splitKVP("   \t ", CmdLineLoader.KVP_DELIMITER));
		assertEquals(KVP.NULL_KVP, KVP.splitKVP("   \t\n\r\f ", CmdLineLoader.KVP_DELIMITER));
		assertEquals(KVP.NULL_KVP, KVP.splitKVP("   \b ", CmdLineLoader.KVP_DELIMITER));
	}
	
	@Test
	public void splitKVPGoodFlagNames() throws Exception {
		
		KVP kvp = null;
		
		kvp = KVP.splitKVP("flag", CmdLineLoader.KVP_DELIMITER);
		assertEquals("flag", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("1", CmdLineLoader.KVP_DELIMITER);
		assertEquals("1", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("1a1", CmdLineLoader.KVP_DELIMITER);
		assertEquals("1a1", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("  1a1   ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("1a1", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("\"1\"", CmdLineLoader.KVP_DELIMITER);
		assertEquals("\"1\"", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("\'1\'", CmdLineLoader.KVP_DELIMITER);
		assertEquals("\'1\'", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP(" \t!@#$%^&*()_+-{var}[OPTION]\\~`<br>,.?/|  \t\n\r\f ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("!@#$%^&*()_+-{var}[OPTION]\\~`<br>,.?/|", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("  1\t1   ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("1\t1", kvp.getName());
		assertNull(kvp.getValue());
	}
	
	@Test
	public void splitKVPGoodNameAndValues() throws Exception {
		
		KVP kvp = null;
		
		kvp = KVP.splitKVP("flag=value", CmdLineLoader.KVP_DELIMITER);
		assertEquals("flag", kvp.getName());
		assertEquals("value", kvp.getValue());
		
		kvp = KVP.splitKVP("  \t flag \t  =  \t value \t  ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("flag", kvp.getName());
		assertEquals("value", kvp.getValue());
		
		kvp = KVP.splitKVP("   flag   =    ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("flag", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = KVP.splitKVP("  fl \t ag \r = \n val \t ue  ", CmdLineLoader.KVP_DELIMITER);
		assertEquals("fl \t ag", kvp.getName());
		assertEquals("val \t ue", kvp.getValue());
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadEmptyFlagName() throws Exception {
		KVP.splitKVP("=value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadSpaceOnlyFlagName() throws Exception {
		KVP.splitKVP("  =value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadAllSpaceAndTabFlagName() throws Exception {
		KVP.splitKVP("   \t =value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadAllWhitespaceAndNewLinesFlagName() throws Exception {
		KVP.splitKVP("   \t\n\r\f = value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadAllBackspaceFlagName() throws Exception {
		KVP.splitKVP("\b =value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test(expected=ParsingException.class)
	public void splitKVPBadAllWhitespaceAndBackspaceFlagName() throws Exception {
		KVP.splitKVP("   \b  =value", CmdLineLoader.KVP_DELIMITER);
	}
	
	@Test
	public void newKVPByNameOnlyGoodName() throws Exception {
		KVP kvp = null;
		
		kvp = new KVP("flag");
		assertEquals("flag", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = new KVP("  \t fl\t \tag \t\r\n ");
		assertEquals("fl\t \tag", kvp.getName());
		assertNull(kvp.getValue());
		
		kvp = new KVP("   .    ");
		assertEquals(".", kvp.getName());
		assertNull(kvp.getValue());
	}
	
	@Test(expected=ParsingException.class)
	public void newKVPByNameOnlyBadEmptyName() throws Exception {
		new KVP("  \t \r\n ");
	}
	
	@Test
	public void newKVPByNameAndValueGood() throws Exception {
		KVP kvp = null;
		
		kvp = new KVP(" \n\r fl\tag \t ", " \t va\tlue \t");
		assertEquals("fl\tag", kvp.getName());
		assertEquals("va\tlue", kvp.getValue());
		
		kvp = new KVP(" \n\r fl\tag \t ", "   \t  \r \n ");
		assertEquals("fl\tag", kvp.getName());
		assertNull(kvp.getValue());
	}
	
	@Test(expected=ParsingException.class)
	public void newKVPByNameAndValueBadEmptyName() throws Exception {
		new KVP("  \t \r\n ", "value");
	}
}
