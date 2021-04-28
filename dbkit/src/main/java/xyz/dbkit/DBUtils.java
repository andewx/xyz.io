package xyz.dbkit;

import xyz.model.ModelKeys;

import java.io.IOException;

/**
 * DBUtils file for Database algorithms includes similarity and distance algorithms for string queries
 */
public final class DBUtils {

    /**
     * Instantiates a new DBNode based on the ModelObject Class Name as a pluralized Class identifier
     * @param ModelName Class of ModelObject
     * @return Instantiated DBNode
     */
    public static DBNode InitNode(String ModelName){
        String Name = ModelKeys.pluralize(ModelName);
        String srcPath = "resources/";
        String modelPath = srcPath + Name + ".keys";
        try {
            return DBNode.CreateDBNode(Name, modelPath);
        }catch(IOException e){
            return null; //File Exists
        }
    }

    /**
     * Similiarity of string distance between two strings
     * @param s1 String 1
     * @param s2 String 2
     * @return Heuristic value of string similarity returned between 0.0 - 1.0
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    /**
     * Calculates heuristic of distance between two strings using a combination of string lengths and
     * char code similarity
     * @param s1 String 1
     * @param s2 String 2
     * @return Heuristic distance between to strings as integer
     */
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
