package xyz.model;


public class FileMap extends ModelObject {
    String FilePath;

    public FileMap(String name, String pathName){
        super();
        FilePath = pathName;
        Name = name;
        ClassName = "FileMap";
        UID = Name;
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("FilePath", FilePath);
    }

    public void update(){
        super.update();
        updateKey("UID", UID);
        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        updateKey("FilePath", FilePath);
    }
}
