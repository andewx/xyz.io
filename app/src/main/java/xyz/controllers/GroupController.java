package xyz.controllers;

import io.javalin.http.Context;
import org.json.JSONObject;
import xyz.app.AppManager;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.Group;
import xyz.model.ModelKeys;
import xyz.model.ModelObject;
import xyz.model.User;

import java.util.ArrayList;

public class GroupController extends BaseController {
    public GroupController(DBMain db_instance, AppManager appManager) {
        super(db_instance, appManager);
    }

    public void GetGroupStarts(Context ctx){
        String name = ctx.pathParam("name");
        ArrayList<ModelObject> groupList;
        try {
            groupList = (ArrayList<ModelObject>) mDB.findStartsWith(mDB.GetNode("Groups"), "Group", name);
        }catch(Exception e){
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }
        if(groupList!= null){
            ctx.contentType("application/json");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(int index = 0; index < groupList.size(); index++){
                if (index != groupList.size() - 1) {
                    sb.append(groupList.get(index).toJson());
                    sb.append(" , ");
                }else{
                    sb.append(groupList.get(index).toJson());
                }
            }
            sb.append("]");
            ctx.result(sb.toString());
        }
    }

    public void GetGroups(Context ctx) {
        ArrayList<ModelObject> groupList = new ArrayList<>();
        JSONObject children;
        try {
            DBNode groupsNode = mDB.GetNode("Groups");
            ModelObject root = groupsNode.GetRoot();
            children = root.getChildren("Groups");
            for (String key : children.keySet()) {
                JSONObject jObj = children.getJSONObject(key);
                Group gObj = new Group(jObj);
                groupList.add(gObj);
            }
        } catch (Exception e) {
            ctx.contentType("application/json");
            ctx.result("[]");
            return;
        }

        ctx.contentType("application/json");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int index = 0; index < groupList.size(); index++) {
            if (index != groupList.size() - 1) {
                sb.append(groupList.get(index).toJson());
                sb.append(" , ");
            } else {
                sb.append(groupList.get(index).toJson());
            }
        }
        sb.append("]");
        ctx.result(sb.toString());
    }

    public void Form(Context ctx){
        User defaultUser = (User) ModelKeys.Default("Group");
        String content = defaultUser.Form();
        ctx.contentType("text/html");
        ctx.result(content);
    }
}
