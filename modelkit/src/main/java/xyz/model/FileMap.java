package xyz.model;


public class FileMap extends ModelObject {
   protected String FilePath;

    public FileMap(String name, String pathName){
        super();
        FilePath = pathName;
        Name = name;
        ClassName = "FileMap";
        UID = Name;
        updateKey("UID", UID);
        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("FilePath", FilePath);
    }


    public String GetFilepath(){
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
        updateKey("FilePath", FilePath);
    }
}
