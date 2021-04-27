package xyz.controllers;

import io.javalin.http.Context;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            ctx.cookieStore("MESSAGE", "ADDED");
            ctx.result("Group Added");
        }
        if(name.equals("User")){
            String email = ctx.formParam("Email");
            String pass = ctx.formParam("Password");
            String usern = ctx.formParam("Name");
            String first = ctx.formParam("FirstName");
            String last = ctx.formParam("LastName");
            User newUser = new User(email,pass,usern,first,last);
            DBNode users = mDB.GetNode("Users");
            mDB.AddModel(users, newUser);
            ctx.cookieStore("MESSAGE", "ADDED");
            ctx.result("User Added");
        }
        if(name.equals("Site")){
            try {
                String desc = ctx.formParam("Description");
                String rest = ctx.formParam("RestURL");
                String mName = ctx.formParam("Name");
                String title = ctx.formParam("Title");
                Site newSite = new Site(mName, desc, rest, title);
                DBNode sites = mDB.GetNode("Sites");
                mDB.AddModel(sites, newSite);

                String srcURL = "/sites/" + mName;

                if (!Files.exists(Path.of("/sites"))) {
                    Files.createDirectory(Path.of("/sites"));
                }

                if (!Files.exists(Path.of(srcURL))) {
                    Files.createDirectory(Path.of(srcURL));
                    Files.createDirectory(Path.of(srcURL + "/js"));
                    Files.createDirectory(Path.of(srcURL + "/css"));
                    Files.createDirectory(Path.of(srcURL + "/img"));
                }
                ctx.result("Site Added");
            }catch(Exception e){
                System.out.println("Error creating site ");
                ctx.result("Bad Request");
            }
        }
        if(name.equals("Page")){
            try {
                String siteID = ctx.formParam("SiteID");
                String mName = ctx.formParam("Name");
                String mHtmlFile = ctx.formParam("Filename");
                Page newPage = new Page(mName, siteID,mHtmlFile);
                DBNode pageNode = mDB.GetNode("Pages");
                DBNode siteNode = mDB.GetNode("Sites");
                DBNode themeNode = mDB.GetNode("Themes");
                Site mySite = new Site(mDB.findKey(siteNode, "Site", siteID));
                Theme myTheme = new Theme(mDB.findKey(themeNode, "Theme", mySite.getThemeID()));
                String srcURL = "/themes/" + myTheme.getName() + "/" + myTheme.getHtmlFile();
                String pageURL = "/sites" + mySite.getName() + "/" + mName + ".html";
                String contents = Files.readString(Path.of(srcURL));
                if(!Files.exists(Path.of(pageURL))){
                    Files.createFile(Path.of(pageURL));
                    Files.writeString(Path.of(pageURL), contents);
                }
                mDB.AddModel(pageNode, newPage);
                ctx.result("Site Added");
            }catch(Exception e){
                System.out.println("Error creating site, site may not exist");
                System.out.println(e.toString());
                ctx.result("Bad Request");
            }
        }
        if(name.equals("Theme")){
            System.out.println("Invalid Endpoint for theme creation");
            ctx.result("BAD REQUEST");
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

    public void ModelUpdate(Context ctx) throws IOException {
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = mDB.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = mDB.findKey(modelNode,ModelName,ModelKey);

        if(ModelName.equals("Group")){
            String grpID = ctx.formParam("GroupID");
            String descr = ctx.formParam("AccessDescription");
            String access = ctx.formParam("AccessLevel");
            int acc = Integer.valueOf(access).intValue();

            myObj.updateKey("GroupID", grpID);
            myObj.updateKey("AccessDescription", descr);
            myObj.updateKey("AccessLevel", access);
            myObj.updateKey("Name", grpID);
            myObj.updateKey("UID", grpID);

            modelNode.UpdateModel(myObj);
            mDB.RunSync();

            ctx.result("Group Edited");
        }
        if(ModelName.equals("User")){
            String email = ctx.formParam("Email");
            String usern = ctx.formParam("Name");
            String first = ctx.formParam("FirstName");
            String last = ctx.formParam("LastName");
            String gid = ctx.formParam("GroupID");

            myObj.updateKey("Email", email);
            myObj.updateKey("UID", email);
            myObj.updateKey("Name", usern);
            myObj.updateKey("FirstName", first);
            myObj.updateKey("LastName", last);
            myObj.updateKey("GroupID", gid);

            try {
                modelNode.UpdateModel(myObj);
                mDB.RunSync();
            }catch(Exception e){
                System.out.println("Error saving newly created user");
                ctx.result("Error: Not stored");
            }
            System.out.println("User added!");
            ctx.result("User Edited");
        }
        if(ModelName.equals("Site")){
            try {
                String desc = ctx.formParam("Description");
                String rest = ctx.formParam("RestURL");
                String mName = ctx.formParam("Name");
                String title = ctx.formParam("Title");
                Site newSite = (Site) myObj;
                newSite.setDescription(desc);
                newSite.setRestURL(rest);
                newSite.setTitle(title);
                newSite.setName(mName);
                ctx.result("Site Edited");
            }catch(Exception e){
                System.out.println("Error creating site directories");
                ctx.contentType("text");
                ctx.result("BAD REQUEST");
                return;
            }
        }
        if(ModelName.equals("Theme")){
            String mName = ctx.formParam("Name");
            String palette = ctx.formParam("Palette");
            Theme newTheme = (Theme)myObj;
            newTheme.setName(mName);
            newTheme.setPalette(palette);
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


    }

    public void ModelDelete(Context ctx){
        String ModelKey = ctx.pathParam("id");
        String ModelName = ctx.pathParam("name");
        DBNode modelNode = mDB.GetNode(ModelKeys.pluralize(ModelName));
        ModelObject myObj = mDB.findKey(modelNode,ModelName,ModelKey);
        modelNode.DeleteModel(myObj);
        mDB.UpdateModel(modelNode,myObj);
        mDB.RunSync();
        ctx.result("Model Deleted");
    }






}
