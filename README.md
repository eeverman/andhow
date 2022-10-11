### AndHow!  Configurable constants for Java application configuration
![Git CI Status](https://github.com/eeverman/andhow/actions/workflows/maven-ci.yaml/badge.svg?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/e306661b53f2463fab9156bf38af58f7)](https://www.codacy.com/gh/eeverman/andhow/dashboard?utm_source=github.com&utm_medium=referral&utm_content=eeverman/andhow&utm_campaign=Badge_Coverage)
[![Javadocs](https://www.javadoc.io/badge/org.yarnandtail/andhow.svg)](https://www.javadoc.io/doc/org.yarnandtail/andhow)
<br/>
**[AndHow Home](https://www.andhowconfig.org)** •
**[User Guide](https://www.andhowconfig.org/user-guide)** •
**[User forum](https://groups.google.com/d/forum/andhowuser)** •
**[Live-code Demo](https://www.andhowconfig.org/live-code-quickstart)**

![AndHow animation](https://github.com/eeverman/andhow-assets/blob/main/andhow_0.5_1280x320_highres_24fps.gif?raw=true)
---
#### New Release:  1.5.0, October 10, 2022 - [notes](https://github.com/eeverman/andhow/releases/tag/andhow-1.5.0).
<img src="https://github.com/eeverman/andhow/raw/master/logo/AndHow-empty-circle-combination.png" width="55" height="72" alt="AndHow's logo"  align="left">
<p>
This release jumps from 0.4.2 to 1.5.0, reflecting that AndHow has been in production long enough to be considered production ready, and includes some API changes.  This release removes deprecated methods, clarifies / subtly changes some behavior, and has general improvements and bug fixes.  See the full [release notes](https://github.com/eeverman/andhow/releases/tag/andhow-1.5.0).

---
                    
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
    <version>1.5.0</version>
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

_**&?!**_&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**[AndHow Home](https://www.andhowconfig.org)** •
**[User Guide](https://www.andhowconfig.org/user-guide)** •
**[User forum](https://groups.google.com/d/forum/andhowuser)** •
**[Live-code Demo](https://www.andhowconfig.org/live-code-quickstart)**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_**&?!**_
