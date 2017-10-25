package org.simple;

/**
 * This example has almost no comments b/c it's a minimal example for the AndHow! homepage.
 */
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.*;

@GroupInfo(name="Basic example of properties embedded directly in a class",
		desc="This description provides documentation for the properties contained w/in this class.")
public class SimpleSample {
	//AndHow property definitions tend to be self documenting, but descriptions can be added
	public final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull().
			mustBeGreaterThan(0).defaultValue(10).desc("Start value for countdown").build();
	
	public final static IntProp COUNT_DOWN_END = IntProp.builder().mustBeNonNull().
			mustBeLessThanOrEqualTo(0).defaultValue(0).desc("End value for countdown").build();
	
	private final static StrProp LAUNCH_COMMAND = StrProp.builder().mustBeNonNull().
			mustMatchRegex(".*Launch.*").aliasIn("cmd").desc("The actual launch command").build();
	
	
	public static void main(String[] args) {
		AndHow.builder().addCmdLineArgs(args).build();
	
		System.out.println("Initiate launch sequence...");
		for (int i = COUNT_DOWN_START.getValue(); i >= COUNT_DOWN_END.getValue(); i--) {
			System.out.println("..." + i);
		}
		
		System.out.println(LAUNCH_COMMAND.getValue());
	}
	
	//Quick hack to access the private LAUNCH_COMMAND 
	public String tellMeTheLaunchCommand() {
		return LAUNCH_COMMAND.getValue();
	}
	
}
