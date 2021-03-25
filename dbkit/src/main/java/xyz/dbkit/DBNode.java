package xyz.dbkit;

import org.json.JSONException;
import org.json.JSONObject;
import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DBNode {
    String name;
    String file;
    protected ModelObject rootGraph;
    boolean hasChanged;

    public DBNode(String DBName, String DBFilePath){ //Instantiates New DBNode Object
        super();
        name = DBName;
        file = DBFilePath;
        rootGraph = new ModelObject();
        hasChanged = false;
    }

    public JSONObject Root(String ModelName) throws JSONException {
        JSONObject retObj = rootGraph.getModels(ModelName);
        if (retObj == null){
            throw new JSONException("Model: " + ModelName + " not found in immediate root graph");
        }
        return retObj;
    }

    public ModelObject GetRoot(){
        return rootGraph;
    }

    public static DBNode CreateDBNode(String name, String path) throws IOException {
        DBNode myNode = new DBNode(name, path);
        Path myPath = Path.of(myNode.file);
        boolean exists =  Files.exists(myPath);
        if(exists){
            myNode.rootGraph = new ModelObject(Files.readString(myPath));
        }else{
            Files.createFile(myPath);
            Files.writeString(myPath, myNode.rootGraph.toString());
        }
        return myNode;
    }

    public ModelObject AddModel(ModelObject m){
         rootGraph.addModel(m);
         hasChanged = true;
         return m;
    }

    public ModelObject UpdateModel(ModelObject m){
        JSONObject groupModel = rootGraph.getModels(m.getModelName());
        groupModel.remove(m.getUID());
        groupModel.put(m.getUID(), m);
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
