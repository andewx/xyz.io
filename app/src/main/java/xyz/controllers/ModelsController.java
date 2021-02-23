package xyz.controllers;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.javalin.http.Context;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.*;

import java.nio.charset.Charset;

public class ModelsController extends BaseController{
    public ModelsController(DBMain db_instance) {
        super(db_instance);
    }

    public void ModelCreateForm(Context ctx){ //takes path params :modelname
        String ModelKey = ctx.pathParam("name");
        ModelObject myObj = ModelKeys.Default(ModelKey);
        if(myObj != null){
            String form = myObj.Form();
            ctx.contentType("text/plain");
            ctx.result(form);
        }
    }

    public void ModelCreate(Context ctx){
        String name = ctx.pathParam("name");
        if(name.equals("Group")){
            String grpID = ctx.formParam("GroupID");
            String descr = ctx.formParam("AccessDescription");
            String access = ctx.formParam("AccessLevel");
            int acc = Integer.valueOf(access).intValue();
            Group groupObj = new Group(grpID, descr, acc);
            DBNode groupNode = db.GetNode("Groups");
            db.AddModel(groupNode, groupObj);
            ctx.result("Group Added!");
        }
        if(name.equals("User")){
            String email = ctx.formParam("Email");
            String pass = ctx.formParam("Password");
            String usern = ctx.formParam("Username");
            String first = ctx.formParam("First");
            String last = ctx.formParam("Last");
            User newUser = new User(email,pass,usern,first,last);
            DBNode users = db.GetNode("Users");
            db.AddModel(users, newUser);
            ctx.result("User Added!");
        }
        if(name.equals("Site")){
            String desc = ctx.formParam("Description");
            String rest = ctx.formParam("RestURL");
            String mName = ctx.formParam("Name");
            String title = ctx.formParam("Title");
            Site newSite = new Site(mName,desc,rest,title);
            DBNode sites = db.GetNode("Sites");
            db.AddModel(sites,newSite);
            ctx.result("Site Added!");
        }
        if(name.equals("Template")){
            String mName = ctx.formParam("Name");
            String html = ctx.formParam("HTML");
            Template newTemplate = new Template(mName, html);
            DBNode templates = db.GetNode("Templates");
            db.AddModel(templates,newTemplate);
            ctx.result("Template Added!");
        }
        if(name.equals("Theme")){
            String mName = ctx.formParam("Name");
            String css = ctx.formParam("CSS");
            Theme newTheme = new Theme(mName, css);
            DBNode themes = db.GetNode("Themes");
            db.AddModel(themes,newTheme);
            ctx.result("Theme Added!");
        }
        if(name.equals("Edge")){
            String mA = ctx.formParam("ModelA");
            String mB = ctx.formParam("ModelB");
            String uA = ctx.formParam("KeyA");
            String uB = ctx.formParam("KeyB");
            String rel = ctx.formParam("Relation");
            Edge newEdge = new Edge(mA,uA,mB,uB,rel);
            DBNode edges = db.GetNode("Edges");
            db.AddModel(edges,newEdge);
            ctx.result("Edge Added!");
        }

        db.interrupt();
    }

    public void ModelEditForm(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = db.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = db.findKey(modelNode,ModelName,ModelKey);
        if(myObj != null){
            String form = myObj.Form();
            ctx.result(form);
        }
    }

    public void ModelEdit(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = db.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = db.findKey(modelNode,ModelName,ModelKey);

        if(ModelName.equals("Group")){
            String grpID = ctx.formParam("GroupID");
            String descr = ctx.formParam("AccessDescription");
            String access = ctx.formParam("AccessLevel");
            int acc = Integer.valueOf(access).intValue();
            Group groupObj = (Group)myObj;
            groupObj.setGroupID(grpID);
            groupObj.setAccessDescription(descr);
            groupObj.setAccessLevel(acc);
            ctx.result("Group Edited");
        }
        if(ModelName.equals("User")){
            String email = ctx.formParam("Email");
            String pass = ctx.formParam("Password");
            String usern = ctx.formParam("Username");
            String first = ctx.formParam("First");
            String last = ctx.formParam("Last");
            User editUser = (User)myObj;

            HashFunction f =  Hashing.sha256();
            HashCode passHash = f.hashString(pass, Charset.defaultCharset());
            editUser.setPassword(passHash.toString());
            editUser.setEmail(email);
            editUser.setFirstName(first);
            editUser.setLastName(last);
            ctx.result("User Edited");
        }
        if(ModelName.equals("Site")){
            String desc = ctx.formParam("Description");
            String rest = ctx.formParam("RestURL");
            String mName = ctx.formParam("Name");
            String title = ctx.formParam("Title");
            Site newSite = (Site)myObj;
            newSite.setDescription(desc);
            newSite.setRestURL(rest);
            newSite.setTitle(title);
            newSite.setName(mName);
            ctx.result("Site Edited");
        }
        if(ModelName.equals("Template")){
            String mName = ctx.formParam("Name");
            String html = ctx.formParam("HTML");
            Template newTemplate = (Template)myObj;
            newTemplate.setHTML(html);
            newTemplate.setName(mName);
            ctx.result("Template Edited");
        }
        if(ModelName.equals("Theme")){
            String mName = ctx.formParam("Name");
            String css = ctx.formParam("CSS");
            Theme newTheme = (Theme)myObj;
            newTheme.setName(mName);
            newTheme.setCSS(css);
            ctx.result("Theme Edited");
        }
        if(ModelName.equals("Edge")){
            String mA = ctx.formParam("ModelA");
            String mB = ctx.formParam("ModelB");
            String uA = ctx.formParam("KeyA");
            String uB = ctx.formParam("KeyB");
            String rel = ctx.formParam("Relation");
            Edge newEdge = (Edge)myObj;
            newEdge.setModelA(mA);
            newEdge.setModelB(mB);
            newEdge.setKeyA(uA);
            newEdge.setKeyB(uB);
            newEdge.setRelation(rel);
            ctx.result("Edge Edited");
        }

        db.UpdateModel(modelNode, myObj);

    }

    public void Delete(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = db.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = db.findKey(modelNode,ModelName,ModelKey);
        modelNode.DeleteModel(myObj);
        db.UpdateModel(modelNode,myObj);
    }






}
