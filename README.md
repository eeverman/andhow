# AndHow Logo Submissions

The AndHow project is looking for a logo!  You can submit a logo as a merge request with the instructions at the bottom of this page.

## Logo Requirements
Logo submissions should:
* Be submitted as `.png` files for easy browser display, as well as the source file(s) used to create them\*
* Have a small version that is a [monogram or brandmark logo](https://logojoy.com/blog/different-types-of-logos/).  The small version should be square or look appropriate in a square format.
* Have a larger version that is a [combination or emblem logo](https://logojoy.com/blog/different-types-of-logos/), incorporating the project name, **AndHow** or **AndHow!**.
* The smaller version and the larger version should be clearly identifiable as related.
* Be your own work\*\*
* Please submit Pull Requests to the [logo_submission branch of AndHow](https://github.com/eeverman/andhow/tree/logo_submission).  Each submission should be in a separate folder to keep submissions separate.

## What Can I win??
All the fame, notoriety and bragging rights a small, mostly unheard of Java utility library can bestow.  The winning logo will be credited on the home page of the project while the logo is used.  The winning logo will be chosen by the AndHow project owner and that decision will be final.

## AndHow central ideas, themes and memes that could be represented in the logo
AndHow is a software library, written in Java, used to configure software applications.  If you think of a software application like a smart phone, AndHow is like the settings page - a set of control knobs, buttons and switches to make it behave the way you want.  In reality, most software applications have no user interface - they might be scientific applications that crunch numbers or faceless services running somewhere in the cloud.  With no user interface, there is no configuration page with knobs and buttons.  Instead, settings are typically stored in a file or loaded from some type of service.  AndHow can read the application's configuration from lots of types of files and talk to lots of types of services to find the configuration.  Once it finds all the configuration, it validates all the configured values to ensure they are acceptable values.  Compared to most configuration libraries, AndHow is very easy to use.

The name **AndHow** / **AndHow!** was chosen because it has a double meaning:
* "And how?", as in, "And how are you going to do that?"  Which is loosely the idea of configuration.
* "And how!" as in, [emphatically so, or strong agreement](https://idioms.thefreedictionary.com/and+how!)

I also like that AndHow!, _the exclamation_, could be represented by punctuation only: <big>**&?!**</big>. Documentation and other project text currently uses **AndHow**, **AndHow!** and **&?!** in several places.

AndHow also uses the tag line: **_AndHow! strong.valid.simple.AppConfiguration_**  That tagline comes from the first three features: Strong Typing, Detailed validation, and Simple to Use (those are explained below).  The 'dot' notation is a package naming convention in Java.  

The [main project page](https://sites.google.com/view/andhow/home) lists these key features:
* **Strong Typing** - Numbers are configured as numbers, Strings as strings, dates as dates, etc..  Many other configuration utilities only use Strings.  As an example, it doesn't make sense to 'configure' the temperatures dial on a stove to 'Rabbit' - it must be a number.
* **Detailed validation** - Beyond _strong typing_, _validation_ means rules can be made for acceptable values.  A stove's temperature dial must be a number (typing), but should be greater than zero as well (a validation rule).
* **Simple to use** - Speaks for itself.
* **Use Java public & private to control configuration visibility** - Most configuration utilities allow any part of the application to access any configured value.  If a password is configured for one part of application to use, the entire application has access to that password.  Not so with AndHow.
* **Validates all property values at startup to Fail Fast** - Validation is great, but it has to happen _early_, right when the application starts up.  The alternative (validating values at the time of use), means that the application might start running just fine, but stop working when some part of the application needs to 'turn the stove on', only to discover the temperature to 'Rabbit'.  AndHow finds those validation rule violations when when the application is first started up.
* **Loads values from multiple sources (JNDI, env vars, prop files, etc)** - AndHow can read the application's configuration from lots of types of files and talk to lots of types of services to find the configuration.
* **Generate configuration sample files based on application properties** - A typical larger application has lots of configuration options and each option has a name and description.  Documenting all of that and putting it into a file to make that first configuration file could be really time consuming.  AndHow will do it for you so you have a initial configuration file to get started from.

Thats the gist of the AndHow project.  Feel free to submit multiple pull requests with different ideas!

\* It would be nice to have vector version for easy resizing

\*\* Know that submitting a merge request with a logo implies that you accept that the logo will become part of this project, which uses the [Apache 2.0 license](https://github.com/eeverman/andhow/blob/master/LICENSE).

## AndHow central ideas, themes and memes that could be represented in the logo


