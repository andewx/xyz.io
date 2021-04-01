package xyz.controllers;

import io.javalin.http.Context;
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
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SiteController extends BaseController{

    public SiteController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
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
            Theme myTheme = new Theme(mDB.findKey(siteNode, "Site", siteName));

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
