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
    public HashMap<String,HashMap<String,Model>> getChildren();
    public Model getModel(String mClass, String uid);
    public void addModel(Model m);
    public HashMap<String,Model> getModels(String mClass);
    public boolean Remove(String mClass, String uid);

    public String toString();
    public String toJson();


}

