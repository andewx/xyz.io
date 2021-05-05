package xyz.controllers;

import io.javalin.http.Handler;

import java.util.HashMap;
import java.util.Set;

/**
 * RouteManager stores the xyz framework application endpoint handlers and their associated security ring access levels
 * Default behavior is store routes as public facing, if you wish to make an endpoint protected use the overloaded
 * Register methods with lower access level integers (1-6). RouteManager is also able to print the Entire Route API to HTML
 *
 * @author briananderson
 */
public class RouteManager {
    private HashMap<String, Handler> RouteGetMethods;
    private HashMap<String, Handler> RoutePostMethods;
    private HashMap<String, RouteConfig> RouteConfigs;
    private int DEFAULT_PUBLIC = 6; //Default public security

    /**
     * Route Manager constructor
     */
    public RouteManager(){
        RouteGetMethods = new HashMap<String, Handler>();
        RoutePostMethods = new HashMap<String, Handler>();
        RouteConfigs = new HashMap<String,RouteConfig>();
    }

    /**
     * Registers Get Route
     *
     * @param route URL GET route
     * @param func  Handler Function (Controller::Method)
     * @param desc  Description of Route
     */
    public void RegisterGet(String route,Handler func, String desc){
        RouteGetMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(DEFAULT_PUBLIC, desc));
    }

    /**
     * Registers POST route
     *
     * @param route URL POST route
     * @param func  Handler Function (Controller::Method)
     * @param desc  Description of Route
     */
    public void RegisterPost(String route,Handler func, String desc){
        RoutePostMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(DEFAULT_PUBLIC, desc));
    }

    /**
     * Registers Get route with security level specified
     *
     * @param route URL GET Route
     * @param func  Handler Function (Controller::Method)
     * @param desc  Route Description
     * @param sec   Route Security
     */
    public void RegisterGet(String route,Handler func, String desc, Integer sec){
        RouteGetMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(sec, desc));
    }


    /**
     * Registers POST route with security level specified
     *
     * @param route URL GET Route
     * @param func  Handler Function (Controller::Method)
     * @param desc  Route Description
     * @param sec   Route Security
     */
    public void RegisterPost(String route,Handler func, String desc, Integer sec){
        RoutePostMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(sec, desc));
    }

    /**
     * Removes GET Route
     *
     * @param route the route
     */
    public void RemoveGetRoute(String route){
        RouteGetMethods.remove(route);
    }

    /**
     * Removes POST Route
     *
     * @param route the route
     */
    public void RemovePostRoute(String route){
        RoutePostMethods.remove(route);
    }

    /**
     * Gets Route Keys
     *
     * @return GET Route Keys
     */
    public Set<String> GetKeySet(){
        return RouteGetMethods.keySet();
    }

    /**
     * Gets POST route keys
     *
     * @return POST Route Keys
     */
    public Set<String> PostKeySet(){
        return RoutePostMethods.keySet();
    }

    /**
     * Gets the Route Handler
     *
     * @param key Route Key (URL)
     * @return Handler handler
     */
    public Handler getGetHandler(String key){
        return (Handler)RouteGetMethods.get(key);
    }

    /**
     * Gets the POST Route Handler
     *
     * @param key Route Key (URL)
     * @return Handler handler
     */
    public Handler getPostHandler(String key){
        return (Handler)RoutePostMethods.get(key);
    }

    /**
     * Gets route security level
     *
     * @param route Route key URL
     * @return Security level
     */
    public Integer getRouteSecurity(String route){
        String routeParse = route.replace("*","");
        try {
            Integer sec = RouteConfigs.get(routeParse).getSecurity();
            return sec;
        }catch(NullPointerException e){
            return 6;
        }
    }

    /**
     * Gets route description
     *
     * @param route Route Key
     * @return string
     */
    public String getRouteDesc(String route){
        return RouteConfigs.get(route).getDescription();
    }

    /**
     * Route Configuration classs
     */
    private class RouteConfig{
        /**
         * The M security level.
         */
        Integer mSecurityLevel;
        /**
         * The M description.
         */
        String mDescription;

        /**
         * Constructs route Configuration
         *
         * @param SecLevel    Security Level
         * @param Description Description of Route
         */
        public RouteConfig(Integer SecLevel, String Description){
            mSecurityLevel = SecLevel;
            mDescription = Description;
        }

        /**
         * Gets route Security
         *
         * @return integer
         */
        public Integer getSecurity(){
            return mSecurityLevel;
        }

        /**
         * Gets Route Description
         *
         * @return string
         */
        public String getDescription(){
            return mDescription;
        }

        /**
         * Sets Route Description
         *
         * @param desc Description
         */
        public void setDescription(String desc){
            mDescription = desc;
        }
    }


    //----------------------------
    // API Printing - Route Manager
    //----------------------------

    /**
     * Prints the Route API to Basic HTML
     *
     * @return string
     */
    public String PrintAPI(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='menu-tree'><div class='menu-tree-header'>Endpoint Http Param Methods API (JSON/HTML)</div>");
        for(String route : RouteGetMethods.keySet()){
            sb.append("<div class='submenu-1'>" + route + ": (" + getRouteSecurity(route) + ")" + "<div class='submenu-2'>" + getRouteDesc(route) + "</div></div>");
        }
        sb.append("</div>");
        sb.append("<div class='menu-tree'><div class='menu-tree-header'>Endpoint Form Post Methods API (JSON/HTML)</div>");
        for(String route : RoutePostMethods.keySet()){
            sb.append("<div class='submenu-1'>" + route + ": (" + getRouteSecurity(route) + ")" + "<div class='submenu-2'>" + getRouteDesc(route) + "</div></div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

}
