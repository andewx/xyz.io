package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;






public final class ModelKeys { //Static Model Methods

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

    public static ModelObject decode(String jsonString){
        return new ModelObject(jsonString);
    }

    public static ArrayList<String> ModelKeys(){
        //Add all model types to this set
        ArrayList<String> keySet = new ArrayList<String>();
        keySet.add("Item");
        return keySet;
    }


}
