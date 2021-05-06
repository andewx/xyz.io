package xyz.model;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;


/**
 * ModelKeys is a static final class used by the database and other modules for a representation of the collection of
 * model objects available. Also provides utlility functions such as <code>genUID()</code> which will
 */
public final class ModelKeys { //Static Model Methods

    /**
     * Concatenates a random 512 bit hash code for UID usage. ModelObjects default behavior is to use this UID
     * @return
     * Requirement(Modelkit 1.4)
     */
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

    /**
     * Decodes a JSONString into a ModelObject as a utlity tool - note this will only fill the values associated with base model objects
     * @param jsonString - ModelObject JSON String
     * @return
     * Requirement(Modelkit 1.2)
     */
    public static ModelObject decode(String jsonString){
        return new ModelObject(jsonString);
    }

    /**
     * Gets the list of ModelKeys available from this Model module. If adding models to the framework you
     * must add your Model name here to the <code>keySet</code> for the database to recognize that your model
     * exists.
     * @return
     * Requirement(Modelkit 2.0)
     */
    public static ArrayList<String> ModelKeys(){
        //Add all model types to this set
        ArrayList<String> keySet = new ArrayList<String>();
        keySet.add("Group");
        keySet.add("User");
        keySet.add("Theme");
        keySet.add("Edge");
        keySet.add("Site");
        keySet.add("Page");
        return keySet;
    }

    /**
     * Returns default model objects for each type of Model. If you add a model to the Model package you must
     * add in a default utility constructor here for the framework to run
     * @param name
     * @return
     * Requirement(Modelkit 2.1)
     */
    public static ModelObject Default(String name){
        if(name.equals("Group")){
            return new Group("", "", 5);
        }
        if(name.equals("User")){
            User myUser = new User("", "","","","");
            myUser.setPassword("");
            return myUser;
        }
        if(name.equals("Site")){
            return new Site("","","", "");
        }
        if(name.equals("Theme")){
            return new Theme("","", "");
        }
        if(name.equals("Edge")){
            return new Edge("","","","", "");
        }
        if(name.equals("Page")){
            return new Page("", "", "");
        }

        return null;
    }

    /**
     * Utility method for consistent pluralization of Model names. Used for example by the DBNode package
     * @param str - the string to be pluralized
     * Requirement(Modelkit 3.0)
     */
    public static String pluralize(String str){
        int index = str.lastIndexOf(str);
        char c = str.charAt(index);
        if (c != 's'){
            return str + "s";
        }
        return str;
    }


}
