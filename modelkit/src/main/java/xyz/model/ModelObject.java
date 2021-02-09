package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelObject extends JSONObject implements Model {

    public String UID;
    public String ClassName;
    public String Name;
    public HashMap<String,HashMap<String, Model>> Children; // Stores Classes of Models followed by UID Keys

    public ModelObject(){
        super();
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        HashMap<String,Model> multiModel = new HashMap<String,Model>();
        Children = new HashMap<String,HashMap<String,Model>>();

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);

    }

    public ModelObject(JSONObject jObj){

        super();
        HashMap<String,Model> multiModel = new HashMap<String,Model>();
        Children = new HashMap<String,HashMap<String,Model>>();

        try { //Assumes jObj is a ModelObject internally
            UID = (String) jObj.get("UID");
            ClassName = (String) jObj.get("ClassName");
            Name = (String) jObj.get("Name");
            put("UID", UID);
            put("ClassName", ClassName);
            put("Name", Name);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }


    }

    public ModelObject(String json){
        super(json);
        UID = (String)get("UID");
        ClassName = (String)get("ClassName");
        Name = (String)get("Name");
        HashMap<String,Model> multiModel = new HashMap<String,Model>();
        Children = new HashMap<String,HashMap<String,Model>>();


        ArrayList<String> Keys = ModelKeys.ModelKeys();
        for (String key : keySet()){ //New item groups have "995edf..key" -> JSON
           for(String k : Keys){
               if (k.compareTo(key) == 0){
                   JSONObject obj= (JSONObject)get(key);
                   for (String internalKey : obj.keySet()) {
                       ModelObject myObj = new ModelObject((JSONObject)obj.get(internalKey));
                       addModel(myObj);
                   }
               }
           }
        }
    }

    public ModelObject(int size){
        super(size);
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        HashMap<String,Model> multiModel = new HashMap<String,Model>();
        Children = new HashMap<String,HashMap<String,Model>>();
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
    }

    @Override
    public void addModel(Model m) {
        HashMap<String,Model> internal = Children.get(m.getModelName());

        if (internal == null){
            HashMap<String,Model> refHashMap = new HashMap<String,Model>();
            refHashMap.put(m.getUID(), m);
            Children.put(m.getModelName(), refHashMap);
            put(m.getModelName(), refHashMap);
        }else{
            internal.put(m.getUID(), m);
            put(m.getModelName(), internal);
        }

    }

    @Override
    public HashMap<String,HashMap<String,Model>> getChildren() {
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
    public Model getModel(String mClass, String uid) {
        HashMap<String, Model> internal = Children.get(mClass);
        ModelObject mObj = (ModelObject)internal.get(uid);

        if (mObj == null){
            throw new JSONException("Model not found");
        }

        return mObj;
    }

    @Override
    public HashMap<String,Model> getModels(String mClass) {
      return  (HashMap<String,Model>)get(mClass);
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
