package xyz.model;

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
