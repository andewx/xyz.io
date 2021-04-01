package xyz.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.app.Security;
import xyz.dbkit.DBMain;
import xyz.model.User;

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

    public User UserFromSession(Context ctx){
        User thisUser;
        try{
            String userID = ctx.cookie("USER");
            JSONObject thisModel  = mDB.findKey(mDB.GetNode("Users"), "User", userID);
            thisUser = new User(thisModel);
            return thisUser;
        }catch(Exception e){
            return null;
        }
    }

    public void pre(Context ctx){

        try {
            Security securityObj = new Security();
            String securityGroup = "";
            mSecurity = 6; //DEFAULT

            int routeSecurity = mApp.GetRouteSecurity(ctx.matchedPath());
            if(routeSecurity == 6){
                return;
            }

            try {
                securityGroup = ctx.cookie("GUID");
                ctx.cookie("ERROR", "");
                ctx.cookie("MESSAGE", "");
                if (securityGroup != null) {
                    mSecurity = mApp.GetSessionSecurity(securityGroup);
                } else {
                    mSecurity = 6;
                }
            } catch (Exception e) {//Do nothing
                ctx.cookie("ERROR", "NOEXIST");
                ctx.cookie("MESSAGE", "NOPRIV");
                mSecurity = 6;
            }

            if (mSecurity != null) {
                Security.Resolver myResolver = securityObj.HasPermission(mSecurity, routeSecurity);
                if (!myResolver.IsValid()) {
                    ctx.cookie("MESSAGE", "DENIED");
                    ctx.redirect("http://localhost:8080/", 200); //Redirect home with error.
                } else {
                    if (myResolver.NeedsResolve()) {
                        resolve(ctx);
                    }
                }
            } else {
                ctx.cookie("ERROR", "NOPRIV");
                ctx.cookie("MESSAGE", "DENIED");
                ctx.redirect("http://localhost:8080/", 200); //Redirect home with error.
            }
        }catch(Exception e){
            ctx.cookie("ERROR", "DENIED");
            ctx.cookie("MESSAGE", "DENIED");
            ctx.redirect("http://localhost:8080/", 200); //Redirect home with error.
        }

    }

    public void post(Context ctx){
        mRouteFrom = ctx.url(); //Sets the last url
        ctx.removeCookie("MESSAGE");
        ctx.removeCookie("ERROR");
    }

    public void resolve(Context ctx){
        //Override resolver when objects needs to be owned by user.
    }


    public void handle(@NotNull Context ctx) throws Exception {

    }
}
