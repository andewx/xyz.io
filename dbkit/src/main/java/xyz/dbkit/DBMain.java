package xyz.dbkit;

import org.json.JSONObject;
import xyz.model.FileMap;
import xyz.model.ModelIterator;
import xyz.model.ModelKeys;
import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBMain implements DBManager{

    HashMap<String,DBNode> Nodes;
    DBNode ActiveNode;
    ModelObject NodeFileManager;
    String Name;
    boolean ExitCondition;
    String srcPath;

    public DBMain(String name) throws IOException {
        //Checks if node property files exists -- stored as .keys json files
        Name = name;
        Nodes = new HashMap<String, DBNode>();
        srcPath = "resources/";
        String nodeFile = srcPath + "node.keys";

        //File Stuff
        Path dbPath = Path.of(nodeFile);
        boolean exists = Files.exists(dbPath);
        boolean hasKeys = false;

        if(exists){
            String jsonText = Files.readString(dbPath);
            if(jsonText.compareTo("") != 0) {
                hasKeys = true;
                NodeFileManager = new ModelObject(jsonText);
                JSONObject MapNodes = NodeFileManager.getChildren("FileMaps");
                if (MapNodes != null) {
                    for (String key : MapNodes.keySet()) {
                        JSONObject fileMapping = (JSONObject) MapNodes.get(key);
                        if (fileMapping != null) {
                            String nodeName = (String)fileMapping.get("Name");
                            Path nodePath = Path.of(srcPath + (String) fileMapping.get("FilePath"));
                            String text = Files.readString(nodePath);
                            DBNode nNode = CreateNode(nodeName, nodePath.toString(), text);
                            ActiveNode = nNode;
                            Nodes.put(nodeName, ActiveNode);
                        }
                    }
                }
            }
        }

        if(!exists){
            Files.createFile(dbPath);
        }

        if(!hasKeys){
            NodeFileManager = new ModelObject();
            NodeFileManager.Name = "NodeFiles";
            NodeFileManager.update();
            DBNode myNode = new DBNode("default", "node.keys");
            FileMap nMap = new FileMap("default", "node.keys");
            Nodes.put(myNode.name, myNode);
            ActiveNode = myNode;
            NodeFileManager.addModel(nMap);
            NodeFileManager.update();
            Path myPath = Path.of(nodeFile);
            Files.writeString(myPath, NodeFileManager.toString());
        }

    }


    @Override
    public DBNode CreateNode(String nodeName, String nodeFilePath) throws IOException {
        DBNode myNode = new DBNode(nodeName, nodeFilePath);
        FileMap nMap = new FileMap(nodeName, nodeFilePath);
        NodeFileManager.addModel(nMap);
        Path myPath = Path.of(srcPath + myNode.file);
        boolean exists =  Files.exists(myPath);
        if(exists){
            Files.writeString( myPath, myNode.rootGraph.toString());
        }else{
            Files.createFile(myPath);
            Files.writeString(myPath, myNode.rootGraph.toString());
        }
        return myNode;
    }

    @Override
    public DBNode CreateNode(String nodeName, String nodeFilePath, String json) {
        DBNode myNode = new DBNode(nodeName, nodeFilePath);
        FileMap nMap = new FileMap(nodeName, nodeFilePath);
        NodeFileManager.addModel(nMap);
        return myNode;
    }

    @Override
    public boolean DeleteNode(String nodeName) {
        DBNode delNode = Nodes.get(nodeName);
        if(delNode == null){
            return false;
        }
        //Delete file;
        Path myPath = Path.of(srcPath + delNode.file);
        boolean exists = Files.exists(myPath);

        if(!exists){
            return false;
        }

        try {
            Files.delete(myPath);
            Nodes.remove(delNode.name);
        }catch(IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean ActiveNode(String nodeName) {
        DBNode aNode = Nodes.get(nodeName);
        if(aNode == null){
            return false;
        }
        ActiveNode = aNode;
        return true;
    }

    @Override
    public DBNode GetNode(String nodeName) {
        return Nodes.get(nodeName);
    }

    @Override
    public ModelObject addModel(DBNode node, ModelObject m) {
        String className = m.getModelName();
        node.rootGraph.addModel(m);
        node.hasChanged = true;
        return m;
    }

    @Override
    public ModelObject updateModel(DBNode node, ModelObject m) {
        m.update();
        node.hasChanged = true;
        return m;
    }

    //--------------------------------- QUERY METHODS ---------------------------------//

    @Override
    public ArrayList<ModelObject> findExact(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)model.get(ClassName);
        boolean matches = true;
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
                for(String mapKey : PropertyKeyValues.keySet()){
                    String cVal = myModel.get(mapKey).toString();
                    if(cVal.compareTo(myMap.get(mapKey).toString())!=0){
                        matches = false;
                        break;
                    }
                }
            }
            if (matches){
                myMatches.add(myModel);
            }
        }

        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findExact(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)node.rootGraph.get(ClassName);
        boolean matches = true;
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
                for(String mapKey : PropertyKeyValues.keySet()){
                    String cVal = myModel.get(mapKey).toString();
                    if(cVal.compareTo(myMap.get(mapKey).toString())!=0){
                        matches = false;
                        break;
                    }
                }
            }
            if (matches){
                myMatches.add(myModel);
            }
        }

        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSome(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)model.get(ClassName);
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
               for(String mapKey : PropertyKeyValues.keySet()){
                   String cVal = myModel.get(mapKey).toString();
                   if(cVal.compareTo(myMap.get(mapKey).toString())==0){
                       myMatches.add(myModel);
                   }
               }
            }
        }

        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSome(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)node.rootGraph.get(ClassName);
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
                for(String mapKey : PropertyKeyValues.keySet()){
                    String cVal = myModel.get(mapKey).toString();
                    if(cVal.compareTo(myMap.get(mapKey).toString())==0){
                        myMatches.add(myModel);
                    }
                }
            }
        }

        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSimilar(ModelObject model, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)model.get(ClassName);
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
                String cVal = myModel.get(property).toString();
                int diff = Math.abs(cVal.compareTo(value));
                if( diff < 5){
                    myMatches.add(myModel);
                }
            }
        }

        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSimilar(DBNode node, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelObject myMap = (ModelObject)node.rootGraph.get(ClassName);
        for(String key : myMap.keySet()){
            ModelObject myModel = (ModelObject)myMap.get(key);
            if(key != null){
                String cVal = myModel.get(property).toString();
                int diff = Math.abs(cVal.compareTo(value));
                if( diff < 5){
                    myMatches.add(myModel);
                }
            }
        }

        return myMatches;
    }

    @Override
    public ModelObject findKey(ModelObject model, String ClassName, String key) {
        JSONObject myMap =  model.getModels(ClassName);
        if (myMap == null){
            return null;
        }
        return (ModelObject)myMap.get(key);
    }

    @Override
    public ModelObject findKey(DBNode node, String ClassName, String key) {
        JSONObject myMap =  node.rootGraph.getModels(ClassName);
        if (myMap == null){
            return null;
        }
        return (ModelObject)myMap.get(key);

    }

    @Override
    public boolean deleteKey(ModelObject model, String ClassName, String key) {

        JSONObject myMap =  model.getModels(ClassName);
        if (myMap == null){
            return false;
        }
        ModelObject value = (ModelObject)myMap.remove(key);
        if(value == null){
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteKey(DBNode node, String ClassName, String key) {

        JSONObject myMap =  node.rootGraph.getModels(ClassName);
        if(myMap == null){
            return false;
        }

        ModelObject value = (ModelObject)myMap.remove(key);
        if(value == null){
            return false;
        }

        return true;
    }

    @Override
    public String SyncNode(DBNode thisNode) throws IOException {

        if(thisNode.hasChanged) {
            Path myPath = Path.of(thisNode.file);
            boolean exists = Files.exists(myPath);
            if (!exists) {
                Files.createFile(myPath);
                Files.writeString(myPath, thisNode.rootGraph.toString());
            } else {
                Files.writeString(myPath, thisNode.rootGraph.toString());
            }
            thisNode.hasChanged = false;
            return "DBNode: " + thisNode.name + " Written\n";
        }
        return "DBNode: " + thisNode.name + " No Update\n";
    }

    @Override
    public synchronized String SyncNotifications() {
        return null; //No implementation
    }

    @Override
    public synchronized String Sync() throws IOException {
        for(String nodeKey : Nodes.keySet()){
            DBNode node = Nodes.get(nodeKey);
            SyncNode(node);
        }
        return "Nodes Synced\n";
    }



    @Override
    public void run() {
        while (!ExitCondition) {
            try {
                this.wait();
                this.SyncNotifications();
                try {
                    this.Sync();
                }catch(IOException e){
                    System.out.println("Database File Error Exiting Sync Thread ...");
                    e.printStackTrace();
                    ExitCondition = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void Exit() {
        ExitCondition = true;
        this.notifyAll();
    }
}
