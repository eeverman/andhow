module andhow.core {
    requires java.base;
    requires java.logging;
    requires java.naming;

    exports org.yarnandtail.andhow;
    exports org.yarnandtail.andhow.api;
    exports org.yarnandtail.andhow.name;
    exports org.yarnandtail.andhow.util;
    exports org.yarnandtail.andhow.load;
    exports org.yarnandtail.andhow.valid;
    exports org.yarnandtail.andhow.export;
    exports org.yarnandtail.andhow.sample;
    exports org.yarnandtail.andhow.service;
    exports org.yarnandtail.andhow.load.std;
    exports org.yarnandtail.andhow.internal;
    exports org.yarnandtail.andhow.property;
    exports org.yarnandtail.andhow.valuetype;

}