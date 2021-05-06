package xyz.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.app.Security;
import xyz.dbkit.DBMain;
import xyz.model.User;

/**
 * BaseController contains the core controller functionality, all other controllers should inherit from
 * this class. It extends handler which provides the Functional access of the called methods. Does pre and post
 * checking on controller route access, and establishes the unique session for this call instance.
 */
public class BaseController implements Handler {

    /**
     * The M db.
     */
    DBMain mDB;
    /**
     * The M route from.
     */
    String mRouteFrom;
    /**
     * The M app.
     */
    AppManager mApp;
    /**
     * The M security.
     */
    Integer mSecurity;

    /**
     * Base controller constructor
     * Requirement(App 1.2)
     * @param db_instance the db instance
     * @param appManager  the app manager
     */
    public BaseController(DBMain db_instance, AppManager appManager) {
        mDB = db_instance;
        mRouteFrom = "";
        mApp = appManager;
        mSecurity = 6;
    }

    /**
     * Constructs user from session given cookie information
     *
     * @param ctx the ctx
     * @return user
     * Requirement(App 1.2)
     */
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

    /**
     * Pre route handling add to this for any additional logic for pre route calls
     * Requirement(App 1.2)
     * @param ctx the ctx
     */
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

    /**
     * Post route handling add to this for any additional post route logic
     * Requirement(App 1.2)Pre
     * @param ctx the ctx
     */
    public void post(Context ctx){
        mRouteFrom = ctx.url(); //Sets the last url
        ctx.removeCookie("MESSAGE");
        ctx.removeCookie("ERROR");
    }

    /**
     * Not implemented but provides a functional base if user wishes to provide unique resolvers to user owned
     * not group owned routes
     * Requirement(App 1.2)
     * @param ctx the ctx
     */
    public void resolve(Context ctx){
        //Override resolver when objects needs to be owned by user.
    }

    /**
     * Handler entry point function for Javalin server calls
     * @param ctx
     * @throws Exception
     */
    public void handle(@NotNull Context ctx) throws Exception {

    }
}
