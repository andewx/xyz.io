package xyz.controllers;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.json.JSONArray;
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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Themes controller class manages themes in its resources/web/themes filesystem. Themes are stored in the
 * resources/web/themes/themeName directory and may have their own associated resources which would be referenced
 * in their HTML files themes/themeName/file.myFile or http://localhost:8080/themes/themeName/file.myFile.
 * When creating themes a Palette JSON object is store with the theme and saved as a palette.css file in
 * themes/themeName/css folder as (--pal1, --pal2, --pal3, --pal4, --pal5). Use the CSS syntax color: var(--pal1); to reference
 * these palette theme values from your site.
 */
public class ThemeController extends BaseController{

    /**
     * The Root.
     */
    protected String Root;

    /**
     * Constructor
     *
     * @param db_instance Database DBMain
     * @param app         AppManager
     */
    public ThemeController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
        Root = "resources/web/themes";
    }

    /**
     * Displays Theme
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
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

    /**
     * Gets theme keys JSON return
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
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

    /**
     * Gets all themes JSON returns object
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void GetAll(Context ctx){
        ArrayList<ModelObject> themeList = new ArrayList<>();
        JSONObject children;
        try{
            DBNode themesNode = mDB.GetNode("Themes");
            ModelObject root = themesNode.GetRoot();
            children = root.getChildren("Themes");
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

    /**
     * Gets Theme By ID
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
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

    /**
     * Creates theme
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void CreateTheme(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String palString = ctx.formParam("palette");

            assert palString != null;
            assert themeName != null;

            //Create Directories
            String srcPath = Root+ "/" + themeName;
            Path url = Path.of(srcPath);
            try {

                if (!Files.exists(Path.of(Root).toAbsolutePath())) {
                    Files.createDirectory(Path.of(Root));
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
                            FileUtil.streamToFile(uploadedFile.getContent(), srcPath + "/index.html");
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

                String CSSPAL = PaletteToCSS(palString);
                assert CSSPAL != null;
                Files.writeString(Path.of(srcPath+"/css/palette.css"), CSSPAL);

                ModelObject myTheme = new Theme(themeName, "index.html", palString);
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

    /**
     * Converts a Palette JSON String into CSS and writes file to palette.css
     * Requirement(App 1.1)
     * @param palette JSON generated palette array, [[0,0,0]...] Five palettes expected
     * @return string
     */
    public String PaletteToCSS(String palette){
        try {
            JSONArray palObj = new JSONArray(palette);
            String cssString = "root:{\n";

            for (int i = 0; i < palObj.length(); i++) {
                String internPal = "--pal" + Integer.toString(i) + ": rgb(";
                JSONArray internArray = palObj.getJSONArray(i);
                internPal = internPal.concat(internArray.getInt(0) + ",");
                internPal = internPal.concat(internArray.getInt(1)+",");
                internPal = internPal.concat(internArray.getInt(2)+"");
                internPal = internPal.concat(");\n");
                cssString = cssString.concat(internPal);
            }
            cssString = cssString.concat("}");
            return cssString;

        }catch(Exception e){
            System.out.println("Could not parse JSONArray for Palette");
            return null;
        }
    }

    /**
     * Gets theme files
     * Requirement(App 1.1)
     * @param themeId the theme id
     * @return string
     */
    public String GetThemeFiles(String themeId) {
        try {
            //List Directories
            String srcPath = Root + "/" + themeId;


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

    /**
     * Gets Files
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
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

    /**
     * Lists files
     * Requirement(App 1.1)
     * @param dir Directory
     * @return File Set
     * @throws IOException BOOOOOOOO
     */
    Set<Path> listFiles(String dir) throws IOException {
        Stream<Path> list = Files.list(Path.of(dir));
        return list.filter(path -> {
            if (!Files.isDirectory(path)) {
                return true;
            }
            return false;
            }).collect(Collectors.toSet());
    }

    /**
     * Lists filecontents
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
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
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error getting contents\"}");
            return;
        }
    }

    /**
     * Edits file contents
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void EditFileContents(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            String content = ctx.formParam("content");
            assert content != null;
            Files.writeString(Path.of(filePath), content);
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"file written\"}");
            return;
        }catch(Exception e){
            System.out.println("File / Theme Could Not Be Found");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error submitting\"}");
            return;
        }
    }

    /**
     * Deletes Theme File
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void DeleteFile(Context ctx){
        try {
            String themeName = ctx.pathParam("name");
            JSONObject postMap = new JSONObject(ctx.body());
            String filePath = postMap.getString("file");
            Files.delete(Path.of(filePath));
            ctx.contentType("application/json");
            ctx.result("{ \"message\":\"deleted\"}");
            return;
        }catch(Exception e){
            System.out.println("File not found");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error deleting file\"}");
            return;
        }
    }

    /**
     * Adds file to theme
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void AddFiles(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            DBNode themeNode = mDB.GetNode("Themes");
            Theme myTheme = new Theme(mDB.findKey(themeNode, "Theme", themeName));
            String rootDir = Root +"/" + themeName;
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
                    myTheme.setHtmlFile(uploadedFile.getFilename());
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/" + uploadedFile.getFilename());
                }
            }
            themeNode.UpdateModel(myTheme);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"success\"}");
            return;

        }catch(Exception e){
            System.out.println("Error adding files to theme");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error on update\"}");
            return;
        }

    }

    /**
     * Deletes a theme
     * Requirement(App 1.1)
     * @param ctx the ctx
     */
    public void DeleteTheme(Context ctx){
        try {
            String themeName = ctx.pathParam("name");
            DBNode themeNode = mDB.GetNode("Themes");

            //List Directories
            String srcPath = Root +"/" + themeName;
            DeleteVisitor fVisitor = new DeleteVisitor();
            Files.walkFileTree(Path.of(srcPath), fVisitor);
            themeNode.DeleteModel("Theme", themeName);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"deleted theme\"}");

        }catch(Exception e){
            System.out.println("Error deleting theme");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error deleting theme\"}");
            return;
        }

    }

    /**
     * FILE UTILITY
     */
    protected class DeleteVisitor extends SimpleFileVisitor<Path> {
        /**
         * FILE VISITOR Visits file directory
         * @param file Filepath
         * @param attrs File attrbs
         * @return FileVisit Result
         * @throws IOException
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * File Visit Post action
         * @param dir Direectory
         * @param exc Exception
         * @return File Visit Result
         * @throws IOException Exceptional
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

}
