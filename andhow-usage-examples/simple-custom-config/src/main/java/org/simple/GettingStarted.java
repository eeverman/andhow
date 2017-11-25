package org.simple;

/**
 * This example has almost no comments b/c it's a minimal example for the AndHow! homepage.
 */
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;

//1
@GroupInfo(name="Simple AndHow usage example", desc="@GroupInfo documents the contained Properties")
public class GettingStarted {
	//2
	final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull().
			mustBeGreaterThanOrEqualTo(1).defaultValue(2).build();
	
	//3
	private final static StrProp LAUNCH_COMMAND = StrProp.builder().mustBeNonNull().
			mustMatchRegex(".*Go.*").build();
	
	//4
	public String launch() {
		String launch = "";
		
		//5 - Note the strong typing of COUNT_DOWN_START as an Integer
		for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
			launch = launch += i + "...";
		}
		
		return launch + LAUNCH_COMMAND.getValue();
	}
	
}
