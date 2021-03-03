package xyz.app;

import io.javalin.http.Handler;
import xyz.controllers.RouteManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.Group;
import xyz.model.User;

import java.time.Instant;
import java.util.Set;

public class AppManager {
   private Sessions mSessions;
   private DBMain mDB;
   private RouteManager mRouteManager;
   private String mName;

   public AppManager(DBMain db, RouteManager router){
       mSessions = new Sessions();
       mDB = db;
       mRouteManager = router;
       mName = "xyzServer";
   }

   public String GetName(){
       return mName;
   }

   public void SetName(String m){
       mName = m;
   }


   public String AddSession(String email){
       DBNode userNode = mDB.GetNode("Users");
       DBNode grpNode = mDB.GetNode("Groups");
       User userObj = (User)mDB.findKey(userNode, "User", email);
       if(userObj != null) {
           Group grpObj = new Group(mDB.findKey(grpNode, "Group", userObj.getGroupID()));
           Integer access = grpObj.GetAccessLevel();
           return mSessions.AddSession(userObj.getEmail(), access); //return GUID
       }

       return null;
   }

   public void AddGetRoute(String route, Handler handle, String desc){
       mRouteManager.RegisterGet(route,handle,desc);
   }

    public void AddPostRoute(String route, Handler handle, String desc){
        mRouteManager.RegisterPost(route,handle,desc);
    }

    public void AddGetRoute(String route, Handler handle, String desc, Integer security_id){
        mRouteManager.RegisterGet(route,handle,desc, security_id);
    }

    public void AddPostRoute(String route, Handler handle, String desc, Integer security_id){
        mRouteManager.RegisterPost(route,handle,desc, security_id);
    }

    public void RemoveGetRoute(String route){
       mRouteManager.RemoveGetRoute(route);
    }

    public void RemovePostRoute(String route){
        mRouteManager.RemovePostRoute(route);
    }

    public Handler GetHandler(String route){
        return mRouteManager.getGetHandler(route);
    }

    public Handler PostHandler(String route){
       return mRouteManager.getPostHandler(route);
    }

    public Set<String> GetRouteKeys(){
       return mRouteManager.GetKeySet();
    }

    public Set<String> PostRouteKeys(){
       return mRouteManager.PostKeySet();
    }


    public Integer GetSessionSecurity(String GUID){
     return mSessions.GetAccess(GUID);
   }

   public Integer GetRouteSecurity(String route){
       return mRouteManager.getRouteSecurity(route);
   }

   public String GetRouteDescription(String route){
       return mRouteManager.getRouteDesc(route);
   }

   public String GetSessionUser(String GUID) {
       return mSessions.GetUsername(GUID);
   }

   public String PrintApi(){
       return mRouteManager.PrintAPI();
   }

   public int Elapsed(String GUID){
       return Instant.now().compareTo(mSessions.GetLastLogin(GUID));
   }

   private synchronized void Remove(String guid){
       mSessions.RemoveSession(guid);
    }

   private void syncSessions(){
       for(String key : mSessions.GetSessions()){
          if( mSessions.GetLastLogin(key).plusSeconds(60*60*60).isAfter(Instant.now())){ //60 hrs max
            Remove(key);
          }
       }
   }



}
