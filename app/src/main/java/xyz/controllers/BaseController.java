package xyz.controllers;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.dbkit.DBMain;

import java.util.Map;
import java.util.function.Consumer;

public class BaseController implements ControllerInterface{

    DBMain db;

    public BaseController(DBMain db_instance){
        db = db_instance;
    }

    public static int getUserSecurity(Context ctx){
        int UserSecurity = 5; //Public
        Map<String,String> Cookies = ctx.cookieMap();
        String userSecurity = Cookies.get("Security");
        if(userSecurity != null){
            Integer myInt = Integer.getInteger(userSecurity);
            if(myInt != null){
                UserSecurity = myInt.intValue();
            }
        }

        return UserSecurity;
    }

    public static void SecurityError(Context ctx){
        String HTML = "<!DOCTYPE html><html><head><title>Index</title></head><body><h1>Security Permissions Error</h1><p>User Does Not Have Access</p></body></html>";
        ctx.contentType("html");
        ctx.result(HTML);
    }


    @Override
    public void handle(@NotNull Context ctx) throws Exception {

    }
}
