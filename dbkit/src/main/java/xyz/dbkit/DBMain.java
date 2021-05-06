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

/**
 * @author briananderson
 * @version 1.0
 *  Framework internal high-level database API. Runs database operations asynchronously using JSON to database representation. Each Model specified from Model/ModelKeys static utility class is mapped to a gen'd DBNode which maintains its own internal mapping of a ModelObject tree for storage. Query functionality currently only exposed as Java class API with simple key or property value searches. Implements the DBManager interface for query operations
 */
public class DBMain extends Thread implements DBManager{

    /**
     * The Nodes.
     */
    HashMap<String,DBNode> Nodes;
    /**
     * The Name.
     */
    String Name;
    /**
     * The Exit condition.
     */
    boolean ExitCondition;
    /**
     * The Src path.
     */
    String SrcPath;

    /**
     * Constructs database from expected DBNodes associated with ModelKeys(). DBNodes then self-construct
     * in the JVM resources directory from the Model.keys files. Throws error if DBNode can't be instantiated
     *
     * @param name Database name
     * @throws IOException DBNode not found or able to be created throws error to caller
     * Requirement(DBkit 1.0)
     */
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

    /**
     * Getter Database name
     *
     * @return Database Name
     * Requirement(DBkit 1.0)
     */
    public String GetName(){
        return Name;
    }

    /**
     * Creates DBNode object whose root node is constructed from the ModelKeys default constructor
     * @param nodeName - Name of the class of DBNode. Must be a ModelObject ClassName whose keys are stored inside the ModelKeys static class
     * @return - Instantiated DBNode
     * @throws IOException - Could not create DBNode because of ModelObject ClassName error
     * Requirement(DBkit 2.0)
     */
    @Override
    public DBNode CreateNode(String nodeName) throws IOException {
            DBNode myNode = DBUtils.InitNode(nodeName);
            Nodes.put(ModelKeys.pluralize(nodeName), myNode);
            return myNode;
    }

    /**
     * Delete DBNode from the Database. Delegates deletion and updates to DBNode
     * @param nodeName - ModelObject ClassName
     * @return boolean on whether DBNode was deleted
     * @throws IOException Could not find or delete a DBNode
     * Requirement(DBkit 2.0)
     */
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

    /**
     * Gets a DBNode from a ModelObject ClassName
     * @param nodeName ModelObject ClassName present in ModelKeys class
     * @return Existing DBNode
     * Requirement(DBkit 2.0)
     */
    @Override
    public DBNode GetNode(String nodeName) {
        return Nodes.get(nodeName);
    }

    /**
     * Adds ModelObject to the top level of the ModelObject tree held by DBNode
     * @param node - DBNode conducting the operation
     * @param m ModelObject to be added
     * @return the added ModelObject
     * Requirement(DBkit 2.0)
     */
    @Override
    public ModelObject AddModel(DBNode node, ModelObject m) {
        node.rootGraph.addModel(m);
        
        node.hasChanged = true;
        return m;
    }

    /**
     * Flags the DBNode as requiring update so that the DB knows this Node is available for syncing
     * @param node DBNode to be updated
     * @param m ModelObject associated with DBNode
     * @return ModelObject Associated
     * Requirement(DBkit 2.0)
     */
    @Override
    public ModelObject UpdateModel(DBNode node, ModelObject m) {
        
        node.hasChanged = true;
        return m;
    }

    //--------------------------------- QUERY METHODS ---------------------------------//

    /**
     * Queries ModelObject m for matching set of Key Values using DFS
     * @param model ModelObject to be searched
     * @param ClassName ModelObject class of the searched type
     * @param PropertyKeyValues Mapped Key/Value Pairs of the search parameters
     * @return ArrayList of objects matching the query parameters
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Queries DBNode for exact matching parameters of a specific model type
     * @param node DBNode to be searched
     * @param ClassName ModelObject Class
     * @param PropertyKeyValues Parameter list of key/value pairs
     * @return List of ModelObjects with matching parameters
     * Requirement(DBkit 4.0)
     */
    @Override
    public ArrayList<ModelObject> findExact(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
      return findExact(node.rootGraph, ClassName, PropertyKeyValues);
    }

    /**
     * Queries ModelObject for objects matching at least one of the key/value pair parameters
     * @param model ModelObject to be searched
     * @param ClassName Class of Object to be found
     * @param PropertyKeyValues Parameter list of key/value pairs
     * @return matching ModelObjects
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Finds ModelObjects matching at least one of the Parameter Key/Value search parameters from a DBNode
     * @param node DBNode to be searched
     * @param ClassName ModelObject class to be found
     * @param PropertyKeyValues Parameter list of key/value pairs
     * @return ArrayList of ModelObjects with at least one matching parameter
     * Requirement(DBkit 4.0)
     */
    @Override
    public ArrayList<ModelObject> findSome(DBNode node, String ClassName, HashMap<String, String> PropertyKeyValues) {
            return findSome(node.rootGraph, ClassName, PropertyKeyValues);
    }

