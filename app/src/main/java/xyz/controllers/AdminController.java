package xyz.controllers;

import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.webkit.SiteTemplate;

public class AdminController extends BaseController{

    public AdminController(DBMain db_instance, AppManager appManager) {
        super(db_instance, appManager);
    }

    public void main(Context ctx){
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/dash-main.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        StringBuilder htmlResponse = new StringBuilder();
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("title", "Good Morning, Brian");
        templatizer.AddKey("dashTitle", "Good Morning, Brian");
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }
}
