package xyz.controllers;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.*;

import java.nio.charset.Charset;

public class ModelsController extends BaseController{
    public ModelsController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    public void DefaultModel(Context ctx){
        String ModelKey = ctx.pathParam("name");
        ModelObject myObj = ModelKeys.Default(ModelKey);
        if(myObj != null){
          ctx.contentType("application/json");
          ctx.result(myObj.toJson());
        }else{
            ctx.status(201);
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
            DBNode groupNode = mDB.GetNode("Groups");
            mDB.AddModel(groupNode, groupObj);
            ctx.cookieStore("message", "Group added");
            ctx.redirect("/users");
        }
        if(name.equals("User")){
            String email = ctx.formParam("Email");
            String pass = ctx.formParam("Password");
            String usern = ctx.formParam("Username");
            String first = ctx.formParam("First");
            String last = ctx.formParam("Last");
            User newUser = new User(email,pass,usern,first,last);
            DBNode users = mDB.GetNode("Users");
            mDB.AddModel(users, newUser);
            ctx.cookieStore("message", "User added");
            ctx.redirect("/users");
        }
        if(name.equals("Site")){
            String desc = ctx.formParam("Description");
            String rest = ctx.formParam("RestURL");
            String mName = ctx.formParam("Name");
            String title = ctx.formParam("Title");
            Site newSite = new Site(mName,desc,rest,title);
            DBNode sites = mDB.GetNode("Sites");
            mDB.AddModel(sites,newSite);
            ctx.result("Site Added!");
        }
        if(name.equals("Template")){
            String mName = ctx.formParam("Name");
            String html = ctx.formParam("HTML");
            Template newTemplate = new Template(mName, html);
            DBNode templates = mDB.GetNode("Templates");
            mDB.AddModel(templates,newTemplate);
            ctx.result("Template Added!");
        }
        if(name.equals("Theme")){
            String mName = ctx.formParam("Name");
            String css = ctx.formParam("CSS");
            Theme newTheme = new Theme(mName, css);
            DBNode themes = mDB.GetNode("Themes");
            mDB.AddModel(themes,newTheme);
            ctx.result("Theme Added!");
        }
        if(name.equals("Edge")){
            String mA = ctx.formParam("ModelA");
            String mB = ctx.formParam("ModelB");
            String uA = ctx.formParam("KeyA");
            String uB = ctx.formParam("KeyB");
            String rel = ctx.formParam("Relation");
            Edge newEdge = new Edge(mA,uA,mB,uB,rel);
            DBNode edges = mDB.GetNode("Edges");
            mDB.AddModel(edges,newEdge);
            ctx.result("Edge Added!");
        }

        mDB.interrupt();
    }

    public void ModelEdit(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = mDB.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = mDB.findKey(modelNode,ModelName,ModelKey);
        if(myObj != null){
            ctx.contentType("application/json");
            ctx.result(myObj.toJson());
        }else{
            ctx.status(201);
        }
    }

    public void ModelUpdate(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = mDB.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = mDB.findKey(modelNode,ModelName,ModelKey);

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

        mDB.UpdateModel(modelNode, myObj);

    }

    public void ModelDelete(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = mDB.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = mDB.findKey(modelNode,ModelName,ModelKey);
        modelNode.DeleteModel(myObj);
        mDB.UpdateModel(modelNode,myObj);
    }






}
