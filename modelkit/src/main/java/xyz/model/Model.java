package xyz.model;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public interface Model {

    //Getters/Setters
    public String getModelName();
    public String getName();
    public String getUID();

    //Model Property Methods
    public JSONObject getJson();
    public Object updateKey(String key, Object value);
    public void removeKey(String key);
    public void update();

    //Child Handlers
    public HashMap<String,HashMap<String,ModelObject>> getChildren();
    public ModelObject getModel(String mClass, String uid);
    public void addModel(ModelObject m);
    public HashMap<String,ModelObject> getModels(String mClass);
    public boolean Remove(String mClass, String uid);
    public ModelIterator getIterator();

    public String toString();
    public String toJson();


}

