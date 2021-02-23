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

    public String Form(){ //Override for custom form element processing
        StringBuilder sb = new StringBuilder();
        sb.append("<form id='" + getModelName() + "' class='model-form'><ul><li><div class='form-label'>");
        boolean required = false;
        for(String key : keySet()){ //No UID / ClassName
            if (!key.equals("UID") && !key.equals("ClassName")){
                sb.append(key);
                sb.append(GetInputTag(key));
                sb.append( "</div></li>");
            }
        }
        sb.append("</ul></form>");
        return sb.toString();
    }

    public String GetInputTag(String key){
        String type = "<input type='text' maxLength='45'" +" value='" + get(key) + " id='" + key + "' name='" + key + "' required></input>";
        if(key.equals("AccessDescription")){
            type = "<textarea rows='50' cols='3'" +" value='" + get(key) + " id='" + key + "' name='" + key + "'></textarea>";
        }
        if(key.equals("AccessSpecifier")){
            type = "<input type='number' min='1' max='5'"+" value='" + get(key) + " id='" + key + "' name='" + key + "'></input>";
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
