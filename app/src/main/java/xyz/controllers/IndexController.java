package xyz.controllers;

import io.javalin.http.Context;
import xyz.dbkit.DBMain;

public class IndexController extends BaseController{

    public IndexController(DBMain db_instance) {
        super(db_instance);
    }

    public static void Index(Context ctx){
        int SecurityLevel = 5;
        int UserSecurity = getUserSecurity(ctx);
        if (UserSecurity <= SecurityLevel) {
            String HTML = "<!DOCTYPE html><html><head><title>Index</title></head><body><h1>Base Controller Index</h1><p>This is the base controller index page</p></body></html>";
            ctx.contentType("html");
            ctx.result(HTML);
        }else{
            SecurityError(ctx);
        }
    }
}
