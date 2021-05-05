# ModelKit Guide
*Note that object plurals by convention will be referring to a general collection of those models*
## Overview ``xyz.model`` package
Web frameworks typically introduce the concept of Model objects in order to faciliate storage of data through flexible
means. In the case of XYZRECIPES. The ``ModelObject`` class is the base model class which extends ``JSONObject`` from the 
``org.json`` library. This organization allows our models to natively read and write their keyed properties to and from JSON.
Also, note that a framework model system can be the core framework definition for frameworks like Content Management Systems (CMS).
In this case models are used to store everything from Application Data, to CSS stylesheets, Notification Systems, and generated HTML.

Relevant Models like ``Group``, ``User``, ``Recipe``, ``Notification``, ``Item``, ``Theme``. Have been added.

The core API of our ``ModelObject`` can be found in the ``Model`` interface which it implements.

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

## ModelObjects
`ModelObject` objects store the `UID` `ClassName` `Name` fields. `UID` is auto-generated for generic objects. And `ClassName`
should always reference the actual `ModelObject` classname, non-pluralized. `Name` is a descriptive field but has no real functional consequences for 
`ModelObject`'s themeselves. 

Storing children in `ModelObjects` is done by `addModel` and the model is added by its `JSONObject` type to the tree under the key of the pluralized `ClassName` 
parameter. 

### Model UID
Model UIDs are generated for General Purpose models from a 512 bit random appended integer
which is hashed by a random seed. You can't reconstruct ModelIDs. They are used to place unique
keys into the internal HashMaps that our ``ModelObject`` uses. The chances of a collision are 1 over
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
You must make sure that all constructor classes respect this assignment and call the
`JSONObject.updateKey()` function to updated the internally mapped key when construction of the `ModelObject` involves a call to
`super()` 


###ModelObject Properties
Since model objects are really abstract `JSONObject` types they can store any data they wish in their key fields. It is up to the user 
to ensure that when they are creating objects from `JSONObjects` or Strings representing JSON, that the keyed data is in fact there.
Otherwise and error will be thrown to the caller. 


## Group/User Model
Group models will be important when controllers/endpoints try to restrict access to certain
method calls on the exposed API. We have chosen to use the ``AccessLevel`` int of  a ``Group`` item to 
specify whether a User has a certain privelege level. Typically ``1`` would be your highest admin priveleges
``2`` would be protected and ``3`` would be user, etc...

The endpoint/controller methods should tag all of their Method enpoints with an access level tag of 
some sort. 


Other methods exist such as `ACL Trees` but these are typically difficult to create and 
must be maintained. 

Note that a ``User`` model here in this context obviously references a ``GroupID`` which is the
``Name`` of the group it is assigned to. 

### User Passwords/Email
User creation will result in the creation of a user type with and its password will be stored
as a ``SHA256`` hash result of that password. A ``User.UID`` is it's email key for fast access.
Note that validation of the key existing previously should be handled by the controller. Otherwise the model will just
create the new user in place. 

## Site/Pages/Theme Model

Sites, pages, and themes are interconnected models. Themes standalone as a model instance and are stored in their
own DBNodes from how the controllers manage these models. `Site` objects will store a `ThemeID` to reference the theme.
Since `Site` can reference multiple pages it makes sense to store theme within the Model itself. Multiple children of
a model are stored by the `pluralize()` name of the `ModelObject.ClassName` field. So a site will reference the `Pages` key
as a reference to the field of pages is holds. This is store as a reference to an internal `JSONObject` as a child node of the model object.

Since sites know they hold this field they reference specific pages with their `UID` and utilize the 
`getPage()` method to get specific pages. 


### Integration with the Database


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
provides an API that offers access to the model roots. 












