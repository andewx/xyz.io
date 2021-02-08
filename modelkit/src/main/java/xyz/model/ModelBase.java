package xyz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.*;

public class ModelBase implements Model { //Basic Model Object

    public String UID;
    public String Name;
    public HashMap<String,String> mPropertyMap;
    public HashMap<String,String> mTypeMap;

    public HashMap<String,Model> mModelUID; //Linked Model By ID
    public HashMap<String, ArrayList<Model>> mModelLists; //Linked Model Lists


    public ModelBase(){
        Name = "Model";
        UID = ModelHandler.genUID();

        mPropertyMap = new HashMap<String,String>();
        mTypeMap = new HashMap<String,String>();
        mModelUID = new HashMap<String,Model>();
        mModelLists = new HashMap<String, ArrayList<Model>>();

        //Add Relevant Properties
        mPropertyMap.put("UID", UID);
        mTypeMap.put("UID", "String");
        mPropertyMap.put("Name", Name);
        mTypeMap.put("Name", "String");

        //Models Set Empty

    }

    @Override
    public String getUID() {
        return UID;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setProperty(String propKey, String propValue) {
        mPropertyMap.replace(propKey,propValue);
    }

    @Override
    public String getProperty(String propKey) {
        return mPropertyMap.get(propKey);
    }

    @Override
    public String getPropertyType(String propKey) {
        return mTypeMap.get(propKey);
    }

    @Override
    public ArrayList<Model> hasModels(String name) {
        return mModelLists.get(name);
    }

    @Override
    public void addModel(Model m) {
        String id = m.getUID();
        String name = m.getName();
        mModelUID.put(id,m);
        ArrayList<Model> list = m.hasModels(name);

        if (list == null){
            list = new ArrayList<Model>();
            list.add(m);
            mModelLists.put(name,list);
        }else{
            list.add(m);
        }

        return;

    }

    @Override
    public Model getModel(String uid) {
        return mModelUID.get(uid);
    }

    @Override
    public void syncModel(boolean syncAll) {
        mPropertyMap.replace("UID", UID);
        mPropertyMap.replace("Name", UID);

        if (syncAll){ //circular references will stall program - not handled
            for (Model iter : mModelUID.values()){
                iter.syncModel(true);
            }
        }
    }
}
