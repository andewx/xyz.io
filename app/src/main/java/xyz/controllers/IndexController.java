package xyz.controllers;

import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.webkit.SiteTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class IndexController extends BaseController{

    public IndexController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    public void Index(Context ctx){
        SiteTemplate templatizer = new SiteTemplate();
        StringBuilder htmlResponse = new StringBuilder();
        templatizer.GetTemplate("templates/explore.html");
        templatizer.AddKey("servername", "xyz-javalin-server");
        templatizer.AddKey("port", String.format("%d",ctx.port()));
        templatizer.AddKey("listening", WhatIP());
        templatizer.AddKey("dbnodes", mDB.NumberNodes());
        templatizer.AddKey("dbname", mDB.GetName());
        templatizer.AddKey("endpoints", mApp.PrintApi());

        templatizer.AddKey("numnodes", mDB.NumberNodes());
        templatizer.AddKey("ip", WhatIP());
        templatizer.AddKey("username", "andewx");
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());

    }

    public String WhatIP(){
        URL whatismyip = null;
        String ip = "";
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
        } catch (IOException e) {
            ip = "No Internet Connection";
            return ip;
        }

        try {
            ip = in.readLine();
        } catch (IOException e) {
            ip = "No Internet Connection";
        }
       return ip;
    }
}
