package xyz.dbkit;

import xyz.model.FileMap;
import xyz.model.ModelIterator;
import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class DBMain implements DBManager{

    HashMap<String,DBNode> Nodes;
    DBNode ActiveNode;
    ModelObject NodeFileManager;
    String Name;
    boolean ExitCondition;

    public DBMain(String name) throws IOException {
        //Checks if node property files exists -- stored as .keys json files
        Name = name;
        String nodeFile = "node.keys";

        //File Stuff
        Path dbPath = Path.of(nodeFile);
        boolean exists = Files.exists(dbPath);
        if(exists){
            String jsonText = Files.readString(dbPath);
            NodeFileManager = new ModelObject(jsonText);
            ModelIterator myIter = new ModelIterator(NodeFileManager);
            while(myIter.hasNext()){ //Read in database
                ModelObject fileMapping = myIter.next();
                String nodeName = fileMapping.getName();
                Path nodePath = Path.of((String)fileMapping.get("FilePath"));
                String text = Files.readString(nodePath);
                DBNode nNode = CreateNode(nodeName, nodePath.toString(), text);
                ActiveNode = nNode;
                Nodes.put(nodeName, ActiveNode);
            }
            Files.writeString(dbPath, NodeFileManager.toString());
        }else{
            Files.createFile(dbPath);
            ModelObject NodeFileManager = new ModelObject();
            NodeFileManager.Name = "root";
            DBNode defaultNode = CreateNode("default", "default.keys");
            ActiveNode = defaultNode;
            Nodes.put(ActiveNode.name, ActiveNode);
            FileMap nFileMap = new FileMap(defaultNode.name, defaultNode.file);
            Files.writeString(dbPath, NodeFileManager.toString());
        }

    }

    @Override
    public DBNode CreateNode(String nodeName, String nodeFilePath) throws IOException {
        DBNode myNode = new DBNode(nodeName, nodeFilePath);
        FileMap nMap = new FileMap(nodeName, nodeFilePath);
        NodeFileManager.addModel(nMap);
        Path myPath = Path.of(myNode.file);
        boolean exists =  Files.exists(myPath);
        if(exists){
            Files.writeString(myPath, myNode.rootGraph.toString());
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
        Path myPath = Path.of(delNode.file);
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
        ModelIterator myIter = new ModelIterator(model);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
              ArrayList<ModelObject> Append = findExact(nextModel, ClassName, PropertyKeyValues);
              myMatches.addAll(Append);
            }else{
                boolean match = true;
                for(String Prop : PropertyKeyValues.keySet()){
                    String propVal = PropertyKeyValues.get(Prop);
                    if(propVal.compareTo((String)nextModel.get(Prop)) != 0){
                        match = false;
                    }
                }
                if(match){
                    myMatches.add(nextModel);
                }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findExact(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelIterator myIter = new ModelIterator(node.rootGraph);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ArrayList<ModelObject> Append = findExact(nextModel, ClassName, PropertyKeyValues);
                myMatches.addAll(Append);
            }else{
                boolean match = true;
                for(String Prop : PropertyKeyValues.keySet()){
                    String propVal = PropertyKeyValues.get(Prop);
                    if(propVal.compareTo((String)nextModel.get(Prop)) != 0){
                        match = false;
                    }
                }
                if(match){
                    myMatches.add(nextModel);
                }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSome(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelIterator myIter = new ModelIterator(model);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ArrayList<ModelObject> Append = findSome(nextModel, ClassName, PropertyKeyValues);
                myMatches.addAll(Append);
            }else{
                for(String Prop : PropertyKeyValues.keySet()){
                    String propVal = PropertyKeyValues.get(Prop);
                    if(propVal.compareTo((String)nextModel.get(Prop)) == 0){
                        myMatches.add(nextModel);
                        break;
                    }
                }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSome(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelIterator myIter = new ModelIterator(node.rootGraph);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ArrayList<ModelObject> Append = findSome(nextModel, ClassName, PropertyKeyValues);
                myMatches.addAll(Append);
            }else{
                for(String Prop : PropertyKeyValues.keySet()){
                    String propVal = PropertyKeyValues.get(Prop);
                    if(propVal.compareTo((String)nextModel.get(Prop)) == 0){
                        myMatches.add(nextModel);
                        break;
                    }
                }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSimilar(ModelObject model, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelIterator myIter = new ModelIterator(model);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ArrayList<ModelObject> Append = findSimilar(nextModel, ClassName, property, value);
                myMatches.addAll(Append);
            }else{
                    int diff = value.compareTo((String)nextModel.get(property));
                    diff = Math.abs(diff); //Problematic need some distance metric for similarity
                    if(diff <=  5){ //Weight
                        myMatches.add(nextModel);
                    }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSimilar(DBNode node, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<ModelObject>();
        ModelIterator myIter = new ModelIterator(node.rootGraph);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ArrayList<ModelObject> Append = findSimilar(nextModel, ClassName, property, value);
                myMatches.addAll(Append);
            }else{
                int diff = value.compareTo((String)nextModel.get(property));
                diff = Math.abs(diff); //Problematic need some distance metric for similarity
                if(diff <=  5){ //Weight
                    myMatches.add(nextModel);
                }
            }
        }
        return myMatches;
    }

    @Override
    public ModelObject findKey(ModelObject model, String ClassName, String key) {
        ModelIterator myIter = new ModelIterator(model);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ModelObject myModel = findKey(nextModel, ClassName,key);
                if(myModel != null){
                    return myModel;
                }
            }else{
              ModelObject myModel = (ModelObject)nextModel.get(key);
              if(myModel != null) {
                  return myModel;
              }
            }
        }
        return null;
    }

    @Override
    public ModelObject findKey(DBNode node, String ClassName, String key) {
        ModelIterator myIter = new ModelIterator(node.rootGraph);
        while(myIter.hasNext()){
            ModelObject nextModel = myIter.next();
            String myClass = nextModel.getModelName();
            if(myClass.compareTo(ClassName) != 0){ //Search Inner
                ModelObject myModel = findKey(nextModel, ClassName,key);
                if(myModel != null){
                    return myModel;
                }
            }else{
                ModelObject myModel = (ModelObject)nextModel.get(key);
                if(myModel != null) {
                    return myModel;
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteKey(ModelObject model, String ClassName, String key) {

        HashMap<String, ModelObject> myMap =  model.getModels(ClassName);

        if(myMap == null){
            for(String keyName : model.Children.keySet()){
                HashMap<String, ModelObject> modelHeader = model.Children.get(keyName);
                for(String innerKey : modelHeader.keySet()){
                    ModelObject innerObject = modelHeader.get(innerKey);
                    if(deleteKey(innerObject, ClassName, key)){
                        return true;
                    }
                }
            }
        }else{
            ModelObject myModel = myMap.get(key);
            if(myModel != null){
                myMap.remove(key);
                model.Modified = Instant.now().toString();
                model.update();
                return true;
            }
        }


        return false;
    }

    @Override
    public boolean deleteKey(DBNode node, String ClassName, String key) {

        HashMap<String, ModelObject> myMap =  node.rootGraph.getModels(ClassName);

        if(myMap == null){
            for(String keyName : node.rootGraph.Children.keySet()){
                HashMap<String, ModelObject> modelHeader = node.rootGraph.Children.get(keyName);
                for(String innerKey : modelHeader.keySet()){
                    ModelObject innerObject = modelHeader.get(innerKey);
                    if(deleteKey(innerObject, ClassName, key)){
                        return true;
                    }
                }
            }
        }else{
            ModelObject myModel = myMap.get(key);
            if(myModel != null){
                myMap.remove(key);
                node.rootGraph.Modified = Instant.now().toString();
                node.rootGraph.update();
                return true;
            }
        }


        return false;
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
