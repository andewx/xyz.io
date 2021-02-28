package xyz.controllers;


import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.model.ModelKeys;
import xyz.model.ModelObject;
import xyz.model.User;

import java.util.ArrayList;

public class UserController extends BaseController{

    public UserController(DBMain db_instance, AppManager appManager) {
        super(db_instance, appManager);
    }

    //API Login / Establishes user session. Returns to previous page.
    public void login(Context ctx){
        String password = ctx.formParam("Password");
        String email = ctx.formParam("Email");
        User userObj = (User)mDB.findKey(mDB.GetNode("Users"), "User", email);
        //Generate SHA256 Secure Hash
       String entered = User.PasswordSHA(password);
       String userPass = userObj.getPassword();
       if(entered != userPass){
           ctx.cookie("error-message", "Email/Password does not match.");
           ctx.status(201);
           return;
       }

        String GUID = mApp.AddSession(userObj.getEmail());
        ctx.cookieStore("GUID", GUID);
        ctx.cookieStore("message", "User login successful");
        ctx.redirect(this.mRouteFrom);
    }

    public void Register(Context ctx){
       User defaultUser = (User) ModelKeys.Default("User");
       String content = defaultUser.Form();
       ctx.contentType("text/html");
       ctx.result(content);
    }

    public void GetLogin(Context ctx){
        String loginForm = "<form method='post' url='/users/login'><legend>Login<input type='email' name='Email' value='user@example.com' maxLength='45'/><input type='password' name='Password' maxLength='45'/>"
                + "<input type='submit' value='Login'/> <div class='error-caption'></div></legend></form>";
        ctx.contentType("text/html");
        ctx.result(loginForm);
    }

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

    public void GetUserStarts(Context ctx){
        String name = ctx.pathParam("name");
        ArrayList<ModelObject> userList;
        try {
            userList = (ArrayList<ModelObject>) mDB.findStartsWith(mDB.GetNode("Users"), "User", name);
        }catch(Exception e){
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        if(userList!= null){
            ctx.contentType("application/json");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(int index = 0; index < userList.size(); index++){
                if (index != userList.size() - 1) {
                    sb.append(userList.get(index).toJson());
                    sb.append(" , ");
                }else{
                    sb.append(userList.get(index).toJson());
                }
            }
            sb.append("]");
            ctx.result(sb.toString());
        }
    }



}
