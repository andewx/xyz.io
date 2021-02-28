package xyz.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import xyz.app.AppManager;
import xyz.app.Security;
import xyz.dbkit.DBMain;


import java.util.HashMap;
import java.util.Map;

public class BaseController implements Handler {

    DBMain mDB;
    String mRouteFrom;
    AppManager mApp;
    Integer mSecurity;

    public BaseController(DBMain db_instance, AppManager appManager) {
        mDB = db_instance;
        mRouteFrom = "";
        mApp = appManager;
        mSecurity = 6;
    }

    public void pre(Context ctx){
        Security securityObj = new Security();
        String securityGroup = "";
        try {
            securityGroup = ctx.cookie("GUID");
            if(securityGroup != null) {
                mSecurity = mApp.GetSessionSecurity(securityGroup);
            }else{
                mSecurity = 6;
            }
        }catch(Exception e){//Do nothing
            mSecurity = 6;
        }

        int routeSecurity = mApp.GetRouteSecurity(ctx.matchedPath());
        Security.Resolver myResolver = securityObj.HasPermission(mSecurity, routeSecurity );

        if(!myResolver.IsValid()){
            ctx.redirect("/", 501); //Redirect home with error.
        }else{
            if(myResolver.NeedsResolve()){
                resolve(ctx);
            }
        }
    }

    public void post(Context ctx){
        mRouteFrom = ctx.url(); //Sets the last url
    }

    public void resolve(Context ctx){
        //Override resolver when objects needs to be owned by user.
    }


    public void handle(@NotNull Context ctx) throws Exception {

    }
}
