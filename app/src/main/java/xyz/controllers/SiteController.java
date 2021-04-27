package xyz.controllers;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelObject;
import xyz.model.Page;
import xyz.model.Site;
import xyz.model.Theme;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
            String theme = ctx.formParam("Theme");

            Site mySite = new Site(name,desc, url, title, theme);
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

                //Request path to site index file - Copy site Index contents to /sites/index.htmls
                Files.copy(Path.of("resources/web/themes/"+mySite.getThemeID()+"/index.html"), Path.of(Root+"/"+name+"/index.html"));
                Page myPage = new Page("Main", name, "index.html");
                mySite.addModel(myPage);
                siteNode.AddModel(mySite);
                mDB.RunSync();

            }catch(Exception e) {
                System.out.println("Error creating site contents");
                ctx.contentType("application/json");
                ctx.result("{\"message\": \"Error creating site contents\"}");
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

    public void AddFiles(Context ctx){
        try {
            String siteName = ctx.pathParam("name");
            DBNode siteNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", siteName));
            String rootDir = Root +"/" + siteName;
            if(!Files.exists(Path.of(rootDir))){
                Files.createDirectory(Path.of(rootDir));
            }
            if(!Files.exists(Path.of(rootDir+"/img"))){
                Files.createDirectory(Path.of(rootDir+"/img"));
            }
            if(!Files.exists(Path.of(rootDir+"/js"))){
                Files.createDirectory(Path.of(rootDir+"/js"));
            }
            if(!Files.exists(Path.of(rootDir+"/css"))){
                Files.createDirectory(Path.of(rootDir+"/css"));
            }

            for(UploadedFile uploadedFile : ctx.uploadedFiles("files")){
                if(uploadedFile.getFilename().endsWith(".png") || uploadedFile.getFilename().endsWith(".jpg") ){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/img/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".js")){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/js/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".css")){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/css/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".html")){
                   // mySite.setHtmlFile(uploadedFile.getFilename()); -- Site Model Adds Its Own Pages
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/" + uploadedFile.getFilename());
                }
            }
            siteNode.UpdateModel(mySite);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"success\"}");
            return;

        }catch(Exception e){
            System.out.println("Error adding files to site");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error on update\"}");
            return;
        }

    }

    public void DeleteFile(Context ctx){ //Shouldn't be able to delete HTML objects
        try {
            String siteName = ctx.pathParam("name");
            JSONObject postMap = new JSONObject(ctx.body());
            String filePath = postMap.getString("file");
            if(!filePath.endsWith(".html")){
                Files.delete(Path.of(filePath));
                ctx.contentType("application/json");
                ctx.result("{ \"message\":\"deleted\"}");
                return;
            }
            ctx.contentType("application/json");
            ctx.result("{ \"message\":\"file is HTML and associated with a page so it could not be deleted\"}");
            return;
        }catch(Exception e){
            System.out.println("File not found");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error deleting file\"}");
            return;
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

    public void DeleteSite(Context ctx){
        try {
            String siteName = ctx.pathParam("name");
            DBNode siteNode = mDB.GetNode("Sites");

            //List Directories
            String srcPath = Root +"/" + siteName;
            SiteController.DeleteVisitor fVisitor = new SiteController.DeleteVisitor();
            Files.walkFileTree(Path.of(srcPath), fVisitor);
            siteNode.DeleteModel("site", siteName);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"deleted site\"}");

        }catch(Exception e){
            System.out.println("Error deleting site");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error deleting site\"}");
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

    private class DeleteVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

}
