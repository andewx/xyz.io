## xyz.app Module Developers Guide

The App Module of package xyz consists of the root web/resources directory and all associated web contents, along with the `App` `AppManager`
`Security` `Sessions` and `xyz.controllers` package. 

The `xyz.app` package is where you will be configuring your server configuration which lies on top of a `javalin.io` framework
to handle http requests. You the developer will be mostly concerned with implementing your server business logic, mediated by 
classes you extend from `BaseController` and references the `DBMain` database instance and relevant `xyz.ModelObject` subclasses for your application. 

## App Configuration

The first task you may want to do when setting up your application is set the main configuration clauses for the application.
Currently this is done through the API.

``` //Initiate Controllers - Form API
        Javalin app = Javalin.create();
        app.config.addStaticFiles("resources/web", Location.EXTERNAL);
        app.start(8080);
        DBMain myDB = new DBMain("xyz-db");
        RouteManager myRouter = new RouteManager();
        AppManager myApp = new AppManager(myDB, myRouter);
        myApp.setRemoteURL("http://localhost:8080");
```
Here are the first few lines after entry into the `static main()` method entry point. A javalin app instance 
is easily created and the static directory for non-mapped URL routes is set for the local `resouces/web` directory
, `Location.EXTERNAL` ensures that Filesystem operations occur outside of the Java classpath. Operating with-in the classpath is problematic 
when trying to load dynamic content. 

`app.start(8080);` signals that our application is to served locally on port 8080. You can choose any port 
most relevant for your application. 

A new database instance is created with `new DBMain("xyz-db")`. When a new instance is created the database
automatically looks to load in the `Model.keys` file associated with a particular
`ModelObject` class. The creation of the database enters its own runtime thread of execution when `mDB.start()` is called.

Next is our `RouteManager` this tracks which Javalin route GET/POST methods are instantiated for a particular route 
and stores the controller handle. Whenever javalin recieves a request that request is sent off in a functional
thread of execution. 

The `AppManager` takes the database instance and the route instance and handles the internal `Sessions` and `Security` routines
which should not typically be managed by the user, unless they specifically want to alter security and sessions settings.

## Register Routes

Routes are registered by specifying a URL and the appropriate controller handle (instanced controller).

` myApp.AddGetRoute("/site", adminController::Sites, "Sites Dashboard",3);`

Here a route, handler, description, and security access level are given. The security access level may be omitted
and the default value 6 is chosen. Post routes are handled exactly the same, with 
`myApp.AddPostRoute(...);`

## Controllers

Extend all of your custom controllers from the `BaseController` class. This ensures that any ubiquitous pre, post route visit
logic can be handled, such as getting the user session from the `Javalin.Context` parameter. Sessions are managed by http cookies within this context.
Additionally much of the http handling is dependent on access to the `Javalin.Context` parameter. Here you will find the
path parsed url parameters, post parameters, and well as set the response types and data. 

``Context.pathParam()``
`Context.formParam()`
`Context.contentType()`
`Context.result()` are the relevant APIs you will be using from the controller classes.

Controllers are also passed a `DBMain` and `AppManager` instance on creation for reference to the memory model and the 
application context. 

# Sessions

A `Session` object stores the Application user state with a `HashMap` that maps a `GUID`, which is a random generated unique ID
stored with a user. This maps to a `UserKeyState`. This `UserKeyState` stores the User key, a message, the user access level,
their last login, and a log parameter. This can be updated to handle more sophisticated user sessions but for now this is the base functionality offered.

