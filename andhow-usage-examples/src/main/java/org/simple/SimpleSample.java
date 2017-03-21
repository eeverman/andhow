package org.simple;

/**
 * This example has almost no comments b/c it's a minimal example for the AndHow! homepage.
 */

import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.PropertyGroup;

public class SimpleSample {
	
	public static void main(String[] args) {
		AndHow.builder() /* 1) Simple builder initializes framework */
				.cmdLineArgs(args)
				.loader(new SystemPropertyLoader())
				.loader(new JndiLoader())
				.loader(new PropertyFileOnClasspathLoader(MySetOfProps.CLASSPATH_PROP))
				.group(MySetOfProps.class) /* 2) MySetOfProps defined below */
				.build();
	
		//3) After initialization, Properties can be used to directly access their values.
		//Note the strongly typed return values.
		String queryUrl =
				MySetOfProps.SERVICE_URL.getValue() +
				MySetOfProps.QUERY_ENDPOINT.getValue();
		Integer timeout = MySetOfProps.TIMEOUT.getValue();
		
		System.out.println("The query url is: " + queryUrl);
		System.out.println("Timeout is : " + timeout);
	}
	
	//4) Normally PropertyGroups would be in separate files with the module they apply to
	@GroupInfo(name="Example Property group", desc="One logical set of properties")
	public interface MySetOfProps extends PropertyGroup {
		
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasIn("url").build(); // 5)
		IntProp TIMEOUT = IntProp.builder().defaultValue(50).build();
		StrProp QUERY_ENDPOINT = StrProp.builder().required()
				.desc("Service name added to end of url for the queries").build();
		// 6)		
		StrProp CLASSPATH_PROP = StrProp.builder().desc("Classpath location of properties file").build();
	}
}
