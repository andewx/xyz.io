# XYZ Recipes
## Overview
Web framework that supports a Recipe productivity planning tool in Java.

## ``Gradle System Build``

To compile and build the project in your respective IDE please download and install gradle. This can be done through
```brew install gradle``` command in bash. 

Additionally most modern IDE's support or come installed with native Gradle handling. You can clone this project with
```git clone https://github.com/andewx/xyzRecipes```

And then Open & Import the root directory in your IDE. It may take several minutes to index your files and download dependencies. 

---
### ``Module Concepts``

Gradle Builds supports multiple build modules. Each of theses modules should be recognized by your IDE. In each ``main`` folder for your module, i.e. ``modelkit/main`` you will see a ``gradle.build`` file. This is essentially a sub-component build settings file and imports some high-level build settings from the ``buildSrc`` folder. 

The modules we build are: #### ``xyz.app`` ``xyz.webkit`` ``xyz.modelkit`` ``xyz.dbkit`` ``xyz.utilities``

System requirements phase will go over these modules. 

---
See ## ``NOTES.md`` for git version control. 

Familiarize yourself with using the build system by cloning the repository. Checking into the development repository. Creating a module-branch in git. Creating your own working commit. And merging with your module-branch for practice. 





