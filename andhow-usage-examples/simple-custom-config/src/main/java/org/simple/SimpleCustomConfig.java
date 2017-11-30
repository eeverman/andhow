package org.simple;

import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;

@GroupInfo(name="Simple AndHow usage example", desc="@GroupInfo documents the contained Properties")
public class SimpleCustomConfig {

	final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull().
			mustBeGreaterThanOrEqualTo(1).defaultValue(2).build();
	
	private final static StrProp LAUNCH_COMMAND = StrProp.builder().mustBeNonNull().
			mustMatchRegex(".*Go.*").build();
	
	public String launch() {
		String launch = "";
		
		//5 - Note the strong typing of COUNT_DOWN_START as an Integer
		for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
			launch = launch += i + "...";
		}
		
		return launch + LAUNCH_COMMAND.getValue();
	}
	
}
