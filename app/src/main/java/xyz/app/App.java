package xyz.app;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.staticfiles.Location;
import xyz.controllers.*;
import xyz.dbkit.DBMain;

import java.io.IOException;

/**
 * Main application entry point. Instantiates a Javalin server on the specified port, configures the static file
 * routing, instantiates the Database and configures all endpoint routing to controller methods. AppManager takes care
 * of session handling and security for authentication to routes. Also stores router endpoints and delegates calls
 * to their handlers functionally.
 */
public class App {
    /**
     * Main entry point method
     * Requirement(App 1.1)
     * @param args JVM args to application currently not handled. Start application with no arguments
     * @throws IOException File I/O Exception for instantiation error
     */
    public static void main(String[] args) throws IOException {

        //Initiate Controllers - Form API
        Javalin app = Javalin.create();
        app.config.addStaticFiles("resources/web", Location.EXTERNAL);
        app.start(8080);
        DBMain myDB = new DBMain("xyz-db");
        RouteManager myRouter = new RouteManager();
        AppManager myApp = new AppManager(myDB, myRouter);
        myApp.setRemoteURL("http://localhost:8080");
        BaseController BaseEndpoint = new BaseController(myDB, myApp);
        IndexController IndexEndpoint = new IndexController(myDB, myApp);
        UserController userController = new UserController(myDB, myApp);
        ModelsController modelController = new ModelsController(myDB, myApp);
        AdminController adminController = new AdminController(myDB,myApp);
        GroupController groupController = new GroupController(myDB, myApp);
        SiteController sitesController = new SiteController(myDB,myApp);
        ThemeController themesController = new ThemeController(myDB, myApp);
        PageController pagesController = new PageController(myDB, myApp);


        myDB.start();


        //Build Model GET API
        myApp.AddGetRoute("/", IndexEndpoint::Index, "Index Main");
        myApp.AddGetRoute("/users", IndexEndpoint::Users, "Login/Registration");
        myApp.AddGetRoute("/admin", adminController::main, "Admin Dashboard",3);
        myApp.AddGetRoute("/schema", adminController::Schema, "Prints out server routes",4);
        myApp.AddGetRoute("/usergroups", adminController::UsersGroups, "Dashboard Users",3);
        myApp.AddGetRoute("/site", adminController::Sites, "Sites Dashboard",3);
        myApp.AddGetRoute("/model/default/:name", modelController::DefaultModel, "Return Model JSON Properties for Model of :name type");
        myApp.AddGetRoute("/model/edit/:name/:id", modelController::ModelEdit, "Return Model JSON where :name is the model type, :id is its UID", 3);
        myApp.AddGetRoute("/model/delete/:name/:id", modelController::ModelDelete, "Delete Model were :name is the model type, :id is the the model UID", 3);
        myApp.AddGetRoute("/users/search/:name", userController::GetUser, "Find specific user");
        myApp.AddGetRoute("/users/find/:name", userController::GetUserStarts, "Find user substring");
        myApp.AddGetRoute("/groups/list", groupController::GetGroups, "Lists All Groups", 3);
        myApp.AddGetRoute("/site/get", sitesController::Get, "Gets all sites", 3);
        myApp.AddGetRoute("/site/getKeys", sitesController::GetKeys, "Gets site keys", 3);
        myApp.AddGetRoute("/site/getSite/:name", sitesController::GetID, "Gets site by ID", 3);
        myApp.AddGetRoute("/site/delete/:name", sitesController::DeleteSite, "Deletes Site", 3);
        myApp.AddGetRoute("/themecreate", adminController::Theme, "New theme page", 3);
        myApp.AddGetRoute("/compl", adminController::compl, "Success Page");
        myApp.AddGetRoute("/theme/getKeys", themesController::GetKeys, "List Themes", 3);
        myApp.AddGetRoute("/theme/get", themesController::GetAll, "Get All Themes", 3);
        myApp.AddGetRoute("/theme/get/:name", themesController::GetID, "Get Theme JSON", 3);
        myApp.AddGetRoute("/theme", themesController::Display, "Themes Dashboard", 3);
        myApp.AddGetRoute("/theme/get_files/:name", themesController::GetFiles, "Get files for theme", 3);
        myApp.AddGetRoute("/theme/delete/:name", themesController::DeleteTheme, "Delete theme", 3);
        myApp.AddGetRoute("/site/editSite/:name/:page", sitesController::EditSite, "Edit Site Page", 3);
        myApp.AddGetRoute("/site/getPages/:name", sitesController::GetPages, "Get site pages", 3);
        myApp.AddGetRoute("/site/getPageData/:site/:page", sitesController::GetPageData, "Get page json store", 3);
        myApp.AddGetRoute("/site/r/:site/:page", sitesController::Display, "Display site page");

        //Build Model POST API
        myApp.AddPostRoute("/model/create/:name", modelController::ModelCreate, "Post params with Model Properties to create model :name type", 3);
        myApp.AddPostRoute("/model/update/:name/:id", modelController::ModelUpdate, "Post params to update model :name type", 3);
        myApp.AddPostRoute("/users/login", userController::login, "User Login");
        myApp.AddPostRoute("/users/submit", userController::SubmitUser, "Creates user with default privileges");
        myApp.AddPostRoute("/theme/create", themesController::CreateTheme, "Create Theme", 3);
        myApp.AddPostRoute("/theme/addFiles", themesController::AddFiles, "Add Files to Theme", 3);
        myApp.AddPostRoute("/theme/deleteFile/:name", themesController::DeleteFile, "Delete File", 3);
        myApp.AddPostRoute("/site/addSite", sitesController::CreateSite, "Create a new site", 3);
        myApp.AddPostRoute("/site/addFiles/:name", sitesController::AddFiles, "Add Files To Site", 3);
        myApp.AddPostRoute("/site/deleteFile/:name", sitesController::DeleteFile, "Delete Site File", 3);
        myApp.AddPostRoute("/site/savePage/:site/:page", sitesController::SavePage, "Saves CMS Site Page", 3);
        myApp.AddPostRoute("/site/fetchURL/:site", sitesController::FetchURL, "Uploads image file by URL", 3);
        myApp.AddPostRoute("/site/addPage/:site", sitesController::AddPage, "Adds page to the site", 3);

        //Javalin Pre/Pos
        app.before("/user/*", userController::pre);
        app.before("/model/*", modelController::pre);
        app.before("/admin", adminController::pre);
        app.before("/", IndexEndpoint::pre);
        app.after("/user/*", userController::post);
        app.after("/model/*",modelController::post);
        app.after("/admin", adminController::post);
        app.after("/", IndexEndpoint::post);
        //Add Routing to Javalin
        for (String key : myApp.GetRouteKeys()){
            Handler method = myApp.GetHandler(key);
            app.get(key, method);
        }

        for (String key : myApp.PostRouteKeys()){
            Handler method = myApp.PostHandler(key);
            app.post(key, method);
        }

    }
}
