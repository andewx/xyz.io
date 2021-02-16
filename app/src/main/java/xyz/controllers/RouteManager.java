package xyz.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RouteManager {
    public HashMap<String, Handler> RouteMethods;

    public RouteManager(){
        super();
        RouteMethods = new HashMap<String, Handler>();
    }

    public void Register(String route,Handler func){
        RouteMethods.put(route, func);
    }

    public Handler getHandler(String key){
        return (Handler)RouteMethods.get(key);
    }

    public String PrintAPI(){
        return null;
    }

}
