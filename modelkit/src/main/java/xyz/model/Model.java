package xyz.model;
import java.util.Map;
import java.util.HashMap;

//Model Interface - Database/Key Store Mapping Interface K is the Key Store for the Resource ID
//Relational models do not store references to other models in there bodies unless through ref properties.
//Models are primarily database objects
public interface Model {

    public Model createModelDBInstance(); //Returns this model with appropriate IDs and relational position in db
    public String getName(); //Model Class Name
    public boolean isUnique(); //Is Model Duplicated
    public String getModelID();  //Model ID For Rel JSON DB
    public String getParentID(); //Parent ID for Rel Items
    public HashMap<String,String> getProperties(); //Properties List
    public HashMap<String,String> hasModelsWithEdges(); //Directive Relational Edges Have Properties {Model : EdgeTye}
    public Model parseJson(String json); //Constructs Model from JSON
    public String encodeJson(); //Encodes Model into JSON Object

}
