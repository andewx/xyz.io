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
import xyz.model.Theme;
import xyz.model.User;
import xyz.webkit.SiteTemplate;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThemeController extends BaseController{

    public ThemeController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    public void Display(Context ctx){
        User thisUser = UserFromSession(ctx);
        if(thisUser == null){
            ctx.redirect("/");
            return;
        }
        SiteTemplate templatizer = new SiteTemplate();
        SiteTemplate dashView = new SiteTemplate();
        dashView.GetTemplate("views/themes-display.html");
        StringBuilder htmlResponse = new StringBuilder();
        String ini = thisUser.getFirstName().substring(0,1) + thisUser.getLastName().substring(0,1);
        templatizer.GetTemplate("templates/dashboard.html");
        templatizer.AddKey("dashcontent",dashView.GetHtml());
        templatizer.AddKey("title", "Hi, " + thisUser.getFirstName());
        templatizer.AddKey("dashTitle", "Hello, " + thisUser.getFirstName());
        templatizer.AddKey("Init", ini);
        templatizer.AddKey("dashSubtitle", "Themes Dashboard");
        templatizer.AddKey("UserFirstLast", thisUser.getFirstName() + " " + thisUser.getLastName());
        templatizer.AddKey("UserEmail", thisUser.getEmail());
        templatizer.ReplaceKeys();
        htmlResponse.append(templatizer.GetHtml());

        ctx.contentType("html");
        ctx.result(htmlResponse.toString());
    }


    public void GetKeys(Context ctx){
        Set<String> themeList = new HashSet<>();
        JSONObject children;
        try{
            DBNode themesNode = mDB.GetNode("Themes");
            ModelObject root = themesNode.GetRoot();
            children = root.getChildren("Themes");
            themeList = children.keySet();
            String retJson = "[";
            int index = 0;
            for(String key : themeList) {
                if (index < themeList.size() - 1) {
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
            System.out.println("Could Not Fetch themes");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
    }


    public void GetAll(Context ctx){
        ArrayList<ModelObject> themeList = new ArrayList<>();
        JSONObject children;
        try{
            DBNode themesNode = mDB.GetNode("Themes");
            ModelObject root = themesNode.GetRoot();
            children = root.getChildren("Themes");
            String retJson = "[";
            int index = 0;
            for(String key : children.keySet()) {
               JSONObject jObj = children.getJSONObject(key);
               Theme gObj = new Theme(jObj);
               themeList.add(gObj);
            }

            ctx.result(themeList.toString());
            return;
        }catch(Exception e){
            System.out.println("Could Not Fetch themes");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
    }

    public void GetID(Context ctx){
        String themeID = ctx.pathParam("name");
        DBNode themeNode = mDB.GetNode("Themes");
        ctx.contentType("application/json");
        try{
            DBNode themesNode = mDB.GetNode("Themes");
            Theme theme = new Theme(mDB.findKey(themeNode, "Theme", themeID));
            String dirContents = GetThemeFiles(themeID);
            theme.put("Dir", dirContents);
            ctx.result(theme.toString());
        }catch(Exception e){
            System.out.println("Could Not Fetch themes, or none exist");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
    }

    public void CreateTheme(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String palString = ctx.formParam("palette");

            assert palString != null;
            assert themeName != null;

            //Create Directories
            String srcPath = "themes/" + themeName;
            Path url = Path.of(srcPath);
            try {

                if (!Files.exists(Path.of("themes").toAbsolutePath())) {
                    Files.createDirectory(Path.of("themes"));
                }

                if (!Files.exists(Path.of(srcPath).toAbsolutePath())) {
                    Files.createDirectory(Path.of(srcPath));
                }
                String cssPath = srcPath + "/" + "css";
                String jsPath = srcPath + "/" + "js";
                String imgPath = srcPath + "/" + "img";

                String htmlFilename = "";
                if(!Files.exists(url.toAbsolutePath())){
                    Files.createDirectory(url);
                }
                if(!Files.exists(Path.of(cssPath).toAbsolutePath())){
                    Files.createDirectory(Path.of(cssPath));
                }
                if(!Files.exists(Path.of(jsPath).toAbsolutePath())){
                    Files.createDirectory(Path.of(jsPath));
                }
                if(!Files.exists(Path.of(imgPath).toAbsolutePath())){
                    Files.createDirectory(Path.of(imgPath));
                }

                for (UploadedFile uploadedFile : ctx.uploadedFiles("files")) {
                    if (uploadedFile.getFilename().endsWith(".html")) {
                        htmlFilename = uploadedFile.getFilename();
                        if(!Files.exists(Path.of(srcPath + "/" + htmlFilename).toAbsolutePath())) {
                            FileUtil.streamToFile(uploadedFile.getContent(), srcPath + "/" + uploadedFile.getFilename());
                        }
                    } else if (uploadedFile.getFilename().endsWith(".js")) {
                        if(!Files.exists(Path.of(jsPath + "/" + uploadedFile.getFilename()).toAbsolutePath())) {
                            FileUtil.streamToFile(uploadedFile.getContent(), jsPath + "/" + uploadedFile.getFilename());
                        }
                    } else if (uploadedFile.getFilename().endsWith(".css")) {
                        if(!Files.exists(Path.of(cssPath + "/" + uploadedFile.getFilename()).toAbsolutePath())) {
                            FileUtil.streamToFile(uploadedFile.getContent(), cssPath + "/" + uploadedFile.getFilename());
                        }
                    } else if (uploadedFile.getFilename().endsWith(".png") || uploadedFile.getFilename().endsWith(".jpg")) {
                        if(!Files.exists(Path.of(imgPath + "/" + uploadedFile.getFilename()).toAbsolutePath())) {
                            FileUtil.streamToFile(uploadedFile.getContent(), imgPath + "/" + uploadedFile.getFilename());
                        }
                    }
                }

                ModelObject myTheme = new Theme(themeName, htmlFilename, palString);
                DBNode themeNode = mDB.GetNode("Themes");
                themeNode.AddModel(myTheme);
                mDB.RunSync();
                String guid = ctx.cookie("GUID");
                mApp.SetSessionMessage("Theme " + themeName + ", successfully created! You can add more files to your theme by visiting the theme dash", guid);
                ctx.contentType("application/json");
                ctx.result("{\"message\" : \"added theme\", \"redirect\": \"/compl\"}");
                return;

            } catch (Exception e) {
                System.out.println("Error creating directory for theme with path");
                System.out.println(e.getCause());
                ctx.contentType("application/json");
                ctx.result("{\"message\" : \"error creating theme\", \"redirect\": \"/err\"}");
                return;
            }
        }catch(Exception e){
            System.out.println("Error creating theme");
            System.out.println("malformed request");
            System.out.println("Reporting Exception Log");
            System.out.println(e.toString());
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }



    }

    public String GetThemeFiles(String themeId) {
        try {
            //List Directories
            String srcPath = "themes/" + themeId;


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
                            } else if (dir.endsWith(themeId)) {
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

    public void GetFiles(Context ctx){

        try {
            String contents = GetThemeFiles(ctx.pathParam("name"));
            ctx.contentType("application/json");
            ctx.result(contents);
            return;

            }catch(Exception e){
                System.out.println("Error retrieving directories");
                ctx.contentType("application/json");
                ctx.result("[]");
                return;
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

    public void FileContents(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            String content = Files.readString(Path.of(filePath));
            ctx.contentType("text");
            ctx.result(content);
            return;
        }catch(Exception e){
            System.out.println("File / Theme Could Not Be Found");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

    public void EditFileContents(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            String content = ctx.formParam("content");
            assert content != null;
            Files.writeString(Path.of(filePath), content);
            ctx.contentType("text");
            ctx.result("File written");
            return;
        }catch(Exception e){
            System.out.println("File / Theme Could Not Be Found");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

    public void DeleteFile(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            Files.delete(Path.of(filePath));
            ctx.contentType("text");
            ctx.result("File written");
            return;
        }catch(Exception e){
            System.out.println("File not found");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

    public void AddFiles(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            DBNode themeNode = mDB.GetNode("Themes");
            Theme myTheme = new Theme(mDB.findKey(themeNode, "Theme", themeName));
            String rootDir = "themes/" + themeName;

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
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/" + uploadedFile.getFilename());
                }
            }
            ctx.contentType("text");
            ctx.result("success");
            return;

        }catch(Exception e){
            System.out.println("Error adding files to theme");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }

    }

    public void DeleteTheme(Context ctx){
        try {
            String themeName = ctx.pathParam("name");
            DBNode themeNode = mDB.GetNode("Themes");

            //List Directories
            String srcPath = "themes/" + themeName;
            DeleteVisitor fVisitor = new DeleteVisitor();
            Files.walkFileTree(Path.of(srcPath), fVisitor);
            themeNode.DeleteModel("Theme", themeName);
            mDB.RunSync();

        }catch(Exception e){
            System.out.println("Error deleting theme");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }

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
