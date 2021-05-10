package xyz.app;

import io.javalin.http.Handler;
import xyz.controllers.RouteManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.Group;
import xyz.model.User;

import java.time.Instant;
import java.util.Set;

/**
 * AppManager manages the POST/GET routing and call handlers for endpoints, manages and tracks user sessions
 * and configures security. Calls on the RouteManager for implementing Route Endpoint handling
 */
public class AppManager {
   private Sessions mSessions;
   private DBMain mDB;
   private RouteManager mRouteManager;
   private String mName;
   public String mRuntimeDir;

    /**
     * Gets the remoteURL For the Server
     *
     * @return remoteURL for the server
     */
    public String getRemoteURL() {
        return remoteURL;
    }

    /**
     * Sets remote Url for the server
     *
     * @param remoteURL Remote URL for the Server
     */
    public void setRemoteURL(String remoteURL) {
        this.remoteURL = remoteURL;
    }

    private String remoteURL;

    /**
     * App Manager constructor, holds the Database, sessions, and the Route Manager
     * Requirement(App 4.0)
     * @param db     DBMain Database
     * @param router RouteManager Router for holding route information
     */
    public AppManager(DBMain db, RouteManager router, String directory){
       mSessions = new Sessions();
       mDB = db;
       mRouteManager = router;
       mName = "xyzServer";
       mRuntimeDir = directory;
   }

    /**
     * Gets Name of AppManager
     *
     * @return string
     */
    public String GetName(){
       return mName;
   }

    /**
     * Sets name of route manager
     *
     * @param m Name
     */
    public void SetName(String m){
       mName = m;
   }


    /**
     * Adds user session with the Key ID session
     * Requirement(App 2.0)
     * @param email the email
     * @return string
     */
    public String AddSession(String email){
       DBNode userNode = mDB.GetNode("Users");
       DBNode grpNode = mDB.GetNode("Groups");
       User userObj = new User(mDB.findKey(userNode, "User", email));
       if(userObj != null) {
           Group grpObj = new Group(mDB.findKey(grpNode, "Group", userObj.getGroupID()));
           if(grpObj !=null) {
               Integer access = grpObj.GetAccessLevel();
               return mSessions.AddSession(userObj.getEmail(), access);
           }else{
               return mSessions.AddSession(userObj.getEmail(),6);
           }
       }

       return "NONE";
   }

    /**
     * Adds a Get Route to the route manager uses default security level 6
     * Requirement(App 4.0)
     * @param route  URL route for handling call
     * @param handle Call handler
     * @param desc   Route Description
     */
    public void AddGetRoute(String route, Handler handle, String desc){
       mRouteManager.RegisterGet(route,handle,desc);
   }

    /**
     * Adds POST route to the RouteManager defaults security level 6
     * Requirement(App 4.0)
     * @param route  URL Route
     * @param handle Route Handler
     * @param desc   Description
     */
    public void AddPostRoute(String route, Handler handle, String desc){
        mRouteManager.RegisterPost(route,handle,desc);
    }

    /**
     * Adds Get Route to the Route Manager
     * Requirement(App 4.0)
     * @param route       URL Route
     * @param handle      Hanlder
     * @param desc        Description
     * @param security_id Security Access Level for Route
     */
    public void AddGetRoute(String route, Handler handle, String desc, Integer security_id){
        mRouteManager.RegisterGet(route,handle,desc, security_id);
    }

    /**
     * Adds POST Route to the Route Manager
     * Requirement(App 4.0)
     * @param route       URL Route
     * @param handle      Hanlder
     * @param desc        Description
     * @param security_id Security Access Level for Route
     */
    public void AddPostRoute(String route, Handler handle, String desc, Integer security_id){
        mRouteManager.RegisterPost(route,handle,desc, security_id);
    }

    /**
     * Removes Get Route
     * Requirement(App 4.0)
     * @param route URL route to be removed
     */
    public void RemoveGetRoute(String route){
       mRouteManager.RemoveGetRoute(route);
    }

    /**
     * Removes POST Route
     * Requirement(App 4.0)
     * @param route URL Route to be Removed
     */
    public void RemovePostRoute(String route){
        mRouteManager.RemovePostRoute(route);
    }

    /**
     * Gets Java Handler for a given Route
     * Requirement(App 4.0)
     * @param route URL Route
     * @return Hanlder handler
     */
    public Handler GetHandler(String route){
        return mRouteManager.getGetHandler(route);
    }

    /**
     * Gets Java Handler for POST route
     * Requirement(App 4.0)
     * @param route URL Route
     * @return Handler handler
     */
    public Handler PostHandler(String route){
       return mRouteManager.getPostHandler(route);
    }

    /**
     * Gets Route Keys
     * Requirement(App 4.0)
     * @return Route URL Keys
     */
    public Set<String> GetRouteKeys(){
       return mRouteManager.GetKeySet();
    }

    /**
     * Gets POST Route Keys
     * Requirement(App 4.0)
     * @return POST Route KEys
     */
    public Set<String> PostRouteKeys(){
       return mRouteManager.PostKeySet();
    }

    /**
     * Gets current Session Security level with associated Cookie UID
     * Requirement(App 2.0)
     * @param GUID the guid
     * @return integer
     */
    public Integer GetSessionSecurity(String GUID){
     return mSessions.GetAccess(GUID);
   }

    /**
     * Gets route security level for Application URL Route
     * Requirement(App 2.0)
     * @param route URL Route
     * @return Int security level
     */
    public Integer GetRouteSecurity(String route){
       return mRouteManager.getRouteSecurity(route);
   }

    /**
     * Gets a Route Description
     * Requirement(App 2.0)
     * @param route URL Route Key
     * @return Route Description
     */
    public String GetRouteDescription(String route){
       return mRouteManager.getRouteDesc(route);
   }

    /**
     * Gets User from session cookie UID
     * Requirement(App 2.0)
     * @param GUID Session Cookie UID
     * @return Session Username
     */
    public String GetSessionUser(String GUID) {
       return mSessions.GetUsername(GUID);
   }

    /**
     * Prints ROUTE Enpoint API
     * Requirement(App 2.0)
     * @return string
     */
    public String PrintApi(){
       return mRouteManager.PrintAPI();
   }

    /**
     * Gets time elapsed from last login
     * Requirement(App 2.0)
     * @param GUID User sessions cookie UID
     * @return Int milliseconds elapsed
     */
    public int Elapsed(String GUID){
       return Instant.now().compareTo(mSessions.GetLastLogin(GUID));
   }

    /**
     * Removes session cookie UID
     * Requirement(App 2.0)
     * @param guid
     */
   private synchronized void Remove(String guid){
       mSessions.RemoveSession(guid);
   }

    /**
     * Sets a session message for the current User ID
     * Requirement(App 2.0)
     * @param msg  Message to be stored
     * @param GUID User session UID
     */
    public void SetSessionMessage(String msg, String GUID){
     mSessions.SetMessage(msg, GUID);
   }

    /**
     * Gets session message for user ID
     * Requirement(App 2.0)
     * @param GUID Session cookie UID
     * @return Session message
     */
    public String GetSessionMessage(String GUID){
    return mSessions.GetMessage(GUID);
   }

    /**
     * Syncs session manager to remove users from long idle times
     */
   private void syncSessions(){
       for(String key : mSessions.GetSessions()){
          if( mSessions.GetLastLogin(key).plusSeconds(60*60*60).isAfter(Instant.now())){ //60 hrs max
            Remove(key);
          }
       }
   }



}
