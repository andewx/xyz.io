package xyz.model;

public class Group extends ModelObject {

    public String GroupID;
    public String AccessDescription;
    public int AccessLevel;


    public Group(String GroupIDName, String Description, int AccessSpecifier){
        super();
        ClassName = "Group";
        GroupID = GroupIDName;
        UID = GroupID;
        Name = GroupID;
        AccessDescription = Description;
        AccessLevel = AccessSpecifier;

        updateKey("UID", GroupID);
        put("GroupID", GroupID);
        updateKey("Name",  Name);
        updateKey("ClassName", ClassName);
        put("AccessDescription", AccessDescription);
        put("AccessLevel", AccessLevel);


    }

    public void update(){
        super.update();
        updateKey("GroupID", GroupID);
        updateKey("Name",  Name);
        updateKey("ClassName", ClassName);
        updateKey("AccessDescription", AccessDescription);
        updateKey("AccessLevel", AccessLevel);
    }



}
