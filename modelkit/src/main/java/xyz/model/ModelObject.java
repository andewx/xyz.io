package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

public class ModelObject extends JSONObject {

    protected String UID;
    protected String ClassName;
    protected String Name;

    public ModelObject(){
        super();
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);

    }



    public ModelObject(JSONObject jObj){

        super();

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

    }

    public ModelObject(int size){
        super(size);
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
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
    }

    //Getters and Setters ------

    public void setUID(String UID) {
        this.UID = UID;
        updateKey("UID", UID);

    }

    public String getUID() {
        return UID;
    }


    public void setName(String name) {
        Name = name;
        updateKey("Name", Name);
    }

    public String getName() {
        return Name;
    }


    
    public String getModelName() {
        return ClassName;
    }

    public void setModelName(String className) {
        ClassName = className;
        updateKey("ClassName", ClassName);
    }

    
    public JSONObject getJson() {
        return (JSONObject)this;
    }

    
    public Object updateKey(String key, Object value) {
       return put(key,value);
    }

    
    public void removeKey(String key){
        remove(key);
    }

    
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

        return true;
    }

    
    public JSONObject getModels(String mClass) {
      return getChildren(pluralize(mClass));
    }

    
    public String toString() {
        return super.toString();
    }

    
    public String toJson() {
        return super.toString();
    }

    public String getNode(){
        return pluralize(ClassName);
    }

    public String getKey(){
        return "UID";
    }

    public String Form(){ //Override for custom form element processing
        StringBuilder sb = new StringBuilder();
        sb.append("<form id='" + getModelName() + "' class='model-form'><ul><li><div class='form-label'>");
        boolean required = false;
        for(String key : keySet()){ //No UID / ClassName
            if (!key.equals("UID") && !key.equals("ClassName")){
                sb.append(key);
                sb.append(GetInputTag(key));
                sb.append("</div></li>");
            }
        }
        sb.append("</ul></form>");
        return sb.toString();
    }

    public String GetInputTag(String key){
        return "<input type ='text' value='" + get(key) + " id='" + key + "' name='" + key + "' ></input>";
    }



}
