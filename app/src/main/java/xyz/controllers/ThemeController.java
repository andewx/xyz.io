package xyz.controllers;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelObject;
import xyz.model.Site;
import xyz.model.Theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThemeController extends BaseController{

    public ThemeController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    public void Get(Context ctx){
        ArrayList<ModelObject> themeList = new ArrayList<>();
        JSONObject children;
        try{
            DBNode themesNode = mDB.GetNode("Themes");
            ModelObject root = themesNode.GetRoot();
            children = root.getChildren("Themes");
            for (String key : children.keySet()) {
                JSONObject jObj = children.getJSONObject(key);
                Site gObj = new Site(jObj);
                themeList.add(gObj);
            }
        }catch(Exception e){
            System.out.println("Could Not Fetch Sites, or none exist");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        ctx.contentType("application/json");
        ctx.result(themeList.toString());
    }

    public void GetID(Context ctx){
        String themeID = ctx.pathParam("id");
        DBNode themeNode = mDB.GetNode("Themes");

        try {
            ModelObject myTheme = mDB.findKey(themeNode, "Theme", themeID);
            ctx.contentType("application/json");
            ctx.result(myTheme.toString());
        }catch(Exception e){
            System.out.println("Could not find theme[ " + themeID + "]");
            ctx.contentType("application/json");
            ctx.result("[]");
        }
    }

    public void CreateTheme(Context ctx){

        String themeName = ctx.formParam("name");
        JSONObject palette = new JSONObject(ctx.formParam("palette"));

        //Create Directories
        String srcPath = "/themes/" + themeName;
        Path url = Path.of(srcPath);
        try {

            if(!Files.exists(Path.of("/themes"))){
                Files.createDirectory(Path.of("themes"));
            }
            String cssPath = srcPath + "/" + "css";
            String jsPath = srcPath + "/" + "js";
            String imgPath = srcPath + "/" + "img";
            String htmlPath = srcPath;
            String htmlFilename = "";
            Files.createDirectory(url);
            Files.createDirectory(Path.of(cssPath));
            Files.createDirectory(Path.of(jsPath));
            Files.createDirectory(Path.of(imgPath));
            Files.createDirectory(Path.of(htmlPath));

            for(UploadedFile uploadedFile : ctx.uploadedFiles("files")){
                if(uploadedFile.getFilename().endsWith(".html")){
                    htmlFilename = uploadedFile.getFilename();
                    FileUtil.streamToFile(uploadedFile.getContent(), htmlPath + "/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".js")){
                    FileUtil.streamToFile(uploadedFile.getContent(), jsPath + "/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".css")){
                    FileUtil.streamToFile(uploadedFile.getContent(), cssPath + "/" + uploadedFile.getFilename());
                }
                else if(uploadedFile.getFilename().endsWith(".png") || uploadedFile.getFilename().endsWith(".jpg") ){
                    FileUtil.streamToFile(uploadedFile.getContent(), imgPath + "/" + uploadedFile.getFilename());
                }
            }

            ModelObject myTheme = new Theme(themeName, htmlFilename, palette.toString());
            DBNode themeNode = mDB.GetNode("Themes");
            themeNode.AddModel(myTheme);
            mDB.RunSync();


        }catch(Exception e){
            System.out.println("Error creating directory for theme with path: " + srcPath);
            System.out.println("Path may already exist");
            System.out.println("Or uploaded filename was problematic do not use special characters");
            System.out.println("Reporting Exception Log");
            System.out.println(e.toString());
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }



    }

    public void GetAllFiles(Context ctx){

        try {
            String themeName = ctx.formParam("name");
            DBNode themeNode = mDB.GetNode("Themes");
            Theme myTheme = new Theme(mDB.findKey(themeNode, "Theme", themeName));

            //List Directories
            String srcPath = "/themes/" + themeName;
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

            try{
                for(String dir : paths){
                    Set<Path> files = listFiles(dir);
                    for(Path f : files){
                        String fName = f.getFileName().toString();
                        if(dir.endsWith("css")){
                            cssRoot.put(fName, f.toString());
                        }
                        else if(dir.endsWith("js")){
                            jsRoot.put(fName,f.toString());
                        }
                        else if(dir.endsWith(themeName)){
                            htmlRoot.put(fName,f.toString());
                        }
                        else if(dir.endsWith("img")){
                            imgRoot.put(fName,f.toString());
                        }
                    }
                }

            ctx.contentType("application/json");
            ctx.result(fileStructure.toString());
            return;

            }catch(Exception e){
                System.out.println("Error retrieving directories");
                ctx.contentType("application/json");
                ctx.result("[]");
                return;
            }


        }catch(Exception e){
            System.out.println("Could not retrieve theme");
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

    public void AddFiles(Context ctx){
        try {
            String themeName = ctx.formParam("name");
            DBNode themeNode = mDB.GetNode("Themes");
            Theme myTheme = new Theme(mDB.findKey(themeNode, "Theme", themeName));
            String rootDir = "/themes/" + themeName;

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

}
