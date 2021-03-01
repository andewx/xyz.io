package xyz.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ModelIterator implements Iterator<ModelObject> {

    private ModelObject curr;
    private JSONObject childList;
    private ArrayList<String> childKeys;
    private int index;
    private int childIndex;
    private String modelKey;
    private boolean isDone;

    public ModelIterator(ModelObject head){
        curr = head;
        isDone = false;
        index = -1;
        childIndex = 0;
        childKeys = new ArrayList<>();
        while(childList == null){
            index++;
            modelKey = ModelKeys.ModelKeys().get(index);
            childList = curr.getModels(modelKey);
        }

        if(childList != null){
            childKeys.addAll(childList.keySet());
        }else{
            isDone = true;
        }
    }


    @Override
    public boolean hasNext() {
        if(childIndex > childList.keySet().size()-1){
            childIndex = 0;
            index++;
        }
        if(index > ModelKeys.ModelKeys().size() - 1){
            isDone = true;
        }else{
            modelKey = ModelKeys.ModelKeys().get(index);
            childList = curr.getModels(modelKey);
        }

        if(childList == null){
            isDone = true;
        }

        return !isDone;
    }

    @Override
    public ModelObject next() {
        String nextKey = childKeys.get(childIndex);
        childIndex++;
        ModelObject nextModel = ModelObject.GetModelObj(childList,nextKey);
        return nextModel;
    }

    public ModelObject getCurrent(){
        return curr;
    }
}
