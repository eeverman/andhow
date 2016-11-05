package yarnandtail.andhow.cmdline;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author eeverman
 */
public class CmdLineLoaderTest {
	
	public String[] goodArgs1;
	public String[] goodArgs2;
	
	@Before
	public void beforeTest() {
		goodArgs1 = new String[] {
			"flag1", "param1=val1", "k=v", "1=2","flag2"
		};
		goodArgs1 = new String[] {
			"param1=val1", "flag1", "k=v","flag2", "1=2"
		};
	}

}
