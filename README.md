# The [AndHow project](https://github.com/eeverman/andhow) is looking for a logo and you could be the designer!

## Is this a contest?  What can I win??
This is not intended to be a contest, instead it is a chance for people to flex their creative muscles during [Hacktoberfest](https://hacktoberfest.digitalocean.com/) with something other than code.

The AndHow project _will_ be choosing a logo to use from the submissions, but there is no prize other than all the fame, notoriety and bragging rights a small, mostly unheard of Java utility library can bestow.  The selected logo will be credited on the home page of the project while the logo is used.  The logo will be chosen by the AndHow project owner and collaborators and that decision will be final.
## Logo Requirements
The Logo should:
* Be submitted as `.png` files for easy browser display, as well as the source file(s) used to create them.
* Have a small version that is an [abstract mark, monogram or pictorial mark](https://99designs.com/blog/tips/types-of-logos/).  The small version should be recognizable in a small format and be square or work well in a square format.
* Have a larger version that is a [combination or emblem logo](https://99designs.com/blog/tips/types-of-logos/), incorporating the project name, **AndHow** or **AndHow!** with the smaller logo.  (examples of all of these types are at the bottom)
* Be your own work

## Discussion
Questions and comments can be posted on [this task](https://github.com/eeverman/andhow/issues/427) or on twitter as [\#andhowconfig_logo](https://twitter.com/hashtag/andhowconfig_logo).

## AndHow central ideas, themes and memes that could be represented in the logo
![Andhow Visual](https://github.com/eeverman/andhow/blob/master/andhow.gif)
**_Note:  This section is long and wordy.  Don't feel you have to read and understand all of it - just look for the part that resonates and might be the basis of the logo._**

AndHow is a software library, written in Java, used to configure software applications.  If you think of a software application like a smart phone, AndHow is like the settings page - a set of control knobs, buttons and switches to make it behave the way you want.  In reality, most software applications have no user interface - they might be scientific applications that crunch numbers or faceless services running somewhere in the cloud.  With no user interface, there is no configuration page with knobs and buttons.  Instead, settings are typically stored in a file or loaded from some type of service.  AndHow can read the application's configuration from lots of types of files and talk to lots of types of services to find the configuration.  Once it finds all the configuration, it validates all the configured values to ensure they are acceptable values.  Compared to most configuration libraries, AndHow is very easy to use.

The name **AndHow** / **AndHow!** was chosen because it has a double meaning:
* "And how?", as in, "And how are you going to do that?"  Which is loosely the idea of configuration.
* "And how!" as in, [emphatically so, or strong agreement](https://idioms.thefreedictionary.com/and+how!)

I also like that _AndHow!_, the exclamation, could be represented by punctuation only: <big>**&?!**</big>. Documentation and other project text currently uses **AndHow**, **AndHow!** and **&?!** in several places.

AndHow also uses the tag line: **_AndHow! strong.valid.simple.AppConfiguration_**  That tagline comes from the first three features: Strong Typing, Detailed validation, Simple to Use (those are explained below) plus 'Application Configuration', which is what AndHow does.  The 'dot' notation is a package naming convention in Java. 

The [main project page](https://sites.google.com/view/andhow/home) lists these key features:
* **Strong Typing** - Numbers are configured as numbers, Text as text, dates as dates, etc..  Many other configuration utilities only use Text.  As an example, the temperatures dial on a stove must be 'configured' to a number, setting it 'Rabbit' doesn't work.
* **Detailed validation** - Beyond strong typing, _validation_ means rules can be made for acceptable values.  A stove's temperature must be a number (typing), but should also be greater than zero (a validation rule).
* **Simple to use** - Speaks for itself.
* **Use Java public & private to control configuration visibility** - Most configuration utilities allow the entire application to access any configured value.  If a password is configured for one part of application to use, the entire application has access to it.  Not so with AndHow.
* **Validates all property values at startup to Fail Fast** - Validation is great, but it has to happen _early_, right when the application starts up.  The alternative (validating values at the time of use), means that the application might start running just fine, but stop working when some part of the application needs to 'turn the stove on', only to discover the temperature is set to 'Rabbit'.  AndHow finds those validation rule violations when when the application is first started up.
* **Loads values from multiple sources (JNDI, env vars, prop files, etc)** - AndHow can read the application's configuration from lots of types of files and/or services.  Its flexible that way.
* **Generate configuration sample files based on application properties** - A typical application has lots of configuration options and each option has a name, description validation rules, etc. - Documenting all of that takes time.  AndHow will do it for you and give you an initial configuration file to start from.

Thats the gist of the AndHow project.  Feel free to submit multiple pull requests with different ideas!
## How to make a submission
### Step 1:  Fork the [AndHow](https://github.com/eeverman/andhow) project in GitHub ([instructions](https://guides.github.com/activities/forking/))
_(While you are there, give AndHow a star)_
### Step 2:  Clone your fork to your local machine
The logo submissions are on an isolated branch of the project, separate from the code.  To check out _just the logo branch_, use this git command:
```
git clone -b logo_submission --single-branch https://YOUR_FORK_URL_FOR_THE_ANDHOW_PROJECT
```
_YOUR_FORK_URL_FOR_THE_ANDHOW_PROJECT_ will be [displayed on the page after forking](https://services.github.com/on-demand/github-cli/clone-repo-cli).
### Step 3:  Make your own branch
`git checkout -b my_new_logo_submission`
### Step 4:  Make your own folder for your logo files (e.g. myname_logo)
### Step 5:  Create an awesome logo!
### Step 6:  Submit your logo [as a pull request](https://guides.github.com/activities/forking/) against the logo_submission branch.

Please remember that submitting a pull request to this project means that your logo will become part of the AndHow project and will come under the [Apache 2.0 license](https://github.com/eeverman/andhow/blob/master/LICENSE).

**I'm really excited to see everyone's creative energy and amazing designs!  Happy hack-design-tober!**

## Some example logos of various types
### Abstract Marks
![Abstract Mark Logos](https://cdn2.f-cdn.com/files/download/36646775/6ba52d.jpg)
### Monograms
![Monogram Logos](https://www.moirae.co.uk/media/4320/lettermarks.jpg)
### Pictorial Logos
![Pictorial Logos](https://www.brandsnack.co/wp-content/uploads/2018/04/pictorial-brandmark-logo-examples.jpg)
### Combination Logos
![Combination Logos](https://www.odanieldesignsblog.com/wp-content/uploads/2017/07/CombinationLogo.png)
### Emblem Logos
![Emblem Logos](https://www.digitalflare.co.uk/media/1477958400/1479686400/1479763310-989a73f58a50f17406f1af3480cf3e09.jpg)