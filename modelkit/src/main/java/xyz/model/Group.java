package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Group extends ModelObject {

    protected String GroupID;
    protected String AccessDescription;
    protected int AccessLevel;


    public Group(String GroupIDName, String Description, int AccessSpecifier) {
        super();
        ClassName = "Group";
        GroupID = GroupIDName;
        UID = GroupID;
        Name = GroupID;
        AccessDescription = Description;
        AccessLevel = AccessSpecifier;

        updateKey("UID", GroupID);
        put("GroupID", GroupID);
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("AccessDescription", AccessDescription);
        put("AccessLevel", AccessLevel);


    }

    public Group(String json){
        super(json);
        GroupID = (String)get("GroupID");
        AccessDescription = (String)get("AccessDescription");
        Integer tempInt = (Integer)get("AccessLevel");
        AccessLevel = tempInt.intValue();
        put("GroupID", GroupID);
        put("AccessDescription", AccessDescription);
        put("AccessLevel", AccessLevel);
    }

    public Group(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            GroupID = (String)jObj.get("GroupID");
            AccessDescription = (String)jObj.get("AccessDescription");
            Integer tempInt = (Integer)jObj.get("AccessLevel");
            AccessLevel = tempInt.intValue();
            put("GroupID", GroupID);
            put("AccessDescription", AccessDescription);
            put("AccessLevel", AccessLevel);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    @Override
    public String Form(){ //Too Coupled with HTML i.e. Forms need to know which classes to use.
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='width:100%'><div style='float:right'><div class='form-close'>X</div></div></div>" );
        sb.append("<form id='group-form' action='model/group_submit/Group' method='post' value='Submit' enctype='multipart/form-data' class='model-form'><legend><label> Create Group</label></legend>");
        boolean required = false;
        for(String key : keySet()){ //No UID / ClassName
            if (!key.equals("UID") && !key.equals("ClassName") && !key.equals("GroupID") && !key.equals("LoginLast")){
                sb.append("<div class='form-item'><div class='form-item-label col-1-2'>");
                sb.append(key);
                sb.append("</div>");
                sb.append(GetInputTag(key));
                sb.append( "</div>");
            }
        }
        sb.append("<input id='submitUser' type='submit' value='Submit'/>");
        sb.append("</form>");
        return sb.toString();
    }

    @Override
    public String GetInputTag(String key){ //too much coupling with - CSS/HTML Specifics
        String type = "<input class='col-1-2' type='text' maxLength='45' value='" + get(key)  + "' name='" + key + "'></input>";
        if(key.equals("AccessDescription")){
            type = "<textarea class='col-1-2' type='text' maxLength='45' value='" + get(key) + "' name='" + key + "' required></textarea>";
        }
        if(key.equals("AccessLevel")){
            type = "<input class='col-1-2' type='number' min=1 max=5 " +" value='" + get(key) + "' name='" +  key +  "' required></input>";
        }
        return type;
    }

    public String getKey(){
        return "GroupID";
    }

    public String GetGroupID() {
        return GroupID;
    }

    public String GetAccessDescription() {
        return AccessDescription;
    }

    public int GetAccessLevel() {
        return AccessLevel;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
        updateKey("GroupID", GroupID);
    }

    public void setAccessDescription(String accessDescription) {
        AccessDescription = accessDescription;
        updateKey("AccessDescription", AccessDescription);
    }

    public void setAccessLevel(int accessLevel) {
        AccessLevel = accessLevel;
        updateKey("AccessLevel", AccessLevel);
    }
}
