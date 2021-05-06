package xyz.controllers;


import io.javalin.http.Context;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelKeys;
import xyz.model.ModelObject;
import xyz.model.User;

import java.util.ArrayList;

/**
 * The type User controller.
 *
 * @author briananderson  Controller for managing users
 */
public class UserController extends BaseController{

    /**
     * Constructor
     *
     * @param db_instance DB
     * @param appManager  APP
     */
    public UserController(DBMain db_instance, AppManager appManager) {
        super(db_instance, appManager);
    }

    //API Login / Establishes user session. Returns to previous page.

    /**
     * Logs in user and establishes session
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void login(Context ctx){
        String password = ctx.formParam("Password");
        String email = ctx.formParam("Email");
        User userObj;
        ModelObject thisModel;
        thisModel  = mDB.findKey(mDB.GetNode("Users"), "User", email);
        try {
            userObj = new User(thisModel);
        } catch(Exception e){
                System.out.println("Error: User Object Format Invalid");
                System.out.println(thisModel.toString(3));
                ctx.cookie("ERROR", "User or Group Policy Not Found");
                ctx.status(201);
                ctx.redirect("/");
                return;
        }

        //Generate SHA256 Secure Hash
       String entered = User.PasswordSHA(password);
       String userPass = userObj.getPassword();
       if(!entered.equals(userPass)){
           System.out.println("User password doesn't match");
           ctx.cookie("ERROR", "INVALPASS");
           ctx.status(203);
           ctx.redirect("/users");
           return;
       }
        System.out.println("User Logged In!");
        String GUID = mApp.AddSession(userObj.getEmail());
        ctx.cookieStore("GUID", GUID);
        ctx.cookieStore("USER", userObj.getUID());
        ctx.cookie("USER", userObj.getUID());
        ctx.cookie("GUID", GUID);
        ctx.cookieStore("MESSAGE", "SUCCESS");
        ctx.redirect("/admin");
    }

    /**
     * Registers new user.
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void Register(Context ctx){
       User defaultUser = (User) ModelKeys.Default("User");
       String content = defaultUser.Form();
       ctx.contentType("text/html");
       ctx.result(content);
    }

    /**
     * Submits new user
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void SubmitUser(Context ctx){
        String email = ctx.formParam("Email");
        String pass = ctx.formParam("Password");
        String usern = ctx.formParam("Name");
        String first = ctx.formParam("FirstName");
        String last = ctx.formParam("LastName");
        User newUser = new User(email,pass,usern,first,last);
        DBNode users = mDB.GetNode("Users");
        mDB.AddModel(users, newUser);
        ctx.cookieStore("MESSAGE", "ADDED");
        ctx.cookie("MESSAGE", "ADDED");
        ctx.redirect("/users");
    }

    /**
     * Gets user login form
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void GetLogin(Context ctx){
        String loginForm = "<form method='post' url='/users/login'><legend>Login<input type='email' name='Email' value='user@example.com' maxLength='45'/><input type='password' name='Password' maxLength='45'/>"
                + "<input type='submit' value='Login'/> <div class='error-caption'></div></legend></form>";
        ctx.contentType("text/html");
        ctx.result(loginForm);
    }

    /**
     * Gets a user by UID
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void GetUser(Context ctx){
        String name = ctx.pathParam("name");
        User userObj;
        try {
             userObj = (User) mDB.findKey(mDB.GetNode("Users"), "User", name);
        }catch(Exception e){
            ctx.contentType("application/text");
            ctx.result("{}");
            return;
        }
        if(userObj != null){
            ctx.contentType("application/text");
            ctx.result(userObj.toJson());
        }
    }

    /**
     * Gets a user whose name includes a suffix
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void GetUserStarts(Context ctx){
        String name = ctx.pathParam("name");
        ArrayList<ModelObject> userList;
        try {
            if(name.compareTo("*") == 0){
                userList = new ArrayList<>();
                JSONObject children;
                children = mDB.GetNode("Users").GetRoot().getChildren("Users");
                for(String key : children.keySet()) {
                    JSONObject jObj = children.getJSONObject(key);
                    User gObj = new User(jObj);
                    userList.add(gObj);
                }
                ctx.contentType("application/json");
                ctx.result(userList.toString());
                return;
            }else{
                userList = (ArrayList<ModelObject>) mDB.findStartsWith(mDB.GetNode("Users"), "User", name);
            }

            ctx.contentType("application/json");
            ctx.result(userList.toString());

        }catch(Exception e){
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
    }



}
