package xyz.model;

import org.json.JSONObject;

/**
 * Group objects represent a group object usually used by Authentication storage for the backend router services
 * @author briananderson
 * @see xyz.model.ModelObject
 */
public class Group extends ModelObject {

    protected String GroupID;
    protected String AccessDescription;
    protected int AccessLevel;


    /**
     * Group Constructor - constructs model Object as a extended class of model object
     * @param GroupIDName - Name of the Group (UID Key)
     * @param Description - Group description
     * @param AccessSpecifier - Integer access level. Intended for authenticating routes based on ring security.
     * @see xyz.model.ModelObject
     */
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

    /**
     * Group constructor with a JSONObject
     * @param jObj - The well formatted JSONObject of a Group
     */
    public Group(JSONObject jObj){
        try {
            for (String key : jObj.keySet()) {
                put(key, jObj.get(key));
                if (key.equals("UID")) {
                    UID = jObj.getString(key);
                }
                if (key.equals("ClassName")) {
                    ClassName = jObj.getString(key);
                }
                if (key.equals("Name")) {
                    Name = jObj.getString(key);
                }
                if (key.equals("AccessDescription")) {
                    AccessDescription = jObj.getString(key);
                }
                if (key.equals("AccessLevel")) {
                    AccessLevel = jObj.getInt("AccessLevel");
                }
            }
        }catch(NullPointerException e){
            System.out.println("new Group(JSONObject jObj) failed. jObj is null");
        }

    }

    /**
     * Group constructor
     * @param json -- A well formatted JSON string of a group object
     */
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

    /**
     * Returns HTML Form tags for creating a group object
     * @return HTML Form tag strings for creating a group object
     */
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

    /**
     * Gets input tags for editing Group objects on a site page
     * @param key
     * @return - Input tags
     */
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

    /**
     * Getter GroupID
     * @return this GroupID
     */
    public String GetGroupID() {
        return GroupID;
    }

    /**
     * Getter Access Description
     * @return this AccessDescription
     */
    public String GetAccessDescription() {
        return AccessDescription;
    }

    /**
     * Getter AccessLevel
     * @return this AccessLevel
     */
    public int GetAccessLevel() {
        return AccessLevel;
    }

    /**
     * Setter Group ID
     * @param groupID Group ID, which is also taken to be the name of the group
     */
    public void setGroupID(String groupID) {
        GroupID = groupID;
        updateKey("GroupID", GroupID);
    }

    /**
     * Setter AccessDescription
     * @param accessDescription Description of access intention for group
     */
    public void setAccessDescription(String accessDescription) {
        AccessDescription = accessDescription;
        updateKey("AccessDescription", AccessDescription);
    }

    /**
     * Setter Access Level
     * @param accessLevel the integer access level for the group for accessing routes
     */
    public void setAccessLevel(int accessLevel) {
        AccessLevel = accessLevel;
        updateKey("AccessLevel", AccessLevel);
    }
}
