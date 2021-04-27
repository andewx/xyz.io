package xyz.dbkit;

import org.json.JSONObject;
import xyz.model.ModelIterator;
import xyz.model.ModelKeys;
import xyz.model.ModelObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class DBMain extends Thread implements DBManager{

    HashMap<String,DBNode> Nodes;
    String Name;
    boolean ExitCondition;
    String SrcPath;

    public DBMain(String name) throws IOException {
        //Checks if node property files exists -- stored as .keys json files
        Name = name;
        Nodes = new HashMap<>();
        ExitCondition = false;
        SrcPath = "resources/";
        

        for(String key : ModelKeys.ModelKeys()){
            DBNode thisNode = DBUtils.InitNode(key);
            if(thisNode == null){
                throw new IOException("Model Node: " + key + " does not exist. Could not create DBNode object. Database creation failed!\n");
            }
            Nodes.put(ModelKeys.pluralize(key), thisNode);
        }

    }

    public String GetName(){
        return Name;
    }

    @Override
    public DBNode CreateNode(String nodeName) throws IOException {
            DBNode myNode = DBUtils.InitNode(nodeName);
            Nodes.put(ModelKeys.pluralize(nodeName), myNode);
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
            
        }catch(IOException e) {
            return false;
        }

        return true;
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
        
        node.hasChanged = true;
        return m;
    }

    //--------------------------------- QUERY METHODS ---------------------------------//

    @Override
    public ArrayList<ModelObject> findExact(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();
        JSONObject Models = model.getModels(ClassName);
        if(Models == null){
          ModelIterator mIter = new ModelIterator(model);
          while(mIter.hasNext()){
              ModelObject thisModel = mIter.next();
              ArrayList<ModelObject> myArray = findExact(thisModel,ClassName,PropertyKeyValues);
              myMatches.addAll(myArray);
          }
        }
        else {
            for (String uid : Models.keySet()) {
                ModelObject thisModel = ModelObject.GetModelObj(Models, uid);
                boolean matches = true;
                for(String prop : PropertyKeyValues.keySet()){
                    String value = (String)thisModel.get(prop);
                    if(value.compareTo((String)PropertyKeyValues.get(prop)) != 0){
                        matches = false;
                    }
                }
                if (matches){ myMatches.add(thisModel);}
            }
        }
        
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findExact(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
      return findExact(node.rootGraph, ClassName, PropertyKeyValues);
    }

    @Override
    public ArrayList<ModelObject> findSome(ModelObject model, String ClassName, HashMap<String, String> PropertyKeyValues) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();

        JSONObject Models = model.getModels(ClassName);
        if(Models == null){
            ModelIterator mIter = new ModelIterator(model);
            while(mIter.hasNext()){
                ModelObject thisModel = mIter.next();
                ArrayList<ModelObject> myArray = findSome(thisModel,ClassName,PropertyKeyValues);
                myMatches.addAll(myArray);
            }
        }
        else {
            for (String uid : Models.keySet()) {
                ModelObject thisModel = ModelObject.GetModelObj(Models, uid);
                boolean matches = false;
                for(String prop : PropertyKeyValues.keySet()){
                    String value = (String)thisModel.get(prop);
                    if(value.compareTo((String)PropertyKeyValues.get(prop)) == 0){
                        matches = true;
                    }
                }
                if (matches){ myMatches.add(thisModel);}
            }
        }
        
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSome(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
            return findSome(node.rootGraph, ClassName, PropertyKeyValues);
    }

    @Override
    public ArrayList<ModelObject> findSimilar(ModelObject model, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();

        JSONObject Models = model.getModels(ClassName);
        if(Models == null){
            ModelIterator mIter = new ModelIterator(model);
            while(mIter.hasNext()){
                ModelObject thisModel = mIter.next();
                ArrayList<ModelObject> myArray = findSimilar(thisModel,ClassName,property, value);
                myMatches.addAll(myArray);
            }
        }
        else {
            for (String uid : Models.keySet()) {
                ModelObject thisModel = ModelObject.GetModelObj(Models, uid);
                String prop = (String)thisModel.get(property);
                if(DBUtils.similarity(prop,value) > 0.8){ //Levenshtein
                    myMatches.add(thisModel);
                }
            }
        }
        return myMatches;
    }

    @Override
    public ArrayList<ModelObject> findSimilar(DBNode node, String ClassName, String property, String value) {
       return findSimilar(node.rootGraph, ClassName, property,value);
    }

    @Override
    public ModelObject findKey(ModelObject model, String ClassName, String key) {
        ModelObject findModel = ModelObject.GetModelObj(model, key);
        if(findModel != null) {
           return findModel;
        }
        findModel = model.getModel(ClassName, key);

        if(findModel != null){
            return findModel;
        } else{
            ModelIterator mIter = new ModelIterator(model); //New Iterator Method
            while(mIter.hasNext()){
                ModelObject currModel = mIter.next();
                return findKey(currModel,ClassName, key);
            }
        }
        return null;
    }

    @Override
    public ModelObject findKey(DBNode node, String ClassName, String key) {
        return findKey(node.rootGraph, ClassName, key);
    }

    public ArrayList<ModelObject> findStartsWith(ModelObject model, String ClassName, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();

        JSONObject Models = model.getModels(ClassName);
        if(Models == null){
            ModelIterator mIter = new ModelIterator(model);
            while(mIter.hasNext()){
                ModelObject thisModel = mIter.next();
                ArrayList<ModelObject> myArray = findStartsWith(thisModel,ClassName, value);
                myMatches.addAll(myArray);
            }
        }
        else {
                for (String uid : Models.keySet()) {
                    ModelObject thisModel = ModelObject.GetModelObj(Models, uid);
                    String key = thisModel.getUID();
                    if (key.startsWith(value)) {
                        myMatches.add(thisModel);
                    }
                }
        }
        return myMatches;
    }

    public ArrayList<ModelObject> findStartsWith(DBNode node, String ClassName, String key) {
        return findStartsWith(node.rootGraph, ClassName, key);
    }

    public ArrayList<ModelObject> propStartsWith(ModelObject model, String ClassName, String property, String value) {
        ArrayList<ModelObject> myMatches = new ArrayList<>();

        JSONObject Models = model.getModels(ClassName);
        if (Models == null) {
            ModelIterator mIter = new ModelIterator(model);
            while(mIter.hasNext()){
                ModelObject thisModel = mIter.next();
                ArrayList<ModelObject> myArray = propStartsWith(thisModel,ClassName, property,value);
                myMatches.addAll(myArray);
            }
        } else {

                for (String uid : Models.keySet()) {
                    ModelObject thisModel = ModelObject.GetModelObj(Models, uid);
                    String key = (String)thisModel.get(property);
                    if (key.startsWith(value)) {
                        myMatches.add(thisModel);
                    }
                }
        }
        return myMatches;
    }

    public ArrayList<ModelObject> propStartsWith(DBNode node, String ClassName, String property, String value) {
        return propStartsWith(node.rootGraph, ClassName, property,value);
    }


    @Override
    public boolean deleteKey(ModelObject model, String ClassName, String key) {

        boolean removed =   model.Remove(ClassName, key);
        if(removed) return true;


        JSONObject myMap =  model.getModels(ClassName);

        if(myMap != null){
            ModelObject mObj = (ModelObject)myMap.remove(key);
            if(mObj != null){
                
                return true;
            }
        }

        for(String modelName : ModelKeys.ModelKeys()) { //Search internal models
            myMap = model.getModels(modelName);
            if (myMap != null) {
                for(String uid : myMap.keySet()){
                    ModelObject thisModel = ModelObject.GetModelObj(myMap, uid);
                    removed = deleteKey(thisModel, ClassName, key);
                    if(removed){
                        
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteKey(DBNode node, String ClassName, String key) {
        return deleteKey(node.rootGraph, ClassName, key);
    }

    public String NumberNodes(){
        return String.format("%d",Nodes.keySet().size());
    }

    @Override
    public void SyncNode(DBNode thisNode) throws IOException {

        if(thisNode.hasChanged) {
            try {
                thisNode.WriteNode(thisNode.GetFile());
                thisNode.hasChanged = false;
            }catch(NoSuchFileException e){
                thisNode.WriteNode(SrcPath + thisNode.GetFile());
                thisNode.hasChanged = false;
            }
        }

    }

    @Override
    public synchronized void SyncNotifications() {
        //I don't do anything
    }

    @Override
    public synchronized void Sync() throws IOException {
        for(String nodeKey : Nodes.keySet()){
            DBNode node = Nodes.get(nodeKey);
            SyncNode(node);
        }

        try {
            this.wait();
        }catch(InterruptedException e){
            //Waiting for this
        }


    }

    public void RunSync(){
        this.interrupt();
    }



    @Override
    public void run() {
        System.out.println("\nDatabase: " + Name + " starting...");

        while (!ExitCondition) {
            try{
                this.SyncNotifications();
                this.Sync();
            }
            catch(IOException e){
                    System.out.println("Database File Error Exiting Sync Thread ...");
                    e.printStackTrace();
                    ExitCondition = true;
            }

        }
    }

    @Override
    public void Exit() {
        ExitCondition = true;
        
    }
}
