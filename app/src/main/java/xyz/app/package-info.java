/**
 * xyz.app is the main server application instance for xyz. Instantiates a Javalin.io server
 * instance and a xyz.dbkit.DBMain database instance. AppManager serves as the resource pool for
 * storing javalin http.Handler endpoint routes. As of current command line arguments are not
 * used for Application configuration with configuration done through the main entry point routine.
 * The server is configured to access web resources using the app/resources folder outside of the JVM
 * classpath.
 */
package xyz.app;