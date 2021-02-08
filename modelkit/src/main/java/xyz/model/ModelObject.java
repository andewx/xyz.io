package xyz.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.*;

public class ModelObject extends JSONObject implements Model {

    public String UID;
    public String ClassName;
    public String Name;
    public HashMap<String,Model> Children;


    public ModelObject(){
        super();
        UID = ModelUtils.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        Children = new HashMap<String,Model>();

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);

    }

    public ModelObject(String json){
        super(json);
        UID = (String)get("UID");
        ClassName = (String)get("ClassName");
        Name = (String)get("Name");
        Children = new HashMap<String,Model>();

        ArrayList<String> Keys = ModelUtils.ModelKeys();
        for (String key : keySet()){
           for(String k : Keys){
               if (k.compareTo(key) == 0){
                   JSONObject obj= (JSONObject)get(key);
                   Children.put((String)obj.get("UID"),(ModelObject)obj);
               }
           }
        }
    }

    public ModelObject(int size){
        super(size);
        UID = ModelUtils.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        Children = new HashMap<String,Model>();
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
    }

    @Override
    public void addModel(Model m) {
        Children.put(m.getUID(),m);
        put(m.getModelName(), m);
    }

    @Override
    public HashMap<String,Model> getChildren() {
        return Children;
    }

    @Override
    public String getModelName() {
        return ClassName;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getUID() {
        return UID;
    }

    @Override
    public JSONObject getJSON() {
        return (JSONObject)this;
    }

    @Override
    public Object updateKey(String key, Object value) {
       return put(key,value);
    }

    @Override
    public void removeKey(String key){
        put(key,(Object)null);
    }

    @Override
    public Model getModel(String uid) {
        return Children.get(uid);
    }

    @Override
    public ArrayList<Model> getModels(String className) {
        ArrayList<Model> myList = new ArrayList<Model>();
        for(Model e : Children.values()){
            if (e.getModelName().compareTo(className) == 0){
                myList.add(e);
            }
        }
        return myList;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    @Override
    public void update(){
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
    }
}
