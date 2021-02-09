package xyz.model;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * xyz.Model.Model interface
 * <p>Application Data Model. Interfaces presents models as Key:Value Property Lists with internal
 * model nodes available for tree structures. Presents relational database specifiers like Unique ID, Parent ID,
 and a model Name for unique database identification.* </p>
 */
public interface Model {

    //Getters/Setters
    public String getModelName();
    public String getName();
    public String getUID();

    //Model Property Methods
    public JSONObject getJSON();
    public Object updateKey(String key, Object value);
    public void removeKey(String key);
    public void update();

    //Child Handlers
    public HashMap<String,HashMap<String,Model>> getChildren();
    public Model getModel(String mClass, String uid);
    public void addModel(Model m);
    public HashMap<String,Model> getModels(String mClass);

    public String toString();
    public String toJson();


}

