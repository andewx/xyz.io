package xyz.dbkit;
import xyz.model.ModelObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import xyz.dbkit.DBNode;


public interface DBManager extends Runnable {

    //Database API
    DBNode CreateNode(String nodeName, String nodeFilePath) throws IOException;
    DBNode CreateNode(String nodeName, String nodeFilePath, String json);
    boolean DeleteNode(String nodeName);
    boolean ActiveNode(String nodeName);
    DBNode GetNode(String nodeName);

    //Repository actions
    ModelObject addModel(DBNode node, ModelObject m);
    ModelObject updateModel(DBNode node, ModelObject m);

    //Query Operations -- Matches exact all properties
    ArrayList<ModelObject> findExact(ModelObject model, String ClassName, HashMap<String,String> PropertyKeyValues);
    ArrayList<ModelObject> findExact(DBNode node, String ClassName, HashMap<String,String> PropertyKeyValues);

    //Query Operations -- Matches exact some properties
    ArrayList<ModelObject> findSome(ModelObject model, String ClassName, HashMap<String,String> PropertyKeyValues);
    ArrayList<ModelObject> findSome(DBNode node, String ClassName, HashMap<String,String> PropertyKeyValues);

    //Query Operations -- Find similar some property
    ArrayList<ModelObject> findSimilar(ModelObject model, String ClassName, String property, String value);
    ArrayList<ModelObject> findSimilar(DBNode node, String ClassName, String property, String value);

    //Query Operations -- Find By Key
    ModelObject findKey(ModelObject model, String ClassName, String key);
    ModelObject findKey(DBNode node, String ClassName, String key);

    //Query Operations -- Delete By Key
    boolean deleteKey(ModelObject model, String ClassName, String key);
    boolean deleteKey(DBNode node, String ClassName, String key);

    //Database I/O Sync Operations - Runnable
    String SyncNode(DBNode thisNode) throws IOException;
    String SyncNotifications();
    String Sync() throws IOException;
    void Exit();

    //Logging Operations
    String toString();


}


