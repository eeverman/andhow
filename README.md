[![Build Status](https://travis-ci.com/eeverman/andhow.svg?branch=master)](https://travis-ci.com/github/eeverman/andhow)
[![codecov](https://codecov.io/gh/eeverman/andhow/branch/main/graph/badge.svg)](https://codecov.io/gh/eeverman/andhow)
[![Javadocs](https://www.javadoc.io/badge/org.yarnandtail/andhow.svg)](https://www.javadoc.io/doc/org.yarnandtail/andhow)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; •
<kbd><b>[AndHow Home](https://www.andhowconfig.org)</b></kbd> •
<kbd><b>[User Guide](https://www.andhowconfig.org/user-guide)</b></kbd> •
<kbd><b>[User forum](https://groups.google.com/d/forum/andhowuser)</b></kbd> •
<kbd><b>[Live-code Quickstart](https://www.andhowconfig.org/live-code-quickstart)</b></kbd>
---
### AndHow!  Configurable constants for Java application configuration

#### New Release:  0.4.2, October 24, 2021 - [notes](https://github.com/eeverman/andhow/releases/tag/andhow-0.4.2).
<img src="https://github.com/eeverman/andhow/raw/homepage/logo/AndHow-empty-circle-combination.png" width="55" height="72" alt="AndHow's new logo" align="left"></img>

This release includes several bug fixes and adds a new
[export feature](https://www.andhowconfig.org/user-guide/integration-and-exports#manual-export-to-maps-java.util.properties-and-more)
to better support configuring other frameworks that accept configuration as a `Map` or `java.util.Properties`.
Special thanks to [Alex Karpov](https://github.com/alex-kar) for many high-quality code fixes and
improvements in this release.
---
![Andhow Visual](andhow.gif)

### What if you could configure constants?  What if Java application configuration _was_ just constants?
AndHow configures your application with strongly typed Properties that work just like `static final`
constants in your code.  Values for Properties are loaded from multiple sources and are validated
at startup.

### Key Features
* **Strong Typing**
* **Detailed validation**
* **Simple to use and test**
* **Use Java `public` & `private` modifiers to control Property value access**
* **Validates _all_ property values at startup to _[Fail Fast](https://www.martinfowler.com/ieeeSoftware/failFast.pdf)_**
* **Loads values from multiple sources (env. vars, system props, cmd line, prop files, JNDI, and more)**
* **Generates configuration template files for the Properties in your application**

### Use it via Maven (available on Maven Central)
```xml
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.4.2</version>
</dependency>
```

### Declaring and Using AndHow Properties
Declaring Properties looks like this:
```java
private static final StrProp HOST = StrProp.builder().startsWith("internal.").build();
public static final IntProp PORT = IntProp.builder().defaultValue(80).build();
```
Using Properties looks like this:
```java
String theHost = HOST.getValue();
int thePort = PORT.getValue();
```
`StrProp` &amp; `IntProp` are AndHow `Property`s. 
Properties and their values are constants, so they are always declared as `static final`,
but may be `private` or any scope.  Properties may have default values, validation rules,
description, and more.

Properties are used just like static final constants with `.getValue()` tacked on.
They are _**strongly typed**_, so `HOST.getValue()` returns a `String`, `PORT.getValue()` returns an `Integer`.

At startup, AndHow scans `System.Properties`, environment variables, JNDI values,
the `andhow.properties` file, etc., in a
[well established order](https://www.andhowconfig.org/user-guide/loaders-and-load-order).
If the loaded value for any Property in the application does not meet the validation requirements,
AndHow throws a detailed `RuntimeException` to [fail fast](https://www.andhowconfig.org/user-guide/key-concepts#andhow-fails-fast)
and prevent the application from running with invalid configuration.

---
• <kbd><b>[AndHow Home](https://www.andhowconfig.org)</b></kbd> •
<kbd><b>[User Guide](https://www.andhowconfig.org/user-guide)</b></kbd> •
<kbd><b>[User forum](https://groups.google.com/d/forum/andhowuser)</b></kbd> •
<kbd><b>[Live-code Quickstart](https://www.andhowconfig.org/live-code-quickstart)</b></kbd> •
---
_**&?!**_

