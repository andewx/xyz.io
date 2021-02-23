package xyz.dbkit;

import xyz.model.ModelKeys;
import xyz.dbkit.DBNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public final class DBUtils {

    public static DBNode InitNode(String ModelName){
        String Name = ModelKeys.pluralize(ModelName);
        String srcPath = "resources/";
        String modelPath = srcPath + Name + ".keys";
        try {
            return DBNode.CreateDBNode(Name, modelPath);
        }catch(IOException e){
            return null; //File Exists
        }
    }
}
