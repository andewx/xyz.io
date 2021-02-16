# ``xyz.app`` Guide

## Overview 
This guide goes over some of the basic usage of routes and controllers from the
``xyz.app`` pacakge.

## Javalin and Controller Interface
``ControllerInterface.java`` just extends Javalin's ``http.Handler`` interface. This
interface allows us to assemble the various methods that we put in our controllers
into the ``RouteManager`` routes ``HashMap``.

When we place a method into ``RouteManager.RouteMethods`` map we can either reference static methods
or the instance methods. All methods take a ``Javalin.Context`` single parameter.

If you are working with models and the database. You will need to be using an instanced class with
a reference to the database. Retrieve and Add data on the ``DBNodes``. The database should auto sync
in the future to save these models. 


## Working with Templates ``xyz.webkit``
Templates are what will allow us to easily work with reusable HTML/JS strings and replace
tags with our own data from the controller/models. Templates are assumed to be pulled in from
the ``app/templates`` directory, or more correctly ``.current_module_dir/templates``.

The template object scans the ``.html`` files for ``@:somekey`` and gets those keys. The controllers
will have to know what keys they are working with and add them to the template object with ``Template.AddKey(String key, String value)``. Then call 
``Template.ReplaceKeys()`` to get the reformmatted HTML. You can get the keys in the HTML file with ``Template.GetKeys()``

Views are sometimes included in web frameworks but for now everything as a general template works fine.

## Forms
There should be two ``RouteManagers`` that you make. One set of routes for the GET posts and the other for
all the POST. Javalin differentiates between adding these routes with ``app.get()`` & ``app.post()``. Use the context object
``Context.formParamsMap()`` to retrieve posted parameters.
