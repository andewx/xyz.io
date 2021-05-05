package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ModelObject Edge Relation Object for mapping the Model/Key Pair A -> Model/Key Pair B
 * Additionally a relationship type may be specified, this Model is a utility object for those wishing
 * to represent data stores appropriate for Graph Databases
 * @author briananderson
 * @version 1.0
 * @see xyz.model.ModelObject
 */
public class Edge extends ModelObject{

    protected String ModelA;
    protected String KeyA;
    protected String ModelB;
    protected String KeyB;
    protected String Relation;


    /**
     * Edge Constructor
     * @param modelA the owning object class of the relation
     * @param keyA the owning object UID Ke
     * @param modelB the subject class of the relation
     * @param keyB the subject object UID Key
     * @param relation  Any string user wishes to define relation with
     */
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

    /**
     * Edge Constructor -- constructs object from JSON formatted string
     * @param json a well formatted JSON string that maps to an Edge Object and contains all required keys
     */
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

    /**
     * Edge Constructor -- constructs Edge object from loaded JSONObject
     * @param jObj - loaded JSONObject with all required keys for an Edge object
     */
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

    /**
     * ModelA Getter
     * @return returns ModelA from this object
     */
    public String getModelA() {
        return ModelA;
    }

    /**
     * ModelA Setter
     * @param modelA the model class of the owning object model
     */
    public void setModelA(String modelA) {
        ModelA = modelA;
        updateKey("ModelA", ModelA);
    }

    /**
     * KeyA Getter
     * @return the UID Key for object A in the relation
     */
    public String getKeyA() {
        return KeyA;
    }

    /**
     * KeyA Setter
     * @param keyA the UID Key for Model Object A
     */
    public void setKeyA(String keyA) {
        KeyA = keyA;
        updateKey("KeyA", KeyA);
    }

    /**
     * Gets model class for Object B in relation
     * @return ModelB class name
     */
    public String getModelB() {
        return ModelB;
    }

    /**
     * ModelB Setter
     * @param modelB the ModelB class
     */
    public void setModelB(String modelB) {
        ModelB = modelB;
        updateKey("ModelB", ModelB);
    }

    /**
     * KeyB Getter
     * @return - Gets the UID Key for Model B
     */
    public String getKeyB() {
        return KeyB;
    }

    /**
     * ModelB Key Setter
     * @param keyB ModelB UID Key
     */
    public void setKeyB(String keyB) {
        KeyB = keyB;
        updateKey("KeyB", KeyB);
    }

    /**
     * Relation Getter
     * @return the Relation string specifier
     */
    public String getRelation() {
        return Relation;
    }

    /**
     * Relation Setter
     * @param relation sets the Edge relationship string
     */
    public void setRelation(String relation) {
        Relation = relation;
        updateKey("Relation", Relation);
    }

}
