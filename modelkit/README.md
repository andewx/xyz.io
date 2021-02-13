# ModelKit Guide
*Note that object plurals by convention will be referring to a general collection of those models*
## Overview ``xyz.model`` package
Web frameworks typically introduce the concept of Model objects in order to faciliate storage of data through flexible
means. In the case of XYZRECIPES. The ``ModelObject`` class is the base model class which extends ``JSONObject`` from the 
``org.json`` library. This organization allows our models to natively read and write their keyed properties to and from JSON.
Also, note that a framework model system can be the core framework definition for frameworks like Content Management Systems (CMS).
In this case models are used to store everything from Application Data, to CSS stylesheets, Notification Systems, and generated HTML.

Relevant Models like ``Group``, ``User``, ``Recipe``, ``Notification``, ``Item``, ``Theme``. Have been added.

The core API of our ``ModelObject`` can be found in the ``Model`` interface which it implements. Warning that all model properties
, including the Model UID can be accessed through public members of the ``ModelObject`` class. This facilitates obtaining
objects through our DB query engine, manipulating properties, committing with the public ``update()`` method available to 
all models. 

The implementation classes that extend ``ModelObject`` are relevant to the rest of
the Web frameworks usage but currently assumptions such as what kind of Children you'd expect to find in a  ``Recipe`` model
are assumed and implemented by the external module api users. 

The ``Model`` interface usage is primarily to expose a common interface to the Database API. 
However if you want to inspect the models after obtaining an object you may use it. 

```
public interface Model {

    //Getters/Setters
    public String getModelName();
    public String getName();
    public String getUID();

    //Model Property Methods
    public JSONObject getJSON();
    public Object updateKey(String key, Object value);
    public void removeKey(String key);
    public void update();

    //Child Handlers
    public HashMap<String,HashMap<String,Model>> getChildren();
    public Model getModel(String mClass, String uid);
    public void addModel(Model m);
    public HashMap<String,Model> getModels(String mClass);
    public Remove(String mClass, String uid);

    public String toString();
    public String toJson();


}
```

### Model UID
Model UIDs are generated for General Purpose models from a 512 bit random appended integer
which is hashed by a random seed. You can't reconstruct ModelIDs. They are used to place unique
keys into the internal HashMaps that our ``ModelObject`` uses. A the chances of a collision are 1 over
1.4×10^77. Essentially a collision is unlikely to happen in our lifetimes. 

Some ``ModelObject`` classes, like users, naturally should be constructed with uniquely identifying data,
in this case, the generated UID is traded for the a new Key by assignment. For example:

```
public class User extends ModelObject{
  public User(String Email, ...){
    UID = Email;
  }
}
```
This makes classes that naturally consist of key data easy to find. Otherwise you will
generally have to list out and iterate through keys. 

### Model Children
All models list the ``Model`` interface children they can have in the ``ModelObject.Children`` property. 

This type is a ``HashMap<String,HashMap<String, Model>> ``

So we have a ``java.util`` ``HashMap`` of ``HashMaps``. This is an understandably indirect and obfuscated
way to store children. But it allows us to store different tree branches by their ``ClassName`` property.
Models can group these properties by adding their ClassNames to the ``xyz.Model.ModelKeys`` class method ``ModelKeys``. 
When trying ``addModel(Model m)`` we first see if any object exists at the key with that Models classname.
If not we create a new hash map and input the actual object into that internal map.

Note that model creation
is not that same as adding/removing a model from the system. You must call the ``addModel`` or ``Remove`` methods from some relevant parent model when
we are thinking about having a model object system.

Importantly Models don't describe the implementation of their Children or their relationship to those children.
So anything can be added. A ``User`` can have a list of ``Recipe`` items. But there is nothing to stop a ``Item`` from having a ``User``
child. It's really up to the controllers to have a full understanding of what Child relationships
should mean, and whether certain relationships should be allowed. 

## Group/User Model
Group models will be important when controllers/endpoints try to restrict access to certain
method calls on the exposed API. We have chosen to use the ``AccessLevel`` int of  a ``Group`` item to 
specify whether a User has a certain privelege level. Typically ``1`` would be your highest admin priveleges
``2`` would be protected and ``3`` would be user, etc...

The endpoint/controller methods should tag all of their Method enpoints with an access level tag of 
some sort. 


Other methods exist such as `ACL Trees` but these are typically difficult to create and 
must be maintained. The juice I feel wasn't worth the squeeze.

Note that a ``User`` model here in this context obviously references a ``GroupID`` which is the
``Name`` of the group it is assigned to. 

### User Passwords/Email
User creation will result in the creation of a user type with and its password will be stored
as a ``SHA256`` hash result of that password. A ``User.UID`` is it's email key for fast access.
Note that validation of the key existing previously should be handled by the controller. Otherwise the model will just
create the new user in place. 

### Notification System

I chose to add a ``Notification`` type for future since it would have to be integrated
into the ``DBKit``. These Models are general use notification lists that specify the
``UID`` and ``ClassName`` of the notifier and the subscriber. This relationship is simply
an ``A -> B`` notification relationship and is stored independently likely in its own ``DBNode``.

Notifications have an attached ``Message`` and  boolean values on whether they have been
``Sent`` or ``Read``.  If a notification has been sent it should be copied into the B object.
If it has been Read it should be removed. This positive notification allows us to scan for notifications
and perform updates and processing relevant to the system. 

The DB will operate a separate processing thread that Scans the notification lists and
appends notifications, and removes them if neccessary. 

## Database (DBKit)


Lastly, I will mention how these models get integrated into the larger system. Since
our models are not simply represented as sequel tables, but model trees, we can think
of their wider implementation as stored trees. Which allows us to view ``ModelObjects``
as always having internal data. As we discussed that internal data is a List of HashMaps of
the classes that have been added. *WARNING: model cycles not allowed*

Essentially then, we are running an informal graph database. Which gives great power. 
We can easily separate data into different rooted tree nodes. A notification system for 
example could store all its notifications in a single tree and thereby save its permanence 
data in a separate database. For example it makes sense to have separate ``DBNode`` items for
``Users``, ``Recipes``, ``Groups``, or even ``Themes`` if we wanted to give it that power. 

This graph database means that we have a reliable in memory view of the model graphs. And that
we can update those graphs super fast as long as we know the keys we are looking for. The database
provides an API that offers access to the model roots. And in a separate thread it can do ``I/O & Graph Processing``
which allows the database to keep its primary work in a separate thread and merely provide a ``ModelObject``
query interface on the other-side. 












