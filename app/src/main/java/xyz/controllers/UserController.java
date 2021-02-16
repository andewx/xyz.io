package xyz.controllers;

import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.User;

public class UserController extends BaseController{

    public UserController(DBMain db_instance) {
        super(db_instance);
    }

    public static void CreateUserForm(Context ctx){
        int SecurityLevel = 5;
        int UserSecurity = getUserSecurity(ctx);
        if(UserSecurity < SecurityLevel){
            String HTML = "<!DOCTYPE html><html><head><title>Index</title></head><body><h1>Base Controller Index</h1><p>Create User Security Form</p></body></html>";
            ctx.contentType("html");
            ctx.result(HTML);
        }else{
            SecurityError(ctx);
        }
    }

    public void CreateUser(Context ctx){ //Form Post Method
        int SecurityLevel = 5;
        int UserSecurity = getUserSecurity(ctx);
        if(UserSecurity < SecurityLevel){
            Map<String, List<String>> Parameters = ctx.formParamMap();
            List<String> UserParams = Parameters.get("User");
            String first = UserParams.get(0);
            String last = UserParams.get(1);
            String email = UserParams.get(2);
            String username = UserParams.get(3);
            String password = UserParams.get(4);

            //Create new user
            DBNode userNode = db.GetNode("Users");
            User newUser = new User(email, password, username,first,last);
            db.addModel(userNode, newUser);

        }else{
            SecurityError(ctx);
        }


    }
}
