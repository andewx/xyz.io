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

When accessing the database for model usage or edits you'll need to utilize an instance of the Controller class that has a reference to the database.


## Working with Templates ``xyz.webkit``
Templates are what will allow us to easily work with reusable HTML/JS strings and replace
tags with our own data from the controller/models. Templates are assumed to be pulled in from
the ``app/templates`` directory, or more correctly ``.current_module_dir/templates``.

The template object scans the ``.html`` files for ``@:somekey`` and gets those keys. The controllers
will have to know what keys they are working with and add them to the template object with ``Template.AddKey(String key, String value)``. Then call 
``Template.ReplaceKeys()`` to get the reformmatted HTML. You can get the keys in the HTML file with ``Template.GetKeys()``

Here is an example:
```
<!DOCTYPE html>
<html><head>
<title>@:recipeName</title>
</head>
<body>
<div class="user-info">@:username</div>
</body>
</html>



## Forms
There should be two ``RouteManagers`` that you make. One set of routes for the GET posts and the other for
all the POST. Javalin differentiates between adding these routes with ``app.get()`` & ``app.post()``. Use the context object
``Context.formParamsMap()`` to retrieve posted parameters.

