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
        templatizer.AddKey("dashSubtitle", "Manage your groups and users from here");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }

    public void Sites(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/sites-page.html");
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        templatizer.AddKey("title", "Hi, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Hello, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Here are your sites");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }

    public void Theme(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/theme-page.html");
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        templatizer.AddKey("title", "Hi, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Hello, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Lets create a new theme");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }

    public void compl(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }

        String guid = ctx.cookie("GUID");
        String message = mApp.GetSessionMessage(guid);
        String content = "<div class='complete'><h1 class='success'>"+message+". Congrats</h1></div>";
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("dashcontent",content);
        templatizer.AddKey("title", "Hi, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Hello, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Completed!");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }


}
