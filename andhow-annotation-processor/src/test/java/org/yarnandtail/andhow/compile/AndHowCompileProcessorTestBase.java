package org.yarnandtail.andhow.compile;

import java.util.HashSet;
import java.util.Set;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.BeforeEach;
import org.yarnandtail.andhow.util.IOUtil;
import org.yarnandtail.compile.MemoryFileManager;
import org.yarnandtail.compile.TestClassLoader;
import org.yarnandtail.compile.TestSource;

/**
 *
 * @author ericevermanpersonal
 */
public class AndHowCompileProcessorTestBase {
	
	/** Classpath of the generated service file for AndHow property registration */
	static final String REGISTRAR_SVS_PATH = 
			"/META-INF/services/org.yarnandtail.andhow.service.PropertyRegistrar";
	static final String INIT_SVS_PATH = "/META-INF/services/org.yarnandtail.andhow.AndHowInit";
   	static final String TEST_INIT_SVS_PATH = "/META-INF/services/org.yarnandtail.andhow.AndHowTestInit";
     
	JavaCompiler compiler;
	MemoryFileManager manager;
	DiagnosticCollector<JavaFileObject> diagnostics;
	TestClassLoader loader;
	
	Set<TestSource> sources;	//New set of source files to compile
	
	@BeforeEach
	public void setupTest() {
		compiler = ToolProvider.getSystemJavaCompiler();
		manager = new MemoryFileManager(compiler);
		diagnostics = new DiagnosticCollector();
		loader = new TestClassLoader(manager);
		sources = new HashSet();
	}
	
	/**
	 * The source path to where to find this file on the classpath
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String srcPath(String classPackage, String simpleClassName) {
		return "/" + classPackage.replace(".", "/") + "/" + simpleClassName + ".java";
	}
	
	/**
	 * Full canonical name of the class.
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String fullName(String classPackage, String simpleClassName) {
		return classPackage + "." + simpleClassName;
	}
	
	/**
	 * Builds a new TestSource object for a Java source file on the classpath
	 * @param classPackage
	 * @param simpleClassName
	 * @return
	 * @throws Exception 
	 */
	public TestSource buildTestSource(String classPackage, String simpleClassName) throws Exception {
		String classContent = IOUtil.getUTF8ResourceAsString(srcPath(classPackage, simpleClassName));
        return new TestSource(fullName(classPackage, simpleClassName), JavaFileObject.Kind.SOURCE, classContent);
	}
	
	/**
	 * Build the canonical name of the generated class from the source class.
	 * 
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String genName(String classPackage, String simpleClassName) {
		return classPackage + ".$" + simpleClassName + "_AndHowProps";
	}
	
}
