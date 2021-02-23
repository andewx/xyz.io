package xyz.controllers;

import io.javalin.http.Context;



import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.User;
import xyz.webkit.SiteTemplate;

public class UserController extends BaseController{

    public UserController(DBMain db_instance, DBNode users_node, RouteManager router) {
        super(db_instance);
    }

    public void main(Context ctx){
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate viewTemplate = new SiteTemplate();
        templatizer.GetTemplate("templates/ion.html");
        viewTemplate.GetTemplate("views/usertop.html");

        //Construct user listing
        StringBuilder sb = new StringBuilder();

        templatizer.AddKey("controllerTitle", "Framework User/Group Security Settings");
        templatizer.AddKey("controllerContent", viewTemplate.GetHtml());
        templatizer.ReplaceKeys();
        sb.append(templatizer.GetHtml());
        ctx.contentType("html");
        ctx.result(sb.toString());
    }

}
