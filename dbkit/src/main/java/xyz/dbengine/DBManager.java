package xyz.dbengine;
import org.json.JSONObject;


public interface DBManager extends Runnable {

    String getName();
    boolean passCredentials();

    //Database API
    boolean CreateNode(String nodeName, String nodeFilePath);
    boolean DeleteNode(String nodeName);
    boolean ActiveNode(String nodeName);

    //Submit Query - Thread Based Query Submittal
    String Submit(String nodeName, JSONObject request);


}


