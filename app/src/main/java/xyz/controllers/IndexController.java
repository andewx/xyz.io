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

/**
 * Index controller handles the public facing index page site
 */
public class IndexController extends BaseController{

    /**
     * Instantiates a new Index controller.
     * Requirement(App 1.1)
     * @param db_instance the db instance
     * @param app         the app
     */
    public IndexController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    /**
     * Index Page display
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void Index(Context ctx){
        SiteTemplate templatizer = new SiteTemplate(mApp.mRuntimeDir);
        StringBuilder htmlResponse = new StringBuilder();
        templatizer.GetTemplate(mApp.mRuntimeDir + "/templates/explore.html");
        templatizer.AddKey("servername", "xyz-javalin-server");
        templatizer.AddKey("port", String.format("%d",ctx.port()));
        templatizer.AddKey("listening", WhatIP());
        templatizer.AddKey("dbnodes", mDB.NumberNodes());
        templatizer.AddKey("dbname", mDB.GetName());

        templatizer.AddKey("numnodes", mDB.NumberNodes());
        templatizer.AddKey("ip", WhatIP());
        templatizer.AddKey("username", "andewx");
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());

    }

    /**
     * Users page display
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void Users(Context ctx){

        SiteTemplate templatizer = new SiteTemplate(mApp.mRuntimeDir);
        SiteTemplate viewForms = new SiteTemplate(mApp.mRuntimeDir);
        StringBuilder htmlResponse = new StringBuilder();
        String message = "";

        try{
            String recieve = ctx.cookie("MESSAGE");
            if(recieve.equals("ADDED")){
                message = "User added!";
            }
        }catch(Exception e){
            //Do nothing
        }
        templatizer.GetTemplate(mApp.mRuntimeDir + "/templates/ion.html");
        viewForms.GetTemplate(mApp.mRuntimeDir + "/views/register-login.html");
        templatizer.AddKey("controllerTitle", "Framework Login/Registration");
        viewForms.AddKey("Message", message);
        viewForms.ReplaceKeys();
        templatizer.AddKey("controllerContent", viewForms.GetHtml());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }

    /**
     * Resolves User IP (for funsies)
     * Requirement(App 1.1)
     * @return string
     */
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
