package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelObject extends JSONObject implements Model {

    public String UID;
    public String ClassName;
    public String Name;
    public String Modified;
    public HashMap<String,HashMap<String, ModelObject>> Children; // Stores Classes of Models followed by UID Keys

    public ModelObject(){
        super();
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Modified = Instant.now().toString();
        Name = "ModelBase";
        Children = new HashMap<String,HashMap<String,ModelObject>>();

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("Modified", Modified);

    }

    public ModelObject(JSONObject jObj){

        super();
        Children = new HashMap<String,HashMap<String,ModelObject>>();

        try { //Assumes jObj is a ModelObject internally
            UID = (String) jObj.get("UID");
            ClassName = (String) jObj.get("ClassName");
            Name = (String) jObj.get("Name");
            Modified = (String) jObj.get("Modified");
            put("UID", UID);
            put("ClassName", ClassName);
            put("Name", Name);
            put("Modified", Modified);

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
        Modified = (String)get("Modified");
        Children = new HashMap<String,HashMap<String,ModelObject>>();


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
        Modified = Instant.now().toString();
        Children = new HashMap<String,HashMap<String,ModelObject>>();
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("Modified", Modified);
    }

    @Override
    public void addModel(ModelObject m) {
        HashMap<String,ModelObject> internal = Children.get(m.getModelName());

        if (internal == null){
            HashMap<String,ModelObject> refHashMap = new HashMap<String,ModelObject>();
            refHashMap.put(m.getUID(), m);
            Children.put(m.getModelName(), refHashMap);
            put(m.getModelName(), refHashMap);
        }else{
            internal.put(m.getUID(), m);
            put(m.getModelName(), internal);
        }

        Modified = Instant.now().toString();

    }

    @Override
    public HashMap<String,HashMap<String,ModelObject>> getChildren() {
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
    public JSONObject getJson() {
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
    public ModelObject getModel(String mClass, String uid) {
        HashMap<String, ModelObject> internal = Children.get(mClass);
        ModelObject mObj = (ModelObject)internal.get(uid);

        if (mObj == null){
            throw new JSONException("Model not found");
        }

        return mObj;
    }

    @Override
    public boolean Remove(String mClass, String uid){
        HashMap<String, ModelObject> internal = Children.get(mClass);
        if (internal == null){
            return false;
        }

        Model val = internal.remove(uid);
        if (val == null){
            return false;
        }

        Modified = Instant.now().toString();

        return true;
    }

    @Override
    public HashMap<String,ModelObject> getModels(String mClass) {
      return (HashMap<String,ModelObject>)Children.get(mClass);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String toJson() {
        return super.toString();
    }

    @Override
    public ModelIterator getIterator(){
        ModelIterator myIter = new ModelIterator(this);
        return myIter;
    }

    @Override
    public void update(){
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("Modified", Modified);
    }
}
