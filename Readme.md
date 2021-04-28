# XYZ.IO Framework Guide
## Overview
Java Web Framework for deploying a self contained Web Framework in Java. Built on top of Javalin.io API.

Gradle build system is modularized. (multi-application build framework). 

Main Components:
``xyz.app`` , ``xyz.webkit``, ``xyz.modelkit``, ``xyz.dbkit``

Which translates to a well defined and familiar MVC pattern. Which we have wrapped in interfaces for easy usage. 

See project issues for feature adding plans. 

## ``Gradle System Build``

To compile and build the project in your respective IDE please download and install gradle. This can be done through
```brew install gradle``` command in bash. 

Additionally most modern IDE's support or come installed with native Gradle handling. You can clone this project with
```git clone https://github.com/andewx/xyz.io```

Gradle can be run with different task options you should run gradle from the root of your idea under the task ``xyzrun`` to
compile and run the project. Tests can be run inside different gradle components with the ``./gradle test`` command inside that package.

And then Open & Import the root directory in your IDE. It may take several minutes to index your files and download dependencies.

---
### ``Module Concepts``

Gradle Builds supports multiple build modules. Each of theses modules should be recognized by your IDE. In each ``main`` folder for your module, i.e. ``modelkit/main`` you will see a ``gradle.build`` file. This is essentially a sub-component build settings file and imports some high-level build settings from the ``buildSrc`` folder. 

The modules we build are: 

### ``xyz.app`` ``xyz.webkit`` ``xyz.modelkit`` ``xyz.dbkit`` ``xyz.utilities``

The idea is that you can handle your tasks and complete tests independently in each module and then ask to merge.

System requirements phase will go over these modules. 

---
See 

## ``NOTES.md`` for git version control. 

Familiarize yourself with using the build system by cloning the repository. Checking into the development repository. Creating a module-branch in git. Creating your own working commit. And merging with your module-branch for practice. 

---
## ``XYZ Framework``
### Overview
XYZ Framework is a fully self-encapsulated framework capable of deploying an in place web application solution. No other stack is required with all components required completely self contained. 

Standard Web Framework MVC architecture is expanded by integrating a Data Permanence solution with a JSON Database. 

``xyz.webkit`` is responsible for managing ``SiteTemplate`` file operations such as filling html files with keyed data.

``xyz.app`` manages a lightweight server with REST controller routes and exposes APIs to consumers. Security handling is managed via simple groupID exclusion security levels rather than ACL tree lists. 

``xyz.dbkit`` manages the database instance for the framework and helps setup and configure JSON data.

``xyz.modelkit`` Is the application Model representation for JSON Objects, adding models to your application is as simple as extending the ModelObject class.

For more information on each package please refer to that packages README.md file.

To run the latest build clone the repository, cd into the main project folder, and run ``./gradlew run`` this will start the main
server which can be accessed on ``http://localhost:8080/`` in your browser of choice. 





