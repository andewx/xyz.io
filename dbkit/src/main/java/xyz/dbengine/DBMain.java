package xyz.dbengine;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.PriorityQueue;

public class DBMain implements DBManager{

    //DBMains run in their own thread to prevent I/O Blocking - New requests should spawn new threads

    HashMap<String,DBNode> nodes;
    private String authCred;
    private String name;
    PriorityQueue<JSONObject> requests;
    HashMap<String,JSONObject> responses;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean passCredentials() {
        return false;
    }

    @Override
    public boolean CreateNode(String nodeName, String nodeFilePath) {
        return false;
    }

    @Override
    public boolean DeleteNode(String nodeName) {
        return false;
    }

    @Override
    public boolean ActiveNode(String nodeName) {
        return false;
    }

    @Override
    public String Submit(String nodeName, JSONObject request) {
        return null; //Submits requests for DB modification spawns thread and returns a repository item ID;
    }

    @Override
    public void run() {
        //syncs database nodes with file outputs based on sync/modify id in DBNode
    }
}
