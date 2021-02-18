package xyz.dbkit;

import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DBNode {
    String name;
    String file;
    public ModelObject rootGraph;
    boolean hasChanged;

    public DBNode(String DBName, String DBFilePath){ //Instantiates New DBNode Object
        super();
        name = DBName;
        file = DBFilePath;
        rootGraph = new ModelObject();
        hasChanged = false;
    }

    public ModelObject AddModel(ModelObject m){
         rootGraph.addModel(m);
         hasChanged = true;
         return m;
    }

    public void DeleteModel(ModelObject m){
       String mUID = m.getUID();
       rootGraph.Remove(m.getModelName(), mUID);
       hasChanged = true;
    }

    public void DeleteModel(String Class, String UID){ hasChanged =true; rootGraph.Remove(Class, UID); }

    public String GetFile(){
        return file;
    }

    public void WriteNode(String url) throws IOException {
        Path file = Path.of(url);
        boolean exists = Files.exists(file);
        if(!exists){
            Files.createFile(file);
            Files.writeString(file,rootGraph.toString());
        }
        else{
            Files.writeString(file, rootGraph.toString());
        }
        hasChanged = false;
    }

}
