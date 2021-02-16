# DBKit Guide
## Package ``xyz.dbkit``
## Overview
The dbkit implementation is still considered to be in development mode
however you will still find a robust and workable query interface at this moment. The first consideration when using
``xyz.dbkit.DBMain`` is it constructor.

``` 
public DBMain(String name) throws IOException {
        //Checks if node property files exists -- stored as .keys json files
        Name = name;
        String nodeFile = "node.keys";

        //File Stuff
        Path dbPath = Path.of(nodeFile);
        ...
   ```
You can create a ``DBMain`` object by any name with no effect on the operation. The only names that matter are in
fact the ``DBNode`` classes attached as a ``HashMap<String,DBNode>`` map. This maps ``DBNode`` names as keys to their
respective objects. These objects have a respective ``String file`` type which is mapped to the ``DBMain`` ``NodeFileManager``
path mapping. This path mapping is stored in a file named ``node.keys``. Currently there is no protection on this
filename even though it is special. Do not name any ``DBNodes`` "node" when you are creating them!

The respective ``DBNode`` inner ``ModelObject`` graphs are built during the constructor of ``DBMain``. If non-existent,
a standard ``DBNode`` of ``default`` is set. 

Next, you need to know that the queries are total ``DFS`` searches of the root model graphs. Since the model graphs are
essentially expanded K-trees. A deep enough graph could be extremely time consuming on the query. It is best to keep
each ``DBNode`` root as flat as possible. Therefore I would mostly try and attach separate Model Types in separate nodes.

For Example:
```
/*
* Assuming we have initalized
* DBMain myDatabase;
*/
DBNode Users =  myDatabase.CreateNode("Users", "users.keys");
myDatabase.addModel(Users, new User()); //Adds to rootGraph
```
You can add models by calling through Model Objects internally but if not done through DBMain then the database
won't know to update. 

I will add a ``ModelObject`` extended type of ``Relational`` to help with this flat mapping. 

## DBManager Interface (DBMain implements)

The interface is kind of large and there are separate managing activities for the database. It must be able to:
- Manage the nodes (and file writing)
- Conduct ``find`` queries
- Implement a ``Runnable`` thread to conduct syncing

Here is the interface:
```
public interface DBManager extends Runnable {

    //Database API
    DBNode CreateNode(String nodeName, String nodeFilePath) throws IOException;
    DBNode CreateNode(String nodeName, String nodeFilePath, String json);
    boolean DeleteNode(String nodeName);
    boolean ActiveNode(String nodeName);
    DBNode GetNode(String nodeName);
    
    ModelObject addModel(DBNode node, ModelObject m);

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

```

## Query (Find) Operations
As I discussed earlier. Database JSON searches are DFS searches. For now even if you land on the ``ClassName`` collection
you were trying to land in. The algorithm is just carrying out a pure DFS recursion. Pure through put has not yet been
tested. 

When using find operations from other tool kits you mostly will be interested in using the function signatures that 
look like ``findExact(DBNode node...)`` since this will start your search of the database. Unless you already have the
database available.

The model root graph access itself is not threaded, except for the syncing actions. Therefore requests are viewed as being
sequential. This may be an issue if the caller is running threaded calls to the Memory model itself. Some of the methods
will be synchronized but its too soon to know what issues we will run into. If a Model Key is being edited and removed from 
the underlying graph at the same your edits will likely be lost. 

The ``findSimilar`` property matches currently look to see how far off a string is and is not the same as like a property
starts with search.

The ``findSome`` query returns objects where any of the properties are matching. All Properties are interpreted to be ``Strings``

## Final Notes

There is honestly quite a lot of testing to be done on the database and I won't consider it ready for another week I imagine


