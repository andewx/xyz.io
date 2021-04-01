/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xyz.app;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import xyz.controllers.*;
import xyz.dbkit.DBMain;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {

        //Initiate Controllers - Form API
        Javalin app = Javalin.create();
        app.config.addStaticFiles("/web");
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
        myApp.AddGetRoute("/usergroups", adminController::UsersGroups, "Dashboard Users",3);
        myApp.AddGetRoute("/sites", adminController::Sites, "Sites Dashboard",3);
        myApp.AddGetRoute("/model/default/:name", modelController::DefaultModel, "Return Model JSON Properties for Model of :name type");
        myApp.AddGetRoute("/model/edit/:name/:id", modelController::ModelEdit, "Return Model JSON where :name is the model type, :id is its UID", 3);
        myApp.AddGetRoute("/model/delete/:name/:id", modelController::ModelDelete, "Delete Model were :name is the model type, :id is the the model UID", 3);
        myApp.AddGetRoute("/users/search/:name", userController::GetUser, "Find specfic user");
        myApp.AddGetRoute("/users/find/:name", userController::GetUserStarts, "Find specfic user");
        myApp.AddGetRoute("/groups/list", groupController::GetGroups, "Lists All Groups", 3);
        myApp.AddGetRoute("/sites/get", sitesController::Get, "Gets all sites", 3);
        myApp.AddGetRoute("/theme", adminController::Theme, "New theme page", 3);
        myApp.AddGetRoute("/compl", adminController::compl, "Success Page");
        myApp.AddGetRoute("/themes/getKeys", themesController::GetKeys, "List Themes", 3);
        myApp.AddGetRoute("/themes/get", themesController::GetAll, "Get All Themes", 3);
        myApp.AddGetRoute("/themes/get/:name", themesController::GetID, "Get Theme JSON", 3);
        myApp.AddGetRoute("/themes", themesController::Display, "Themes Dashboard", 3);
        myApp.AddGetRoute("/themes/get_files/:name", themesController::GetFiles, "Get files for theme", 3);
        myApp.AddGetRoute("/themes/delete/:name", themesController::DeleteTheme, "Delete theme", 3);
        myApp.AddGetRoute("/themes/deleteFile/:name/:path", themesController::DeleteFile, "Delete File", 3);
        //Build Model POST API
        myApp.AddPostRoute("/model/create/:name", modelController::ModelCreate, "Post params with Model Properties to create model :name type", 3);
        myApp.AddPostRoute("/model/update/:name/:id", modelController::ModelUpdate, "Post params to update model :name type", 3);
        myApp.AddPostRoute("/users/login", userController::login, "User Login");
        myApp.AddPostRoute("/users/submit", userController::SubmitUser, "Creates user with default privileges");
        myApp.AddPostRoute("/themes/create", themesController::CreateTheme, "Create Theme", 3);
        myApp.AddPostRoute("/themes/addFiles", themesController::AddFiles, "Add Files to Theme", 3);

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
