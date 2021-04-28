package xyz.controllers;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelObject;
import xyz.model.Page;
import xyz.model.Site;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Page controller provides basic page functionality but most page logic is handled by its owner
 * the site. We provide some Page specific endpoints here however since we store pages by our business logic
 * inside of other Site objects these pages are not generally visible to the DBKIT query interface. You must reference
 * the SiteID of a page and get that site to get the associated Page JSON object.
 */
public class PageController extends BaseController{

    /**
     * Instantiates a new Page controller.
     *
     * @param db_instance the db instance
     * @param app         the app
     */
    public PageController(DBMain db_instance, AppManager app) {
        super(db_instance, app);
    }

    /**
     * Gets all pages in the Pages DBNode, this route should not typically be used since sites store
     * pages.
     *
     * @param ctx the ctx
     */
    public void Get(Context ctx){
        ArrayList<ModelObject> PageList = new ArrayList<>();
        JSONObject children;
        try{
            DBNode PagesNode = mDB.GetNode("Pages");
            ModelObject root = PagesNode.GetRoot();
            children = root.getChildren("Pages");
            for (String key : children.keySet()) {
                JSONObject jObj = children.getJSONObject(key);
                Site gObj = new Site(jObj);
                PageList.add(gObj);
            }
        }catch(Exception e){
            System.out.println("Could Not Fetch Sites, or none exist");
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        ctx.contentType("application/json");
        ctx.result(PageList.toString());
    }

    /**
     * Gets page by UID using path parameter id
     *
     * @param ctx the ctx
     */
    public void GetID(Context ctx){
        String PageID = ctx.pathParam("id");
        DBNode PageNode = mDB.GetNode("Pages");

        try {
            ModelObject myPage = mDB.findKey(PageNode, "Page", PageID);
            ctx.contentType("application/json");
            ctx.result(myPage.toString());
        }catch(Exception e){
            System.out.println("Could not find Page[ " + PageID + "]");
            ctx.contentType("application/json");
            ctx.result("[]");
        }
    }

    /**
     * Gets a page from an associated site. This is the typical method you should use when dealing with
     * sites. Takes the form param (name) for a PageName  and gets the associated Site from SiteID
     *
     * @param ctx the ctx
     */
    public void GetPage(Context ctx){

        try {
            String PageName = ctx.formParam("name");
            DBNode PageNode = mDB.GetNode("Pages");
            DBNode SiteNode = mDB.GetNode("Sites");
            Page myPage = new Page(mDB.findKey(PageNode, "Page", PageName));
            Site mySite = new Site(mDB.findKey(SiteNode, "Site", myPage.getSiteID()));

            String srcPath = "/sites/" + mySite.getName() + "/" + myPage.getName() + ".html";

            if(Files.exists(Path.of(srcPath))){
                ctx.contentType("text/html");
                String htmlString = Files.readString(Path.of(srcPath));
                ctx.result(htmlString);
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
     * Gets Page file contents from form params (name) PageName, & (path) filepath
     *
     * @param ctx the ctx
     */
    public void FileContents(Context ctx){
        try {
            String PageName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            String content = Files.readString(Path.of(filePath));
            ctx.contentType("text");
            ctx.result(content);
            return;
        }catch(Exception e){
            System.out.println("File / Page Could Not Be Found");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

    /**
     * Edits a pages file contents with the provide POST form parameter contents
     *
     * @param ctx the ctx
     */
    public void EditFileContents(Context ctx){
        try {
            String PageName = ctx.formParam("name");
            String filePath = ctx.formParam("path");
            String content = ctx.formParam("content");
            assert content != null;
            Files.writeString(Path.of(filePath), content);
            ctx.contentType("text");
            ctx.result("File written");
            return;
        }catch(Exception e){
            System.out.println("File / Page Could Not Be Found");
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

    /**
     * Adds page file to the sites filesystem
     *
     * @param ctx the ctx
     */
    public void AddFiles(Context ctx){
        try {
            String PageName = ctx.formParam("name");
            DBNode PageNode = mDB.GetNode("Pages");
            DBNode SiteNode = mDB.GetNode("Sites");
            Page myPage = new Page(mDB.findKey(PageNode, "Page", PageName));
            Site mySite = new Site(mDB.findKey(SiteNode, "Site", myPage.getSiteID()));
            String rootDir = "/sites/" + mySite.getName();

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
            System.out.println("Error adding files to Page");
            System.out.println(e.toString());
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }

    }

    /**
     * Updates a pages contents
     *
     * @param ctx the ctx
     */
    public void UpdatePage(Context ctx){
        try{
            String PageName = ctx.formParam("name");
            String contents = ctx.formParam("html");
            DBNode PageNode = mDB.GetNode("Pages");
            DBNode SiteNode = mDB.GetNode("Sites");
            Page myPage = new Page(mDB.findKey(PageNode, "Page", PageName));
            Site mySite = new Site(mDB.findKey(SiteNode, "Site", myPage.getSiteID()));
            String srcURL = "/sites/" + mySite.getName() + "/" + myPage.getName() + ".html";

            if(Files.exists(Path.of(srcURL))){
                Files.write(Path.of(srcURL), contents.getBytes());
            }
            throw new Exception("File path to page does not exist: " + srcURL);

        }catch(Exception e){
            System.out.println("Error updating page");
            System.out.println(e.toString());
            ctx.contentType("text");
            ctx.result("Bad Request");
            return;
        }
    }

}
