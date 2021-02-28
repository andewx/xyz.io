package xyz.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RouteManager {
    private HashMap<String, Handler> RouteGetMethods;
    private HashMap<String, Handler> RoutePostMethods;
    private HashMap<String, RouteConfig> RouteConfigs;
    private int DEFAULT_PUBLIC = 6; //Default public security

    public RouteManager(){
        RouteGetMethods = new HashMap<String, Handler>();
        RoutePostMethods = new HashMap<String, Handler>();
        RouteConfigs = new HashMap<String,RouteConfig>();
    }

    public void RegisterGet(String route,Handler func, String desc){
        RouteGetMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(DEFAULT_PUBLIC, desc));
    }

    public void RegisterPost(String route,Handler func, String desc){
        RoutePostMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(DEFAULT_PUBLIC, desc));
    }

    public void RegisterGet(String route,Handler func, String desc, Integer sec){
        RouteGetMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(sec, desc));
    }

    public void RegisterPost(String route,Handler func, String desc, Integer sec){
        RoutePostMethods.put(route, func);
        RouteConfigs.put(route, new RouteConfig(sec, desc));
    }


    public void RemoveGetRoute(String route){
        RouteGetMethods.remove(route);
    }

    public void RemovePostRoute(String route){
        RoutePostMethods.remove(route);
    }

    public Set<String> GetKeySet(){
        return RouteGetMethods.keySet();
    }

    public Set<String> PostKeySet(){
        return RoutePostMethods.keySet();
    }


    public Handler getGetHandler(String key){
        return (Handler)RouteGetMethods.get(key);
    }

    public Handler getPostHandler(String key){
        return (Handler)RoutePostMethods.get(key);
    }


    public Integer getRouteSecurity(String route){
        return RouteConfigs.get(route).getSecurity();
    }

    public String getRouteDesc(String route){
        return RouteConfigs.get(route).getDescription();
    }

    private class RouteConfig{
        Integer mSecurityLevel;
        String mDescription;

        public RouteConfig(Integer SecLevel, String Description){
            mSecurityLevel = SecLevel;
            mDescription = Description;
        }

        public Integer getSecurity(){
            return mSecurityLevel;
        }

        public String getDescription(){
            return mDescription;
        }

        public void setDescription(String desc){
            mDescription = desc;
        }
    }


    //----------------------------
    // API Printing - Route Manager
    //----------------------------

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
