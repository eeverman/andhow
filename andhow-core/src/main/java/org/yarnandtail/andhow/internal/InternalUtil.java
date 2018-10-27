/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yarnandtail.andhow.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Loader;

/**
 *
 * @author ericevermanpersonal
 */
public class InternalUtil {

	/**
	 * Prints details on a failed startup to a PrintStream.
	 * 
	 * @param staticConfig The current configuration for AndHow
	 * @param loaders Loaders in use by AndHow - each may print a config sample
	 * @param afe The fatal error that caused start to fail
	 * @param out The PrintStream to write to
	 */
	public static void printFailedStartupDetails(StaticPropertyConfigurationInternal staticConfig,
			List<Loader> loaders, AppFatalException afe, PrintStream out) {
		
		File sampleDir = ReportGenerator.printConfigSamples(staticConfig, loaders, true);
		String sampleDirStr = (sampleDir != null)?sampleDir.getAbsolutePath():"";
		afe.setSampleDirectory(sampleDirStr);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ReportGenerator.printProblems(ps, afe, staticConfig);
		
		try {
			String message = os.toString("UTF8");
			//Add separator prefix to prevent log prefixes from indenting 1st line
			out.println(System.lineSeparator() + message);
		} catch (UnsupportedEncodingException ex) {
			ReportGenerator.printProblems(System.err, afe, staticConfig);	//shouldn't happen	
		}
		
	}
}
