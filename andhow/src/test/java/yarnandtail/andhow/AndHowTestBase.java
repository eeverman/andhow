package yarnandtail.andhow;

import javax.naming.NamingException;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * All tests using AppConfig must extend this class so they have access to the
 * one and only AppConfig.Reloader, which is a single backdoor to cause the
 * AppConfig to reload.
 * 
 * @author eeverman
 */
public class AndHowTestBase {
	
	public static AndHow.Reloader reloader = AndHow.builder().buildForNonPropduction();

	/**
	 * Simple consistent way to get an empty JNDI context.
	 * 
	 * bind() each variable, then call build().
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public static SimpleNamingContextBuilder getJndi() throws NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		return builder;
	}
	
}
