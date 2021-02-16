package xyz.dbkit;

import org.json.JSONObject;
import xyz.model.FileMap;

import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class DBMain extends Thread implements DBManager{

    HashMap<String,DBNode> Nodes;
    DBNode ActiveNode;
    ModelObject NodeFileManager;
    String Name;
    boolean ExitCondition;
    String srcPath;

    public DBMain(String name) throws IOException {
        //Checks if node property files exists -- stored as .keys json files
        Name = name;
        Nodes = new HashMap<>();
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
                            Path nodePath = Path.of((String) fileMapping.get("FilePath"));
                            String text = Files.readString(nodePath);
                            ActiveNode = CreateNode(nodeName, nodePath.toString(), text);
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
            DBNode myNode = new DBNode("default", srcPath + "default.keys");
            FileMap nMap = new FileMap("default", srcPath + "node.keys");
            Nodes.put(myNode.name, myNode);
            ActiveNode = myNode;
            NodeFileManager.addModel(nMap);
            NodeFileManager.update();
            Path myPath = Path.of(nodeFile);
            myNode.WriteNode(myNode.file);
            Files.writeString(myPath, NodeFileManager.toString());
        }

    }

    public void WriteFileMap() throws IOException {
        NodeFileManager.update();
        Path file = Path.of(srcPath + "node.keys");
        boolean exists = Files.exists(file);
        if(!exists){
            Files.createFile(file);
        }

        Files.writeString(file, NodeFileManager.toString());
    }


    @Override
    public DBNode CreateNode(String nodeName, String nodeFilePath) throws IOException {
        DBNode myNode = new DBNode(nodeName, srcPath + nodeFilePath);
        FileMap nMap = new FileMap(nodeName, srcPath + nodeFilePath);
        NodeFileManager.addModel(nMap);
        Path myPath = Path.of(myNode.file);
        boolean exists =  Files.exists(myPath);
        if(exists){
            myNode.rootGraph = new ModelObject(Files.readString(myPath));
        }else{
            Files.createFile(myPath);
            Files.writeString(myPath, myNode.rootGraph.toString());
        }

        Nodes.put(nodeName, myNode);
        WriteFileMap();
        return myNode;
    }

    @Override
    public DBNode CreateNode(String nodeName, String nodeFilePath, String json) throws IOException {
        DBNode myNode = new DBNode(nodeName, nodeFilePath);
        FileMap nMap = new FileMap(nodeName, nodeFilePath);
        NodeFileManager.addModel(nMap);
        WriteFileMap();
        return myNode;
    }

    @Override
    public boolean DeleteNode(String nodeName) throws IOException {
        DBNode delNode = Nodes.get(nodeName);
        if(delNode == null){
            return false;
        }
        //Delete file;
        Path myPath = Path.of(delNode.file);
        boolean exists = Files.exists(myPath);

        if(!exists){
            return false;
        }

        try {
            Files.delete(myPath);
            Nodes.remove(delNode.name);
            NodeFileManager.Remove("FileMap",delNode.name);
        }catch(IOException e) {
            return false;
        }
        WriteFileMap();

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

    public ModelObject GetFileManager(){
        return NodeFileManager;
    }

    @Override
    public DBNode GetNode(String nodeName) {
        return Nodes.get(nodeName);
    }

    @Override
    public ModelObject AddModel(DBNode node, ModelObject m) {
        node.rootGraph.addModel(m);
        node.hasChanged = true;
        return m;
    }

    @Override
    public ModelObject UpdateModel(DBNode node, ModelObject m) {
        m.update();
        node.hasChanged = true;
        return m;
    }

    //--------------------------------- QUERY METHODS ---------------------------------//

    @Override
    public ArrayList<ModelObject> findExact(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        ArrayList<ModelObject> myMatches = new ArrayList<>();
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
        if (myMap == null) return false;
        ModelObject value = (ModelObject)myMap.remove(key);
        return value != null;
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
            thisNode.WriteNode(srcPath + thisNode.GetFile());
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
        System.out.println("\nDatabase: " + Name + " starting...");

        while (!ExitCondition) {

            try{ this.sleep(4000);
             this.SyncNotifications();
             System.out.println("Syncing...\n");
             this.Sync();
            }
            catch(IOException | InterruptedException e){
                    System.out.println("Database File Error Exiting Sync Thread ...");
                    e.printStackTrace();
                    ExitCondition = true;
            }

        }
    }

    @Override
    public void Exit() {
        ExitCondition = true;
        this.notifyAll();
    }
}
