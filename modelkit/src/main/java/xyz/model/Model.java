package xyz.model;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/**
 * xyz.Model.Model interface
 * <p>Application Data Model. Interfaces presents models as Key:Value Property Lists with internal
 * model nodes available for tree structures. Presents relational database specifiers like Unique ID, Parent ID,
 and a model Name for unique database identification.* </p>
 */

public interface Model {

    //Model Factory Methods
    public Model createModel(String unique); //Initialization basis for a model

    //Model ID Types
    public String getUID(); //Gets Model Id
    public String getName(); //Model Class Name

    //Model Property Methods
    public void setProperty(String propKey, String propValue); //This may mirror internal attributes (reflection case)
    public String getProperty(String propKey, String propValue); //Get Property Key Value
    public String getPropertyType(String propKey); //Switch case method for casting types

    //Model Internal Recursion (K - Tree)
    public ArrayList<Model> hasModels(); //Nodal References to other Model Objects
    public void addModel(Model m);
    public Model getModel(String uid);

    //Model Property Reflection
    public void syncModel(boolean syncInternal); //Synchronize Property Lists And Model Lists Recursively (cycle risk)

}

