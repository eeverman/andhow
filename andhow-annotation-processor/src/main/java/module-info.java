module andhow.processor {
    requires java.base;
    requires java.logging;
    requires java.compiler;
    requires jdk.compiler;
    requires andhow.core;

    exports org.yarnandtail.andhow.compile;
}