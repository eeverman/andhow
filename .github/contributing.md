## How to contribute to _AndHow_

First, _**thank you**_ for getting involved - Let's work together and make something cool!

### First Steps
* _**[Star](https://github.com/eeverman/andhow/stargazers)**_ the AndHow project on GitHub.
This raises the profile of AndHow a bit and may help others become contributors.
* If you are unfamiliar with the project, read through the home page at
[www.andhowconfig.org](https://www.andhowconfig.org) (4 minute read).

### Work from the _main_ branch
* The _**main**_ branch is the branch to work from: branch from it, make PRs to it
* The _homepage_ branch is the default branch displayed on GitHub

This can be confusing because most projects use main as the default branch, but there are
[advantages to this structure](https://www.andhowconfig.org/developer-guide/project-branching-structure#github-and-the-default-branch).

### Typical Task / Issue workflow
AndHow uses the typical [feature branch repository strategy](https://martinfowler.com/articles/branching-patterns.html#feature-branching)
(aka fork-and-branch), where developers work on a branch in their own repository.  Basic steps are:
* Fork the AndHow project on GitHub
* _Clone_ from your fork to your local machine to work on it
* Work on a task in a new branch created just for that task - create the branch from your **_main_**
* Submit completed work (or work in progress for review) as a Pull Request to **_main_**
of the canonical repository

Feature branch names should look like this:  `Issue123-A-short-name-for-the-issue`

If that is all new to you, here is a bit of
[help with a first git checkout](https://www.andhowconfig.org/developer-guide/first-checkout-with-git).

### Unit Testing
**As a contributor, please:**
* Write tests for new functionality at or new 100% test coverage
* Write tests for untested code if you are modifying it

...and always feel free to contribute tests for untested or poorly tested code

### Code style
* This project uses _tabs_ for indentation. 
If you are working on a file that is not tab indent, please convert it to tabs (but don't do other files).
* Add complete Javadocs for new methods and classes (other than test classes unless needed)
* Good javadocs comments _what_ and _why_.  We usually don't need comments on how.

### More help getting started
There is a [New Workstation setup](https://www.andhowconfig.org/developer-guide/new-workstation-setup)
page if you need additional help, or post a question on the
[forum](https://groups.google.com/g/andhowuser).

### Working well together
One of the joys of a project like this is collaborating with others.
Collaboration is more than completing issues, it is discussing ideas, asking questions, learning,
discovering something new and cool together.  Some ways to help that happen:
* Post Work In Progress (WIP) pull requests and ask for review - its a good way start discussion.
* Ask questions on issues:  _Is this really the best approach?  Does this feature really need to
be in this release?_
* Take part in the [Discussions](https://github.com/eeverman/andhow/discussions) or the
[user forum](https://groups.google.com/g/andhowuser)

### Project Tenets and Development Guidelines
#### AndHow must use no runtime dependencies
AndHow is a low level utility that can be used in any application or other utility.
If AndHow has dependencies, that can lead to version conflicts when included in other projects.
AndHow does have dependencies for testing and at compile time (the tools.jar / jdk.compile module),
but none of these are dependencies at runtime.

#### AndHow must have good quality and effective test coverage
As a low level utility, we don't want user's to have to second guess if it is working correctly.
That does not mean that test coverage must be 100%, but the tests should give confidence that
the code functions as intended and is capable of catching new bugs.

Part of AndHow is an annotation processor at _compile time_, so there are unique testing challenges.
Caveats aside, the current test coverage (around 87%) should be improved. 

### Other ways to contribute
* Report a bug or suggest a feature on the [issue tracker](https://github.com/eeverman/andhow/issues)
* Submit pull-requests to improve the Javadocs or [test coverage](https://app.codecov.io/gh/eeverman/andhow)
* Any corrections or added documentation needed on the [AndHow site](https://www.andhowconfig.org)
can be opened as an issue on the project.
* Suggest a new example for the [AndHow Samples Project](https://github.com/eeverman/andhow-samples)