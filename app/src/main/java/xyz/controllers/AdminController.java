package xyz.controllers;

import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.model.User;
import xyz.webkit.SiteTemplate;

public class AdminController extends BaseController{

    public AdminController(DBMain db_instance, AppManager appManager) {
        super(db_instance, appManager);
    }

    public void main(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/dash-main.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("title", "Good Morning, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Good Morning, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Here's what's going on with your sites");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }

    public void UsersGroups(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/dash-main.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("title", "Good Morning, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Good Morning, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Here's what's going on with your sites");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }


}
