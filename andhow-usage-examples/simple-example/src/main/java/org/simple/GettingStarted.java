package org.simple;

import org.yarnandtail.andhow.property.*;

public class GettingStarted {
	
	//1
	final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull()
			.desc("Start the countdown from this number")
			.mustBeGreaterThanOrEqualTo(1).defaultValue(3).build();
	
	private final static StrProp LAUNCH_CMD = StrProp.builder().mustBeNonNull()
			.desc("What to say when its time to launch")
			.mustMatchRegex(".*Go.*").defaultValue("GoGoGo!").build();
	
	public String launch() {
		String launch = "";
		
		//2
		for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
			launch = launch += i + "...";
		}
		
		return launch + LAUNCH_CMD.getValue();
	}
	
	public static void main(String[] args) {
		GettingStarted gs = new GettingStarted();
		System.out.println(gs.launch());
	}
}
