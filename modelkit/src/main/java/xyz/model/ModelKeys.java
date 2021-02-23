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
        keySet.add("Group");
        keySet.add("User");
        keySet.add("Notification");
        keySet.add("Theme");
        keySet.add("Edge");
        keySet.add("Site");
        keySet.add("Template");
        keySet.add("FileMap");
        return keySet;
    }

    public static ModelObject Default(String name){
        if(name.equals("Group")){
            return new Group("", "", 5);
        }
        if(name.equals("User")){
            User myUser = new User("", "","","","");
            myUser.setPassword("");
            return myUser;
        }
        if(name.equals("Notification")){
            return new Notification("","","","","","");
        }
        if(name.equals("FileMap")){
            return new FileMap("", "");
        }
        if(name.equals("Site")){
            return new Site("","","", "");
        }
        if(name.equals("Template")){
            return new Template("");
        }
        if(name.equals("Theme")){
            return new Theme("","");
        }
        if(name.equals("Edge")){
            return new Edge("","","","", "");
        }

        return null;
    }

    public static String pluralize(String str){
        int index = str.lastIndexOf(str);
        char c = str.charAt(index);
        if (c != 's'){
            return str + "s";
        }
        return str;
    }


}
