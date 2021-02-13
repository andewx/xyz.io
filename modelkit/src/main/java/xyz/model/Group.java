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

        put("UID", GroupIDName);
        put("GroupID", GroupID);
        put("Name",  Name);
        put("ClassName", ClassName);
        put("AccessDescription", AccessDescription);
        put("AccessLevel", AccessLevel);


    }

    public void update(){
        super.update();
        put("GroupID", GroupID);
        put("Name",  Name);
        put("ClassName", ClassName);
        put("AccessDescription", AccessDescription);
        put("AccessLevel", AccessLevel);
    }



}
