package xyz.model;

import java.util.LinkedHashMap;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;
import xyz.model.Item;


/**
 * <class>xyz.model.ModelFactory</class>
 * <descr></desc>Model Factory Method API. <p>Encode/Decode Class & Initialization class for models. Generates model
 * unique identifiers. Handles Model JSON String Encode Decode Operations. </p></descr>
 */
public final class ModelFactory {

    private Model _modelHandle; // Current Model Handle
    public String uID;
    private LinkedHashMap<String, Model> InstanceMap;


    public ModelFactory(){

        this.InstanceMap = new LinkedHashMap<String,Model>();

        //Create Item Factory Base Object
        Item defaultItem = new Item();
        this.InstanceMap.put("Item", defaultItem);

    }

    public String genUID(){
        //Generate sha256 Hash
        String initialVal = "1qaz2wsxXSW@ZAQ!3edc4rfv$RFV$RFV"; //Some Initial Base String
        HashFunction f =  Hashing.goodFastHash(128); //Seeded Hash Function
        this.uID = f.hashBytes(initialVal.getBytes()).toString();
        return this.uID;
    }

    public Model Build(String className){
        Model ObjInstance = InstanceMap.get(className);
        return ObjInstance.createModel(genUID());
    }

    public String toJSON(Model model) {return null;}
    public Model fromJSON(String jsonString){ return null; }


}