    /**
     * Finds ModelObjects who partially match the corresponding key/value parameter using a distance metric algorithm to compare string similiarity
     * @param model ModelObject to be searched
     * @param ClassName Class of ModelObject to be found
     * @param property Property Key
     * @param value Value to be compared
     * @return ModelObject list of similarily matching parameters
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Finds ModelObjects from DBNode who partially match the corresponding key/value parameter using a distance metric algorithm to compare string similiarity
     * @param node DBNode to be searhed
     * @param ClassName Class of ModelObject to be found
     * @param property Property Key
     * @param value Value to be compared
     * @return ModelObject list of similarily matching parameters
     * Requirement(DBkit 4.0)
     */
    @Override
    public ArrayList<ModelObject> findSimilar(DBNode node, String ClassName, String property, String value) {
       return findSimilar(node.rootGraph, ClassName, property,value);
    }

    /**
     * Finds a singular object by its key from a ModelObject by its UID
     * @param model ModelObject to be searched
     * @param ClassName Class of ModelObject to be found
     * @param key Key to be compared
     * @return ModelObject if found or null
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Finds a singular object by its key from a ModelObject by its UID
     * @param node DBNode to be searched
     * @param ClassName Class of ModelObject to be found
     * @param key Key to be compared
     * @return ModelObject if found or null
     * Requirement(DBkit 4.0)
     */
    @Override
    public ModelObject findKey(DBNode node, String ClassName, String key) {
        return findKey(node.rootGraph, ClassName, key);
    }

    /**
     * Finds a list of objects that start with the specified string in its UID
     *
     * @param model     ModelObject to be searched
     * @param ClassName Class of ModelObject to be found
     * @param value     The Prefix of the object to be compared
     * @return List of ModelObjects with matching prefixes
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Finds a list of objects that start with the specified string in its UID
     *
     * @param node      DBNode to be searched
     * @param ClassName Class of ModelObject to be found
     * @param key       The Prefix of the object to be compared
     * @return List of ModelObjects with matching prefixes
     * Requirement(DBkit 4.0)
     */
    public ArrayList<ModelObject> findStartsWith(DBNode node, String ClassName, String key) {
        return findStartsWith(node.rootGraph, ClassName, key);
    }

    /**
     * Finds a list of objects whose specific property is a suffix of that object
     *
     * @param model     ModelObject to be searched
     * @param ClassName Class of ModelObject to be found
     * @param property  Property parameter to be searched
     * @param value     Value of suffix to be matched
     * @return List of ModelObjects with matching suffixes for a given property
     * Requirement(DBkit 4.0)
     */
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

    /**
     * Finds a list of objects whose specific property is a suffix of that object
     *
     * @param node      DBNode to be searched
     * @param ClassName Class of ModelObject to be found
     * @param property  Property parameter to be searched
     * @param value     Value of suffix to be matched
     * @return List of ModelObjects with matching suffixes for a given property
     * Requirement(DBkit 4.0)
     */
    public ArrayList<ModelObject> propStartsWith(DBNode node, String ClassName, String property, String value) {
        return propStartsWith(node.rootGraph, ClassName, property,value);
    }

    /**
     * Deletes a ModelObject with the specified key UID
     * @param model ModelObject to be searched
     * @param ClassName Class of ModelObject to be found
     * @param key Value to matched as the UID
     * @return boolean of whether not the keyvalue was delete
     * Requirement(DBkit 4.0)
     */
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


    /**
     * Deletes a ModelObject with the specified key UID
     * Requirement(DBkit 3.0)
     * @param node DBNode to be searched
     * @param ClassName Class of ModelObject to be found
     * @param key Value to matched as the UID
     * @return boolean of whether not the keyvalue was delete
     */
    @Override
    public boolean deleteKey(DBNode node, String ClassName, String key) {
        return deleteKey(node.rootGraph, ClassName, key);
    }

    /**
     * Number of nodes the DB has
     * Requirement(DBkit 3.0)
     * @return Number of DBNode keys
     */
    public String NumberNodes(){
        return String.format("%d",Nodes.keySet().size());
    }

    /**
     * Run synchronization of DBNode, writes out DBNode into resources directory under the name of its DBNode class
     * Requirement(DBkit 3.0)
     * @param thisNode DBNode to be written
     * @throws IOException File I/O Error Encountered throws to caller
     */
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

    /**
     * Non implemented Notifications database async tasking
     */
    @Override
    public synchronized void SyncNotifications() {
        //I don't do anything
    }

    /**
     * Synchronizes DB across all DBNodes calling SyncNode. If DBNode is flagged then DBNode should write itself.
     * Method is sychronized in its own thread and completes before futher execution of the monitoring thread
     * @throws IOException File I/O Error throws to caller
     * Requirement(DBkit 3.0)
     */
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

    /**
     * Causes DB to execute synchronized thread taskings via interrupt() main caller to Database thread
     */
    public void RunSync(){
        this.interrupt();
    }


    /**
     * Initializes database and instantiates thread for Database sync
     * Requirement(DBkit 3.0)
     */
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

    /**
     * Exits database by setting flag for thread execution
     * Requirement(DBkit 3.0)
     */
    @Override
    public void Exit() {
        ExitCondition = true;
        
    }
}
