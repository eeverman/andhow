package org.simple;

import yarnandtail.andhow.property.*;
import yarnandtail.andhow.*;
import yarnandtail.andhow.load.PropFileLoader;

/**
 * This example has almost no comments b/c it's a minimal example for the AndHow! homepage.
 */
public class SimpleSample {
	
	public static void main(String[] args) {
		AndHow.builder() /* Simple builder initializes framework */
				.addLoader(new PropFileLoader())
				.addGroup(MySetOfProps.class)
				.build();
	
		//After initialization, Properties can be used to directly access their values.
		//Note the strongly typed return values.
		String queryUrl =
				MySetOfProps.SERVICE_URL.getValue() +
				MySetOfProps.QUERY_ENDPOINT.getValue();
		Integer timeout = MySetOfProps.TIMEOUT.getValue();
		
		System.out.println("The query url is: " + queryUrl);
		System.out.println("Timeout is : " + timeout);
	}
	
	//Normally PropertyGroups would be in separate files with the module they apply to
	@GroupInfo(name="Example Property group", desc="One logical set of properties")
	public interface MySetOfProps extends PropertyGroup {
		
		StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").build();
		IntProp TIMEOUT = IntProp.builder().setDefault(50).build();
		StrProp QUERY_ENDPOINT = StrProp.builder().required()
				.setDescription("Service name added to end of url for the queries").build();
	}
}
