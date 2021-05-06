package xyz.dbkit;

import org.json.JSONException;
import org.json.JSONObject;
import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author briananderson
 * @version 1.0
 * DBNode class stores a root model object which is used for storing JSON formatted representations of the ModelObject classes. This
 * class is loaded with a hasChanged flag to indicate whether the DB sync operations should call on the DBNode to write itself to the filesystem.
 *
 */
public class DBNode {
    String name;
    String file;
    protected ModelObject rootGraph;
    boolean hasChanged;

    /**
     * DBNode Constructor
     * @param DBName DBNode Name
     * @param DBFilePath Filepath of DBNode
     * Requirement(DBkit 2.0)
     */
    public DBNode(String DBName, String DBFilePath){ //Instantiates New DBNode Object
        super();
        name = DBName;
        file = DBFilePath;
        rootGraph = new ModelObject();
        hasChanged = false;
    }

    /**
     * Stores the specified ModelObject - typically a dumb header node as its root
     * @param ModelName Root node to be held by DBNode
     * @throws JSONException Root not found
     * Requirement(DBkit 2.0)
     */
    public JSONObject Root(String ModelName) throws JSONException {
        JSONObject retObj = rootGraph.getModels(ModelName);
        if (retObj == null){
            throw new JSONException("Model: " + ModelName + " not found in immediate root graph");
        }
        return retObj;
    }

    /**
     * Gets the root node
     * @return ModelObject RootNode
     * Requirement(DBkit 2.0)
     */
    public ModelObject GetRoot(){
        return rootGraph;
    }

    /**
     * Creates a DBNode based on the Name and Path of the node specified. Path should be visibile to the JVM
     * via typically the resources directory.
     * @param name Name of the DBNOde
     * @param path Path for storage
     * @return instantiated node
     * @throws IOException File I/O Errors thrown to caller
     * Requirement(DBkit 2.0)
     */
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

    /**
     * Flags the DBNode as having modified
     * Requirement(DBkit 2.0)
     * @param value value of flag
     */
    public void setChanged(boolean value){
        hasChanged = value;
    }

    /**
     * Adds a Modelobject to the DBNode from the root\
     * Requirement(DBkit 2.0)
     * @param m ModelObject to be Added
     * @return ModelObject added
     */
    public ModelObject AddModel(ModelObject m){
         rootGraph.addModel(m);
         hasChanged = true;
         return m;
    }

    /**
     * Updates the Model by removing and then re-adding a ModelObject
     * Requirement(DBkit 2.0)
     * @param m ModelObject to be Updated
     * @return Updated ModelObject
     */
    public ModelObject UpdateModel(ModelObject m){
        JSONObject groupModel = rootGraph.getModels(m.getModelName());
        groupModel.remove(m.getUID());
        groupModel.put(m.getUID(), m);
        hasChanged = true;
        return m;
    }

    /**
     * Deletes a ModelObject from the DBNode root graph
     * Requirement(DBkit 2.0)
     * @param m ModelObject to be removed
     */
    public void DeleteModel(ModelObject m){
       String mUID = m.getUID();
       rootGraph.Remove(m.getModelName(), mUID);
       hasChanged = true;
    }

    /**
     * Deletes a model from this DBNode root Graph. Problematic if model is deeply stored this is not a DFS search
     * but assumes a flat search directory. Models internal to other model objects won't be removed
     * Requirement(DBkit 2.0)
     * @param Class Class of Model to be removed
     * @param UID UID of Model to be removed
     */
    public void DeleteModel(String Class, String UID){ hasChanged =true; rootGraph.Remove(Class, UID); }

    /**
     * Gets the DBNode filename
     * Requirement(DBkit 2.0)
     * @return filename of DBNode (Model.keys)
     */
    public String GetFile(){
        return file;
    }

    /**
     * Writes a DBNode to the FileSystem
     * Requirement(DBkit 2.0)
     * @param url URL of the Path to Be Written To
     * @throws IOException File/IO Exception thrown to caller
     */
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
