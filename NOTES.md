# USAGE NOTES

## GIT MANAGEMENT (NOTE YOU MAY USE YOUR IDE TO USE GIT FUNCTIONALITY)
- Sync Scheduled Releases to master

- Minimal branching ```(master -> development -> module/domain)```

- Update local repo with git pull regularly. Especially prior to any commit. use git pull.

- If you current repo is ready for merging with the upstream development branch annotate 

### ``git pull``

``git pull`` in general will sync your local working branch, and all remote tracking branches. Note that ``git fetch`` is a similar command that syncs with
the immediate remote tracked branch. (The one you branched from)

So before you decide to create a branch you should run
```
git checkout mybranch
git pull origin development
```
This ensures that your current local working branch has been updated with the latest tracking branches on the development repository

After you pull. Immediately push your locally integrated branch back to the git server
```
git commit -m "Branch Updated"
git push -u origin branchname
```
Once everything is in place in your branch and you're ready to have your branch ready to push
```
git pull origin development
git commit -m "updated my branch etc..."
git push -u origin branchname
```

The git manager will then be responsible for ensuring the development builds are correct and merging into the master

### ``git merge``
``git merge`` will update the upstream remote branch and merge the specified branches into itself. For our usage this will look like. 
```
git checkout development
git merge user-branch
git branch -d user-branch //deletes user branch
```


---
## BUILD MODULES
This project uses gradle  for system builds which will integrate with most IDEs. The way this project is set up is with multiple
modules in the gradle build environment. Our main modules are:

## ``xyz.webkit`` ``xyz.modelkit`` ``xyz.dbkit`` ``xyz.app``

These modules can be built and tested independently of one another. With only high level dependencies in most cases. What this 
means for you is that you will dedicate your git branch to the development of your assigned module. This lowers the chance
that you'll create tracking difficulties when trying to merge.


##``Gradle Builds``
Modules have their own ``gradle.build`` file. There are two items to note here.
First we have the ``id`` which references to the gradle build reference. This tells gradle
for example if we are using ``java`` to build a java application with certain execution tasks available. Or ``java-library``
which signals that gradle will build a library object out of this module. 

In ``buildSrc/src/main/groovy`` you will see files like ``xyz.java-common-conventions.groovy``
These are ids which basically offer a template for the overall build. Your module build files reference these ids and then adds their own
usage details. Like ``implementation org.json:json:20201115`` which says that our module
depends on a JSON library. 

These implementation notes will tell gradle to download these dependency jars during build. Note that you may need to invalidate
your caches and restart your IDE for the dependencies to properly load.

---
## MODULES & DEVELOPMENT METHODS
Continuous development and project updates are supported with git primarily under the
modules concept. Module domains are what allow us to continously update versions of our own tasks and then
commit to the overall project.

Therefore requirements will be expressed in terms of their module functionality. And tasks will
be assigned to modules. 

If you finish a module and all your tasks keep bolstering test cases. If you're sure your implementation is 100% ready to go coordinate working
on another module. 

### MODULE COORDINATION

Coordinate that you would like to help and see if there is an untouched domain area in that module you can work on. 
Typically this will be a package or subpackage that the owner doesn't expect to modify. Clarify that you'll be taking ownership of that package.

Checkout on their main working branch
``git checkout module-branch``

and then create a branch that's a derivatiion with your name
``git branch module-branch-ANDERSON``

We will stay away from numbering versions except for major builds and releases.
So branches should not look like ``module-branch-v0.1``

When your branch is 100% ready to go and tested. Sync first
``git pull``
and then inform the branch owner that you're going to merge with their module.
Once you get confirmation run ``git merge ``  or ```git rebase```
---

### ``rebase && --no-rebase`` 
When you run ``git merge`` it is implicitly not rebasing. Rebasing in essence makes it appear as if you created your branch from a later update
of the tracked branch ```master``` for example.

### Why use ``git rebase``
All of your commits get stacked on the latest master branch release. For all the master branch knows you've been working of its latest version. 

This could really be called ``clean merge`` even though the actual command is ``git rebase``.
This will probably be our main method of merging. 
