package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;


public final class ModelHandler { //Static Model Methods

    public static String genUID(){
        //Generate sha256 Hash
        Instant seed = Instant.now();
        Random myRandom = new Random(seed.getEpochSecond());
        int[] intStore = new int[16];

        for(int i = 0; i < 16; i++) {
            int randInt = myRandom.nextInt();
            intStore[i] = randInt;
        }

        String hashString = intStore.toString();
        HashFunction f =  Hashing.murmur3_128();
        HashCode UID = f.hashString(hashString, Charset.defaultCharset());
        return UID.toString();
    }


    public static String toJSON(Model model) {
        JSONObject jPropMap = new JSONObject(model.getPropertyMap());
        return jPropMap.toString();
    }
    public static Model fromJSON(String jsonString) {
        JSONObject myObject = new JSONObject(jsonString);
        ModelBase myModel = new ModelBase();
        myModel.mPropertyMap = (HashMap)myObject.toMap();
        return myModel;
    }


}
