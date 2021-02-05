package xyz.dbengine;

import xyz.model.Model;
import java.util.ArrayList;
import java.util.HashMap;

//Database Engine Query Methods
public interface DBEngine {

    //Query API - Imperative Interface
    ArrayList<Model> QueryID(String modelName, String id);
    ArrayList<Model> QueryNID(String modelName, String id);
    ArrayList<Model> QueryPropertyMatchesValue(String modelName, String property, String value);
    ArrayList<Model> QueryPropertyNotMatchesValue(String modelName, String property);
    ArrayList<Model> QueryListPropertyMatch(String modelName, ArrayList<String> props, ArrayList<String> values);
    ArrayList<Model> QueryRelationWithParentID(String relationalModelName, String id);

    //Internal Model Handling
    int CreateModel(Model model);
    int DeleteModel(Model model);
    int EditModel(Model model, HashMap<String,String> props);
    int UpdateModelProps(String modelName, String id, HashMap<String,String> props);


    //Filesystem Storage API - Would put security methods here as well
    void getConfigFile(String filePathString);
    HashMap<String,String> getDatabaseMap(); //Maps database name to FILE
    int IngestDatabase(String database); //Reads Database into internal memory FILE
    int WriteDatabase(String database); //Writes internal memory representation back into storage format in FILE
}
