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
import xyz.webkit.SiteTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Site Controller Manages Sites and their associated pages and stores in the framework filesystem.
 * resources/web/sites/ is where sites are stored and where file manipulation occurs. Sites are associated with
 * templates by their ThemeID and Pages with their Sites by SiteID. However the Model Site stores pages internal to
 * its own JSONObject which means that Pages are generally not visible to the DBKIT query interface. Additionally
 * when using template files you should provide a standard list of @:SomeKey values in the Template for the Editor
 * and Page to Inject. These keys are (@:EditorCSS, @:EditorPostCSS, @:EditorBody, @:PageCSS, @:PagePreJS, @:PagePostJS, @:PageBody, Title)
 * Uploaded Images to the Page sites are downloaded to the resources/web/images directory, there were route issues which
 * have been solved since, so this may be moved back into the originally intended /sites/mySite/img directories. You
 * may now use this directory when uploading your own image/resource files for your site.
 */
public class SiteController extends BaseController{

    protected String Root;

    /**
     * Constructor for the site controller
     *
     * @param db_instance DB
     * @param app         App manager
     */
    public SiteController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
        Root = "resources/web/sites";
    }

    /**
     * Endpoint for submitting sites for creation
     * @param ctx Javalin.io http server context
     */
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
                Files.writeString(Path.of(Root+"/"+name+"/Index.html"), "");
                Files.writeString(Path.of(Root+"/"+name+"/css/Index.css"), "");
                Page myPage = new Page("Index", name, "Index.html");
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

    /**
     * Endpoint for AddPage requests
     * @param ctx Javalin.io http server context
     */
    public void AddPage(Context ctx){
        try{
            String name = ctx.formParam("page");
            String site = ctx.pathParam("site");

            DBNode siteNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", site));

            try {

                Page myPage = new Page(name, site, name +".html");
                Files.createFile(Path.of(Root+"/"+site+"/"+name+".html"));
                Files.createFile(Path.of(Root+"/"+site+"/css/"+name+".css"));
                mySite.addModel(myPage);
                siteNode.AddModel(mySite);
                mDB.RunSync();
                ctx.contentType("application/json");
                ctx.result("{\"message\": \"added page\"}");

            }catch(Exception e) {
                System.out.println("Error adding page");
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

    /**
     * Gets page JSON Data representation for editor
     *
     * @param ctx Javalin.io http server context
     */
    public void GetPageData(Context ctx){
        try{
            String siteName = ctx.pathParam("site");
            String pageName = ctx.pathParam("page");
            DBNode siteNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", siteName));
            Page myPage = mySite.getPage(pageName);
            String response = myPage.getJsonData();
            ctx.contentType("application/json");
            ctx.result(response);
        }
        catch(Exception e){
            System.out.println("Could not fetch page JSON");
        }
    }

    /**
     * Display Site Editor for Given Page Site
     *
     * @param ctx Javalin.io http server context
     */
    public void EditSite(Context ctx){
        try {
            String siteName = ctx.pathParam("name");
            String pageName = ctx.pathParam("page");
            DBNode siteNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", siteName));
            Page myPage = mySite.getPage(pageName);
            String cssFile = myPage.getCssFile();
            assert myPage != null;
            String rootDir = Root + "/" + siteName;
            String themeFile = "resources/web/themes/" + mySite.getThemeID() + "/index.html";

            SiteTemplate editorHeader = new SiteTemplate();
            SiteTemplate editorBody = new SiteTemplate();
            SiteTemplate editorFooter = new SiteTemplate();
            SiteTemplate themeTemplate = new SiteTemplate();
            themeTemplate.GetTemplate(themeFile);
            //Editor Inject
            editorHeader.GetTemplate("templates/editor-header.html"); //Editor CSS
            editorBody.GetTemplate("templates/editor-toolbar.html"); // Editor Body
            editorFooter.GetTemplate("templates/editor-footer.html"); //JS Scripts
            editorBody.AddKey("SiteName", mySite.getUID());
            editorBody.AddKey("SitePage", myPage.getUID());
            editorBody.ReplaceKeys();
            themeTemplate.AddKey("Title", mySite.getName());
            themeTemplate.AddKey("EditorCSS", editorHeader.GetHtml());
            themeTemplate.AddKey("EditorPostJS", editorFooter.GetHtml());
            themeTemplate.AddKey("EditorBody", editorBody.GetHtml());
            themeTemplate.AddKey("PageCSS", "<link rel=\'stylesheet\' href=\'http://localhost:8080/sites/"+siteName+"/css/"+cssFile+"\'>");
            themeTemplate.AddKey("PagePreJS", "");
            themeTemplate.AddKey("PagePostJS", "");
            themeTemplate.AddKey("PageBody", "");
            themeTemplate.ReplaceKeys();
            ctx.contentType("text/html");
            ctx.result(themeTemplate.GetHtml());

        }catch(Exception e){
            System.out.println("Could not fetch site editor");
        }
    }

    /**
     * Add Files to the site
     *
     * @param ctx Javalin.io http server context
     */
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

            boolean hasFile = false;
            String uploadedURL = "http://localhost:8080/sites/" + siteName + "/";
            for(UploadedFile uploadedFile : ctx.uploadedFiles("files")){
                hasFile = true;
                if(uploadedFile.getFilename().endsWith(".png") || uploadedFile.getFilename().endsWith(".jpg") ){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/img/" + uploadedFile.getFilename());
                    uploadedURL = uploadedURL + "img/" + uploadedFile.getFilename();
                }
                else if(uploadedFile.getFilename().endsWith(".js")){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/js/" + uploadedFile.getFilename());
                    uploadedURL = uploadedURL + "js/" + uploadedFile.getFilename();
                }
                else if(uploadedFile.getFilename().endsWith(".css")){
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/css/" + uploadedFile.getFilename());
                    uploadedURL = uploadedURL + "css/" + uploadedFile.getFilename();
                }
                else if(uploadedFile.getFilename().endsWith(".html")){
                   // mySite.setHtmlFile(uploadedFile.getFilename()); -- Site Model Adds Its Own Pages
                    FileUtil.streamToFile(uploadedFile.getContent(), rootDir + "/" + uploadedFile.getFilename());
                    uploadedURL = uploadedURL + uploadedFile.getFilename();
                }
            }

            if(!hasFile){ //Simple Image Upload
                UploadedFile fileImage = ctx.uploadedFile("image");
                assert fileImage != null;
                FileUtil.streamToFile(fileImage.getContent(), "resources/web/images/" + fileImage.getFilename());
                uploadedURL = "http://localhost:8080/images/" + fileImage.getFilename();
            }

            siteNode.UpdateModel(mySite);
            mDB.RunSync();
            ctx.contentType("application/json");
            ctx.result("{\"success\": 1, \"file\":{\"url\":\""+uploadedURL +"\"}}");

        }catch(Exception e){
            System.out.println("Error adding files to site");
            ctx.contentType("application/json");
            ctx.result("{\"success\": 0}");
        }

    }

    /**
     * Delete Files from Site
     *
     * @param ctx Javalin.io http server context
     */
    public void DeleteFile(Context ctx){ //Shouldn't be able to delete HTML objects
        try {
            String siteName = ctx.pathParam("name");
            JSONObject postMap = new JSONObject(ctx.body());
            String filePath = postMap.getString("file");

            if(filePath.endsWith(".html")){ //Try to remove page from site model if it exists with the same name
                DBNode siteNode = mDB.GetNode("Sites");
                Site mySite = new Site(mDB.findKey(siteNode, "Site", siteName));
                String[] urlSplat = filePath.split("/");
                String suffix = urlSplat[urlSplat.length-1];
                String page = suffix.replace(".html", "");
                mySite.removePage(page);
                siteNode.UpdateModel(mySite);
                mDB.RunSync();
                Files.delete(Path.of(filePath));
            }else{
                Files.delete(Path.of(filePath));
            }
            ctx.contentType("application/json");
            ctx.result("{ \"message\":\"deleted\"}");
            ctx.contentType("application/json");
            ctx.result("{ \"message\":\"file is HTML and associated with a page so it could not be deleted\"}");

        }catch(Exception e){
            System.out.println("File not found");
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"error deleting file\"}");

        }
    }

    /**
     * Endpoint returns Site JSON Object
     * @param ctx Javalin.io http server context
     */
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

    /**
     * Endpoint delete Site by UID
     *
     * @param ctx Javalin.io http server context
     */
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

    /**
     * Endpoint Get sites - returns JSON Array of all Site JSON strings to the client
     * @param ctx Javalin.io http server context
     */
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

    /**
     *
     * Endpoint site pages - returns JSON Array of Page objects to the client
     * @param ctx Javalin.io http server context
     */
    public void GetPages(Context ctx){
        ArrayList<ModelObject> pageList = new ArrayList<>();
        String siteName = ctx.pathParam("name");
        JSONObject children;
        try{
            DBNode sitesNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(sitesNode, "Site", siteName));
            children = mySite.getChildren("Pages");
            for (String key : children.keySet()) {
                JSONObject jObj = children.getJSONObject(key);
                Page gObj = new Page(jObj);
                pageList.add(gObj);
            }
        }catch(Exception e){
            System.out.println("Could not find site pages");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        ctx.contentType("application/json");
        ctx.result(pageList.toString());
    }

    /**
     * Endpoint Get Keys - returns JSON string array of all Sites
     * @param ctx Javalin.io http server context
     */
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

    /**
     * Endpoint SiteFles gets file directory hierarchy associated with a site and returns as
     * a JSON array with key value pairs (path, file)
     * @param SiteId SiteID
     * @return JSON String of Dir Hierarchy
     */
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
            System.out.println("Error retrieving directories, JSON Error");
            System.out.println(e.getCause());
            return "[]";
        }
    }

    /**
     * Endpoint renders a site/page in its original context for user display. Uses SiteTemplate injects
     * to merge the Page HTML resources with the overarching Template HTML file
     * @param ctx Javalin.io http server context
     */
    public void Display(Context ctx){
        try{
            assert ctx.pathParam("site") != null;
            assert ctx.pathParam("page") != null;
            DBNode SiteNode = mDB.GetNode("Sites");
            Site mSite = new Site(mDB.findKey(SiteNode, "Site",ctx.pathParam("site")));
            Page mPage = new Page(mSite.getPage(ctx.pathParam("page")));
            String themeFile = "resources/web/themes/" + mSite.getThemeID() + "/index.html";
            SiteTemplate PageTemplate = new SiteTemplate();
            SiteTemplate ThemeTemplate = new SiteTemplate();
            PageTemplate.GetTemplate("resources/web/sites/"+ctx.pathParam("site")+"/"+mPage.getFilename());
            ThemeTemplate.GetTemplate(themeFile);
            ThemeTemplate.AddKey("Title", mSite.getName());
            ThemeTemplate.AddKey("EditorCSS", "");
            ThemeTemplate.AddKey("EditorPostJS", "");
            ThemeTemplate.AddKey("EditorBody", "");
            ThemeTemplate.AddKey("PageCSS", "<link rel=\'stylesheet\' href=\'http://localhost:8080/sites/"+ctx.pathParam("site")+"/css/"+mPage.getCssFile()+"\'>");  //TODO Add in Page CSS - JS Templates
            ThemeTemplate.AddKey("PagePreJS", "");
            ThemeTemplate.AddKey("PagePostJS", "");
            ThemeTemplate.AddKey("PageBody", PageTemplate.GetHtml());
            ThemeTemplate.ReplaceKeys();
            ctx.contentType("text/html");
            ctx.result(ThemeTemplate.GetHtml());


        }catch(Exception e){
            System.out.println("Could not form site/page request");
            ctx.contentType("text/html");
            ctx.result("Error 404 Site Not Found");
        }
    }

    /**
     * Endpoint for saving pages with a site
     *
     * @param ctx Javalin.io http server context
     */
    public void SavePage(Context ctx){

        try {
            String PageName = ctx.pathParam("page");
            String SiteName = ctx.pathParam("site");
            DBNode PageNode = mDB.GetNode("Pages");
            DBNode SiteNode = mDB.GetNode("Sites");
            String HtmlContent = ctx.formParam("html");
            Map<String, List<String>> params = ctx.formParamMap();
            String JsonData = ctx.formParam("pageJson");
            Site mySite = new Site(mDB.findKey(SiteNode, "Site", SiteName));
            Page myPage = mySite.getPage(PageName);

            String srcPath = "resources/web/sites/" + SiteName + "/" + myPage.getFilename();

            if(Files.exists(Path.of(srcPath))){
                ctx.contentType("text/html");
                Files.writeString(Path.of(srcPath), HtmlContent);
                myPage.setJsonData(JsonData);
                mySite.addModel(myPage);
                SiteNode.setChanged(true);
                mDB.RunSync();
                String success = "{\"message\" : \"page saved\"}";
                ctx.contentType("application/json");
                ctx.result(success);
                return;
            }
            throw new Exception("Page html file not found");

        }catch(Exception e){
            System.out.println("Could not retrieve Page");
            System.out.println(e.toString());
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }

    }

    /**
     * Endpoint, given URL this method downloads file and stores in filesystem this is a general access point
     * for other applications
     *
     * @param ctx Javalin.io http server context
     */
    public void FetchURL(Context ctx){
        try{
            String siteName = ctx.pathParam("site");
            DBNode siteNode = mDB.GetNode("Sites");
            Site mySite = new Site(mDB.findKey(siteNode, "Site", siteName));
            String rootDir = Root +"/" + siteName;

            String urlPost = ctx.formParam("url");
            assert urlPost != null;
            String[] urlConstr = urlPost.split("/");
            String fileName = urlConstr[urlConstr.length-1];

            InputStream in = new URL(urlPost).openStream();
            Files.copy(in, Paths.get(rootDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            String uploadedUrl = "http://localhost:8080/sites/" + siteName + "/img/" + fileName;
            ctx.contentType("application/json");
            ctx.result("{\"success\": 1, \"file\":{\"url\":\""+uploadedUrl +"\"}}");

        }catch(Exception e) {
            System.out.println("Error uploading image to site by requested URL");
            ctx.contentType("application/json");
            ctx.result("{\"success\": 0}");
        }
    }

    /**
     * Lists directory contents
     *
     * @param ctx Javalin.io http server context
     */
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

    /**
     * List filesystem utility method
     *
     * @param dir Directory to be listed
     * @return set
     * @throws IOException the io exception
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
     * Utility class for deleting site files
     */
    private class DeleteVisitor extends SimpleFileVisitor<Path> {
        /**
         * File Vistory
         * @param file File path
         * @param attrs Attributes
         * @return Visit Result
         * @throws IOException File I/O Error throws to caller
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Site Directory visit result
         * @param dir directory
         * @param exc exception
         * @return FIle visit result
         * @throws IOException
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

}
