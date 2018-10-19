# AndHow Logo Submissions

The AndHow project is looking for a logo!  You can submit a logo with the instructions at the bottom of this page.

## Requirements
Logo submissions should:
* Be submitted as `.png` files for easy browser display, as well as the source file(s) used to create them
* Have a small version that is a [monogram or brandmark logo](https://logojoy.com/blog/different-types-of-logos/).  The small version should be square or look appropriate in a square space.
* Have a larger version that is a [combination or emblem logo](https://logojoy.com/blog/different-types-of-logos/), incorporating the project name, **AndHow**.
* The smaller version and the larger version should be clearly identifiable as related.
* Be your own work.
* Know that submitting a merge request with a logo implies that you accept that the logo will become part of this project, which uses the [Apache 2.0 license](https://github.com/eeverman/andhow/blob/master/LICENSE).
* It would be nice to have vector version of the logos easy resizing
## AndHow central ideas, themes and memes that could be represented in the logo
AndHow is a software library, written in Java, used to configure software applications.  If you think of a software application like a smart phone, AndHow is like the settings page - a set of control knobs, buttons and switches to make it behave the way you want.

The name **AndHow** / **AndHow!** was choosen because it has a double meaning:
* "And how", as in, "And how are you going to do that?"  Which is loosely the idea of configuration.
* "And how!" as in, [emphatically so or strong agreement](https://idioms.thefreedictionary.com/and+how!)

I also like that AndHow! - the exclamation - could be represented by punctuation only: <big>**&?!**</big>. Documentation and other project text currently uses **AndHow**, **AndHow!** and **&?!**

The [main project page](https://sites.google.com/view/andhow/home) lists these key features:
* **Strong Typing** - Numbers are configured as numbers, Strings as strings, dates as dates, etc..  Many other types of configuration utilities only use Strings.  For instance, it wouldn't make any sense to set the temperatures dial on a stove to 'Rabbit' - it needs a number.
* **Detailed validation** - _Validation_ means that you can specify rules for what is considered a good configuration value.  A stove's temperature dial must be set to a number, but that number should probably greater than zero as well.
* **Simple to use** - Speaks for itself, but this is in terms of how hard it is for an application developer to use AndHow in their software project.
* **Use Java public & private to control configuration visibility** - In a lot of configuration utilities, any part of the application can access any of the configuration values.  If one part of the application needs a password to login to some secret system, the entire application has access to that password.  Not so with AndHow.
* **Validates all property values at startup to Fail Fast** - Validation is great, but it has to happen _early_, right when the application starts up.  The alternative (validating values at the time of use), means that the application might start running just fine, but stop working when some part of the application needs to 'turn the stove on', only to discover there is no way to set the temperature to '-Rabbit'.  Likely that will happen in the middle of the night and someone will need to come and figure out what the problem is.
* **Loads values from multiple sources (JNDI, env vars, prop files, etc)** - The idea of a settings page on a smart phone is a good picture of application configuration, but in reality, software applications that run on servers rarely have some user friendly page that someone clicks a bunch of options on.  Instead, the various settings are stored in a file somewhere.  AndHow can read lots of different types of files and talk to lots of different types of services to find the configuration.  I has a lot of flexibility in where it can find that configuration.
* **Generate configuration sample files based on application properties** - A typical larger application might have lots of configuration options and each option has some sort of name and a description of what it does.  Documenting all of that and putting it into a file to make that first configuration file could be really time consuming.  AndHow will do it for you, so you have a starting configuration file to get started from.


Thats the gist of the AndHow project.  Feel free to submit multiple pull requests with different ideas!