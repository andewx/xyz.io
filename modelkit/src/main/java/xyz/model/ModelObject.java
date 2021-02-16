package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ModelObject extends JSONObject implements Model {

    public String UID;
    public String ClassName;
    public String Name;
    public String Modified;

    public ModelObject(){
        super();
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Modified = Instant.now().toString();
        Name = "ModelBase";

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("Modified", Modified);

    }

    public ModelObject(JSONObject jObj){

        super();

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




    }

    public ModelObject(int size){
        super(size);
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        Modified = Instant.now().toString();
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
        put("Modified", Modified);
    }

    public JSONObject getChildren(String Pluralized){ //Expects the Plura
        try {
            JSONObject childMap = (JSONObject)get(Pluralized);
            return childMap;
        }catch(JSONException e){
            return null;
        }catch(ClassCastException e){ //Returned JSON Object
                return null;
        }
    }

    public String plural(){
        return pluralize(getModelName());
    }

    public String pluralize(String str){
        int index = str.lastIndexOf(str);
        char c = str.charAt(index);
        if (c != 's'){
            return str + "s";
        }
        return str;
    }

    @Override
    public void addModel(ModelObject m) {
        JSONObject internal = getChildren(m.plural());
        if (internal == null){
             internal = new JSONObject();
             internal.put(m.getUID(), m);
             put(m.plural(),internal);
        }else{
            internal.put(m.getUID(), m);
            updateKey(m.plural(), internal);
        }

        Modified = Instant.now().toString();
        update();

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
        JSONObject internal = getChildren(pluralize(mClass));
        if(internal == null){
            throw new JSONException("Model not found");
        }
        try {
            ModelObject mObj = (ModelObject) internal.get(uid);
            if(mObj == null){
                throw new JSONException("Model: "+mClass+ "[ " + uid + "] not found\n");
            }
            return mObj;
        }catch(ClassCastException e){
            JSONObject jObj = (JSONObject)internal.get(uid);
            return new ModelObject(jObj);
        }

    }

    @Override
    public boolean Remove(String mClass, String uid){
        String collection = pluralize(mClass);
        JSONObject internal = getChildren(collection);
        if (internal == null){
            return false;
        }

        ModelObject val = (ModelObject)internal.remove(uid);
        if (val == null){
            return false;
        }

        Modified = Instant.now().toString();
        updateKey("Modified", Modified);

        return true;
    }

    @Override
    public JSONObject getModels(String mClass) {
      return getChildren(pluralize(mClass));
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

        updateKey("UID", UID);
        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        updateKey("Modified", Modified);

    }
}
