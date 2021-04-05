package xyz.controllers;

import io.javalin.http.Context;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelObject;
import xyz.model.Page;
import xyz.model.Site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SiteController extends BaseController{

    protected String Root;

    public SiteController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
        Root = "resources/web/sites";
    }

    public void CreateSite(Context ctx){
        try{
            String name = ctx.formParam("Name");
            String title = ctx.formParam("Title");
            String desc = ctx.formParam("Description");
            String url = ctx.formParam("RestURL");
            String site = ctx.formParam("site");

            Site mySite = new Site(name,desc, url, title, site);
            DBNode siteNode = mDB.GetNode("Sites");
            DBNode pageNode = mDB.GetNode("Pages");

            try {
                //Create Directory Models
                if (!Files.exists(Path.of(Root).toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root));
                }
                if (!Files.exists(Path.of(Root+"/"+name).toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root+"/"+name));
                }
                if (!Files.exists(Path.of(Root+"/"+name+"/img").toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root+"/"+name+"/img"));
                }
                if (!Files.exists(Path.of(Root+"/"+name+"/css").toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root+"/"+name+"/css"));
                }
                if (!Files.exists(Path.of(Root+"/"+name+"/js").toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root+"/"+name+"/js"));
                }

                //Request path to site index file - Copy site Index contents to /sites/index.html
                Files.copy(Path.of("resources/web/sites/"+site+"/index.html"), Path.of(Root+"/"+name+"/index.html"));
                Page myPage = new Page("Main", name, "index.html");
                mySite.addModel(myPage);
                siteNode.AddModel(mySite);
                mDB.RunSync();

            }catch(Exception e) {
                System.out.println("Error creating site contents");
                ctx.contentType("application/json");
                ctx.result("{\"message\": \"Error creating site contents\"");
                return;
            }

            siteNode.AddModel(mySite);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"message\": \"success\"}");

        }catch(Exception e){
            System.out.println("Error creating site");
            ctx.contentType("application/json");
            ctx.result("{\"message\": \"Error creating site\"");
        }
    }

    public void GetID(Context ctx){
        String siteID = ctx.pathParam("name");
        DBNode siteNode = mDB.GetNode("Sites");
        ctx.contentType("application/json");
        try{
            Site site = new Site(mDB.findKey(siteNode, "Site", siteID));
            String dirContents = GetSiteFiles(siteID);
            site.put("Dir", dirContents);
            ctx.result(site.toString());
        }catch(Exception e){
            System.out.println("Could Not Fetch sites, or none exist");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
    }



    public void Get(Context ctx){
        ArrayList<ModelObject> siteList = new ArrayList<>();
        JSONObject children;
        try{
            DBNode sitesNode = mDB.GetNode("Sites");
            ModelObject root = sitesNode.GetRoot();
            children = root.getChildren("Sites");
            for (String key : children.keySet()) {
                JSONObject jObj = children.getJSONObject(key);
                Site gObj = new Site(jObj);
                siteList.add(gObj);
            }
        }catch(Exception e){
            System.out.println("Could Not Fetch Sites, or none exist");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        ctx.contentType("application/json");
        ctx.result(siteList.toString());
    }
    
    public void GetKeys(Context ctx){
        Set<String> siteList = new HashSet<>();
        JSONObject children;
        try{
            DBNode sitesNode = mDB.GetNode("Sites");
            ModelObject root = sitesNode.GetRoot();
            children = root.getChildren("Sites");
            siteList = children.keySet();
            String retJson = "[";
            int index = 0;
            for(String key : siteList) {
                if (index < siteList.size() - 1) {
                    retJson = retJson.concat("\""+key + "\",");
                } else {
                    retJson = retJson.concat("\"" +key + "\"");
                }
                index++;
            }
            retJson = retJson.concat("]");
            ctx.result(retJson);
            return;
        }catch(Exception e){
            System.out.println("Could Not Fetch sites");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        
    }

    public String GetSiteFiles(String SiteId) {
        try {
            //List Directories
            String srcPath = Root + "/" + SiteId;


            Path url = Path.of(srcPath);
            String cssPath = srcPath + "/css";
            String jsPath = srcPath + "/js";
            String imgPath = srcPath + "/img";

            JSONObject fileStructure = new JSONObject();
            JSONObject srcRoot = new JSONObject();
            fileStructure.put(srcPath, srcRoot);
            JSONObject cssRoot = new JSONObject();
            fileStructure.put(cssPath, cssRoot);
            JSONObject jsRoot = new JSONObject();
            fileStructure.put(jsPath, jsRoot);
            JSONObject imgRoot = new JSONObject();
            fileStructure.put(imgPath, imgRoot);

            ArrayList<String> paths = new ArrayList<>();
            paths.add(srcPath);
            paths.add(cssPath);
            paths.add(jsPath);
            paths.add(imgPath);


            try {
                for (String dir : paths) {
                    try {
                        Set<Path> files = listFiles(Path.of(dir).toAbsolutePath().toString());
                        for (Path f : files) {
                            String fName = f.getFileName().toString();
                            if (dir.endsWith("css")) {
                                cssRoot.put("file", fName);
                                cssRoot.put(fName, f.toString());
                            } else if (dir.endsWith("js")) {
                                jsRoot.put(fName, f.toString());
                            } else if (dir.endsWith(SiteId)) {
                                srcRoot.put(fName, f.toString());
                            } else if (dir.endsWith("img")) {
                                imgRoot.put(fName, f.toString());
                            }
                        }
                    }catch(Exception e){
                        System.out.println("Error Opening Directory");
                    }
                }
                return fileStructure.toString();
            }
            catch (Exception e) {
                System.out.println("Error retrieving directories");
                return "[]";
            }
        }
        catch (JSONException e) {
            System.out.println("Error retrivieing directories, JSON Error");
            System.out.println(e.getCause());
            return "[]";
        }
    }


    public void GetPages(Context ctx){
        try {
            DBNode siteNode = mDB.GetNode("Sites");
            DBNode pageNode = mDB.GetNode("Pages");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", ctx.formParam("name")));
            HashMap<String,String> props = new HashMap<>();
            props.put("SiteID", mySite.getName());
            ArrayList<ModelObject> pageList = mDB.findExact(pageNode, "Page", props);
            String result = "[";
            int index = 0;
            for(ModelObject page : pageList){
                result = result.concat(page.toString());
                if(index < pageList.size() - 1){
                    result.concat(",");
                }
            }
            result.concat("]");
            ctx.contentType("application/json");
            ctx.result(result);
            return;
        }catch(Exception e){
            System.out.println("Could not retrieve site or pages");
            System.out.println(e.toString());
        }

    }

    public void ListDirectoryContents(Context ctx) {
        try {
            String siteName = ctx.formParam("name");
            DBNode siteNode = mDB.GetNode("Sites");
            Site mysite = new Site(mDB.findKey(siteNode, "Site", siteName));

            //List Directories
            String srcPath = "/sites/" + siteName;
            JSONObject fileStructure = new JSONObject();
            fileStructure.put("SRC", srcPath);
            JSONObject htmlRoot = fileStructure.put("HTML", new JSONObject());
            JSONObject cssRoot = fileStructure.put("CSS", new JSONObject());
            JSONObject jsRoot = fileStructure.put("JS", new JSONObject());
            JSONObject imgRoot = fileStructure.put("IMG", new JSONObject());


            Path url = Path.of(srcPath);
            String cssPath = srcPath + "/css";
            String jsPath = srcPath + "/js";
            String imgPath = srcPath + "/img";

            ArrayList<String> paths = new ArrayList<>();
            paths.add(cssPath);
            paths.add(jsPath);
            paths.add(imgPath);
            paths.add(srcPath);

            try {
                for (String dir : paths) {
                    Set<Path> files = listFiles(dir);
                    for (Path f : files) {
                        String fName = f.getFileName().toString();
                        if (dir.endsWith("css")) {
                            cssRoot.put(fName, f.toString());
                        } else if (dir.endsWith("js")) {
                            jsRoot.put(fName, f.toString());
                        } else if (dir.endsWith(siteName)) {
                            htmlRoot.put(fName, f.toString());
                        } else if (dir.endsWith("img")) {
                            imgRoot.put(fName, f.toString());
                        }
                    }
                }

                ctx.contentType("application/json");
                ctx.result(fileStructure.toString());
                return;

            } catch (Exception e) {
                System.out.println("Error listing site directories");
                System.out.println(e.toString());
            }
        } catch (Exception e) {
            System.out.println("Error retrieving site components");
            System.out.println(e.toString());
        }
    }

    Set<Path> listFiles(String dir) throws IOException {
        Stream<Path> list = Files.list(Path.of(dir));
        return list.filter(path -> {
            if (!Files.isDirectory(path)) {
                return true;
            }
            return false;
        }).collect(Collectors.toSet());
    }

}
