package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Edge extends ModelObject{
    protected String ModelA;
    protected String KeyA;
    protected String ModelB;
    protected String KeyB;
    protected String Relation;


    public Edge(String modelA, String keyA, String modelB, String keyB, String relation){
        super();
        ModelA = modelA;
        ModelB = modelB;
        KeyA = keyA;
        KeyB = keyB;
        Relation = relation;
        ClassName = "Edge";
        Name = "";
        updateKey("ClassName", ClassName);
        put("ModelA", ModelA);
        put("ModelB", ModelB);
        put("KeyA", KeyA);
        put("KeyB", KeyB);
        put("Relation", Relation);
    }
    public Edge(String json){
        super(json);
        ModelA = (String)get("ModelA");
        ModelB = (String)get("ModelB");
        KeyA = (String)get("KeyA");
        KeyB = (String)get("KeyB");
        Relation = (String)get("Relation");
        put("ModelA", ModelA);
        put("ModelB", ModelB);
        put("KeyA", KeyA);
        put("KeyB", KeyB);
        put("Relation", Relation);
    }

    public Edge(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            ModelA = (String)jObj.get("ModelA");
            ModelB = (String)jObj.get("ModelB");
            KeyA = (String)jObj.get("KeyA");
            KeyB = (String)jObj.get("KeyB");
            Relation = (String)jObj.get("Relation");
            put("ModelA", ModelA);
            put("ModelB", ModelB);
            put("KeyA", KeyA);
            put("KeyB", KeyB);
            put("Relation", Relation);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getModelA() {
        return ModelA;
    }

    public void setModelA(String modelA) {
        ModelA = modelA;
        updateKey("ModelA", ModelA);
    }

    public String getKeyA() {
        return KeyA;
    }

    public void setKeyA(String keyA) {
        KeyA = keyA;
        updateKey("KeyA", KeyA);
    }

    public String getModelB() {
        return ModelB;
    }

    public void setModelB(String modelB) {
        ModelB = modelB;
        updateKey("ModelB", ModelB);
    }

    public String getKeyB() {
        return KeyB;
    }

    public void setKeyB(String keyB) {
        KeyB = keyB;
        updateKey("KeyB", KeyB);
    }

    public String getRelation() {
        return Relation;
    }

    public void setRelation(String relation) {
        Relation = relation;
        updateKey("Relation", Relation);
    }

}
