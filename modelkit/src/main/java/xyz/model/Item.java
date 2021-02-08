package xyz.model;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.lang.reflect.*;

public class Item implements Model {

    public String uID;
    public String mName;
    public String mDescr;
    public String mTitle;
    public String mUnits;
    public int mAmount;
    public LinkedHashMap<String,String> mPropertyMap;
    public LinkedHashMap<String,String> mTypeMap;
    public LinkedHashMap<String,Model> mModels;
    public ArrayList<Object> mObjectMap;


    public Model createModel(String unique) {
        Item newItem = new Item();
        newItem.mName = "Item";
        newItem.mAmount = 1;
        newItem.uID = unique;

      //Reflection To Write Map Property Values
      try {
          Class c = Class.forName("xyz.model." + newItem.mName);
          Field[] p = c.getDeclaredFields();
          for(Field field : p){
              Class valType = field.getType();
              String strValue = new String();
              newItem.mPropertyMap.put(field.getName(), field.get(strValue).toString());
              newItem.mTypeMap.put(field.getName(), valType.getName());
          }
      }
      catch (Throwable e){
          System.out.println("Fatal Error: Item Model could not be initialized\n");
      }

        return newItem;
    }


    @Override
    public String getUID() {
        return uID;
    }

    @Override
    public String getName() {
        return this.mName;
    }

    @Override
    public void setProperty(String propKey, String propValue) {
        mPropertyMap.replace(propKey,propValue);
    }

    @Override
    public String getProperty(String propKey, String propValue) {
        return mPropertyMap.get(propKey);
    }

    @Override
    public String getPropertyType(String propKey) {
        return mTypeMap.get(propKey);
    }

    @Override
    public ArrayList<Model> hasModels() {
        return new ArrayList<Model>(mModels.values());
    }

    @Override
    public void addModel(Model m) {
            mModels.put(m.getUID(), m);
    }

    @Override
    public Model getModel(String uid) {
        return mModels.get(uid);
    }

    @Override
    public void syncModel(boolean syncInternal) {
        //Reflection For Syncs
        try {
            String className = this.getName();
            Class c = Class.forName("xyz.model." + className);

            Field[] p = c.getDeclaredFields();
            for(Field field : p){
                Class valType = field.getType();
                String strValue = "";
                mPropertyMap.put(field.getName(), field.get(strValue).toString());
            }
        }
        catch (Throwable e){
            System.out.println("Fatal Error: Item Model could not be initialized\n");
        }
    }
}
