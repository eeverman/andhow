/*
 * NOT4:  THIS CLASS LARGLY COPIED FROM THE LOG4J UTIL:
 * https://git-wip-us.apache.org/repos/asf?p=logging-log4j2.git;a=blob;f=log4j-api/src/main/java/org/apache/logging/log4j/util/StackLocator.java
 * 
 * BELOW IS THE ORIGINAL LICENSE W/ THAT THAT FILE:
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.yarnandtail.andhow.util;


/**
 * A utility class for find the calling class, method and line number for use
 * in creating more detailed logging information.  This class was mostly copied
 * from the class of the same name in the Apache log4j project.
 */
public class StackLocator {

    public static StackTraceElement calcLocation(final String fqcnOfLogger) {
        if (fqcnOfLogger == null) {
            return null;
        }
        // LOG4J2-1029 new Throwable().getStackTrace is faster than Thread.currentThread().getStackTrace().
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement last = null;
        for (int i = stackTrace.length - 1; i > 0; i--) {
            final String className = stackTrace[i].getClassName();
            if (fqcnOfLogger.equals(className)) {
                return last;
            }
            last = stackTrace[i];
        }
        return null;
    }

}
