# WEBKIT NOTES

### EARLY REQUIREMENTS - Subject to change
- RESTFUL Server API Request HTTP Handler
- Integrated fully into standalone application
- Lightweight HTTP Webserver Resource Handler As Well.
- Controllers map to endpoint business logic functionality


### NOTES:
- ``javalin.io``. seems like a good option
  
- If there are others
let me know but either way we probably want to avoid having to complete the entire web server in itself
  
- Remember the framework should be standalone , NOT REQUIRE A WEBSTACK.

- Need to see which server api will be best for us. ``Javalin``. ``Spark``. ``Undertow``. ``Jetty``. 


### JAVALIN.IO

- Javalin.io is a great layer of abstraction. We can just configure the routes in the app module and tell it to run our handlers. And voila we have every ready to go. 

- At it's heart it is a (Jetty) webserver in Java. So it is capable of receiving HTTP requests and serving back static content. With a powerful REST declaration framework on top. Just the bare minimum.
```
app.post("/", ctx -> {
    // some code
    ctx.status(201);
});
```
Note that there is an -ism regarding threaded requests. A ``Future`` javalin object is passed to the router request ``(post\get\put\etc)``
```import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create().start(7000)
    app.get("/") { ctx -> ctx.result(getFuture()) }
}

// hopefully your future is less pointless than this:
private fun getFuture() = CompletableFuture<String>().apply {
    Executors.newSingleThreadScheduledExecutor().schedule({ this.complete("Hello World!") }, 1, TimeUnit.SECONDS)
}
```

### STRUCTURE
- With most of the server implementation handled by javalin we'd spend likely most of our effort
writing the levels just above the web server here
  
- ``Endpoints`` ``Authentication`` ``Template`` ``Sessions`` ``User`` 
- And whatever else we decide.