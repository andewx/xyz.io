# DBKit Guide
## Package ``xyz.dbkit``
## Overview
DBKit module, or the `xyz.dbkit` package operates with two main classes, the `DBMain` object and the
`DBNode` object. The first consideration when using
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
respective objects. These objects have a respective ``String file`` type which is mapped to the `ModelObject` that `DBNode` is for. 
`DBMain` generates these nodes based on the `ModelObjects` which are registered with `xyz.modelkit.ModelKeys` static class.

The respective ``DBNode`` inner ``ModelObject`` graphs are built during the constructor of ``DBMain``. If non-existent,
a standard ``DBNode`` of ``default`` is set. 

Next, you need to know that the queries are total ``DFS`` searches of the root model graphs. Since the model graphs are
essentially expanded K-trees. A deep enough graph could be extremely time consuming on the query. It is best to keep
each ``DBNode`` root as flat as possible. Therefore I would mostly try and attach separate Model Types in separate nodes. 
Occassionally a model relationship is best described with internal objects, in which case it is okay but the relationship is
assumed in the model. It is recommended that you provide a getter and setter for the expected model types another model might
hold in its own `ModelObject` class. A `Site` can hold a `Page` for instance so we provide a `Site.getPage()` implemenation. 

Here is how a DBNode is created programatically.

```
/*
* Assuming we have initalized
* DBMain myDatabase;
*/
DBNode Users =  myDatabase.CreateNode("Users", "users.keys");
myDatabase.addModel(Users, new User()); //Adds to rootGraph
```
See the `xyz.modelkit.ModelKeys` file for how to register classes with the database. 
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
you were trying to land in. The algorithm is just carrying out a pure DFS recursion. 

When using find operations from other tool kits you mostly will be interested in using the function signatures that 
look like ``findExact(DBNode node...)`` since this will start your search of the database. Unless you already have the
database available.



The ``findSimilar`` property matches currently look to see how far off a string is and is not the same as like a property
starts with search.

The ``findSome`` query returns objects where any of the properties are matching. All Properties are interpreted to be ``Strings``

## Notes

While the database is threaded itself, calls currently for query operations are not executed in their own threads
and are therefore sequential, this is also true of course for I/O. A frequent submission of database queries from a large number
of requests is not a tested operation so response time is not guaranteed.

The database executes its syncing (database write) operation via interrupt. Calls to the thread of the database should hang
after all writes are finished which adds some stability to the platform. 

