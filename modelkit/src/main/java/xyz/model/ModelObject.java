package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author briananderson
 * @version 1.0
 * The base Model class extends the JSONObject class, therefore all Models are represented as JSON stores whose key
 * value pairs store data to be updated. The implementation of a specific class maps its own fields to these key/value pairs.
 * JSONObjects may be deep tree structures, when initializing from a nested tree structure ModelObject stores the branch
 * as a JSONObject type

 */
public class ModelObject extends JSONObject {

    protected String UID;
    protected String ClassName;
    protected String Name;

    /**
     * Base constructor, generates a random 512 bit UID for the model and sets the ClassName and Name
     */
    public ModelObject(){
        super();
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";

        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);

    }


    /**
     * Constructs a complete ModelObject from the key/value pairs present in JSONObject. Not all fields map to the ModelObject
     * fields so that inherited classes can make use of ModelObject construction through the super constructor
     * @param jObj - Well formatted JSONObject
     * Requirement(Modelkit 1.1)
     */
    public ModelObject(JSONObject jObj){

        for(String key : jObj.keySet()){
            try {
                put(key, jObj.getString(key));
            }catch(JSONException e){
                put(key, jObj.getJSONObject(key));
            }
            if(key.equals("UID")){
                UID = jObj.getString(key);
            }
            if(key.equals("ClassName")){
                ClassName = jObj.getString(key);
            }
            if(key.equals("Name")){
                Name = jObj.getString(key);
            }
            if(key.equalsIgnoreCase("empty")){
                remove(key);
            }
        }
    }

    /**
     * Returns the JSONObject key as a string. User should generally be aware of whether the internal key value is a string
     * otherwise the exception case is silently handled and an empty string is returned
     * @param Key - JSON Object key
     * @return String value from key or empty string
     * Requirement(Modelkit 1.1)
     */
    public String GetString(String Key){
        try{
           return this.getString(Key);
        }catch(JSONException e){
           return "";
        }
    }

    /**
     * Copies a model object into a new one with a shallow copy of the ModelObject field attributes only. May not want
     * to use this class as it is not fully tested
     * @param mObj - Modelobject to be copied
     * @throws JSONException - JSON super error.
     * Requirement(Modelkit 1.1)
     */
    public ModelObject(ModelObject mObj) throws JSONException{ //Shallow Copies
        super();
        try { //Assumes jObj is a ModelObject internally
            UID = mObj.GetString("UID");
            ClassName = mObj.GetString("ClassName");
            Name = mObj.GetString("Name");
            put("UID", UID);
            put("ClassName", ClassName);
            put("Name", Name);

        }catch(JSONException | NullPointerException e){

        }
    }

    /**
     * Creates a model object from a JSON String, initializes with the super JSONObject constructor first
     * @param json - JSON string to be copied
     * Requirement(Modelkit 1.1)
     */
    public ModelObject(String json){
        super(json);

        UID = getString("UID");
        ClassName = getString("ClassName");
        Name =  getString("Name");
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
    }

    /**
     * Creates a ModelObject of a specified size
     * @param size - Size of the model object to be created
     * Requirement(Modelkit 1.1)
     */
    public ModelObject(int size){
        super(size);
        UID = ModelKeys.genUID();
        ClassName = "ModelBase";
        Name = "ModelBase";
        put("UID", UID);
        put("ClassName", ClassName);
        put("Name", Name);
    }

    /**
     * Attempts to copy a ModelObject with a specific key value (uid) from a JSONObject
     * @param obj - JSONObject to be copied from
     * @param uid - Key value of the ModelObject to be copied
     * @return - Constructed ModelObject
     * Requirement(Modelkit 1.1)
     */
    public static ModelObject GetModelObj(JSONObject obj, String uid){
        ModelObject thisModel;
        try{
            thisModel = (ModelObject)obj.get(uid);
        }catch(JSONException | ClassCastException e){
            try {
                thisModel = new ModelObject((JSONObject) obj.get(uid));
            }catch(JSONException | ClassCastException d){
               try{
                   thisModel = new ModelObject(((JSONObject)obj.get(uid)).toString());
               }catch(JSONException | ClassCastException c){
                    return null;
               }
            }
        }
        return thisModel;
    }

    /**
     * Gets the children JSONObject from this ModelObject. ModelObjects store children models by their pluralized names
     * i.e. (Pages). This key is searched for a the resulting JSONObject is returned
     * @param Pluralized - pluralized Model class key
     * @return - JSONObject for child element
     * Requirement(Modelkit 1.1)
     */
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

    /**
     * Pluralizes this models name
     * @return
     */
    public String plural(){
        return pluralize(getModelName());
    }

    /**
     * Utility function for pluralizing names
     * @param str - String to be pluralized
     * @return - pluralized string
     * Requirement(Modelkit 1.1)
     */
    public String pluralize(String str){
        int index = str.lastIndexOf(str);
        char c = str.charAt(index);
        if (c != 's'){
            return str + "s";
        }
        return str;
    }

    /**
     * Adds a model by its UID internally to this model, by replacing or updating existing JSONObject Keys
     * @param m - ModelObject to be added to this model
     * Requirement(Modelkit 1.1)
     */
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

    /**
     * Setter for UID
     * @param UID - UID
     * Requirement(Modelkit 1.1)
     */
    public void setUID(String UID) {
        this.UID = UID;
        updateKey("UID", UID);
    }

    /**
     * Gets UID Key
     * @return ModelObject UID
     * Requirement(Modelkit 1.1)
     */
    public String getUID() {
        return UID;
    }

    /**
     * Sets Name
     * @param name - ModelObject Name
     * Requirement(Modelkit 1.1)
     */
    public void setName(String name) {
        Name = name;
        updateKey("Name", Name);
    }

    /**
     * Gets Name
     * @return ModelObject name
     * Requirement(Modelkit 1.1)
     */
    public String getName() {
        return Name;
    }


    /**
     * Getter ModelName
     * @return ModelObject ClassName
     * Requirement(Modelkit 1.1)
     */
    public String getModelName() {
        return ClassName;
    }

    /**
     * Sets ModelName
     * @param className - ModelObject ClassName
     * Requirement(Modelkit 1.1)
     */
    public void setModelName(String className) {
        ClassName = className;
        updateKey("ClassName", ClassName);
    }

    /**
     * Returns the JSONObject super cast of this object
     * @return JSONObject cast of this object
     * Requirement(Modelkit 1.2)
     */
    public JSONObject getJson() {
        return (JSONObject)this;
    }

    /**
     * Updates internal JSONObject key value pair
     * @param key - Key to be updated
     * @param value - Value to be included
     * Requirement(Modelkit 1.1)
     */
    public void updateKey(String key, Object value) {
        remove(key);
        put(key, value);
    }

    /**
     * Removes key from ModelObject
     * @param key - Key to be removed
     * Requirement(Modelkit 1.1)
     */
    public void removeKey(String key){
        remove(key);
    }

    /**
     * Gets internal ModelObject held by this, returns null if not found
     * @param mClass - Class of internal object
     * @param uid - UID of internal object
     * @return Found ModelObject or null if not found
     * Requirement(Modelkit 1.3)
     */
    public ModelObject getModel(String mClass, String uid) {
        JSONObject internal = getChildren(pluralize(mClass));
        if(internal == null){
            return null;
        }
        ModelObject mObj = ModelObject.GetModelObj(internal, uid);
        if(mObj == null){
            return null;
        }
        return mObj;

    }

    /**
     * Finds a removes a specified UID with associated ClassName from this object
     * @param mClass class of internal object
     * @param uid UID of internal object
     * @return True/False if model was removed
     * Requirement(Modelkit 1.1)
     */
    public boolean Remove(String mClass, String uid){
        String collection = pluralize(mClass);
        JSONObject internal = getChildren(collection);

        if(! internal.has(uid)){
            return false;
        }else{
            internal.remove(uid);
        }

        return true;
    }

    /**
     * Gets JSONObject of internally held models of a specific class type
     * @param mClass internal class held by ModelObject
     * @return JSONObject holding stored models
     * Requirement(Modelkit 1.3)
     */
    public JSONObject getModels(String mClass) {
      return getChildren(pluralize(mClass));
    }

    /**
     * Gets JSON string of ModelObject
     * @return JSON string representation of ModelObject
     */
    public String toString() {
        return super.toString();
    }

    /**
     * Gets JSON string of ModelObject
     * @return JSON string representation of ModelObject
     */
    public String toJson() {
        return super.toString();
    }
    /**
     * Node key name of this ModelObject, which is just pluralized class name
     * @return Pluralized classname
     */
    public String getNode(){
        return pluralize(ClassName);
    }

    /**
     * Returns keyed field of the ModelObject
     * @return
     * @deprecated
     */
    public String getKey(){
        return "UID";
    }

    /**
     * Form builder for model object in HTML
     * @return
     */
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

    /**
     * Input tag builder for ModelObject
     * @param key key value of input tag
     * @return
     */
    public String GetInputTag(String key){
        return "<input type ='text' value='" + get(key) + " id='" + key + "' name='" + key + "' ></input>";
    }



}
