package xyz.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ModelIterator for implements Java STD Iterator for ModelObjects, allows for rudimentary DFS
 * traversal of the JSON Tree
 * @author briananderson
 */
public class ModelIterator implements Iterator<ModelObject> {

    private ModelObject curr;
    private JSONObject childList;
    private ArrayList<String> childKeys;
    private int index;
    private int childIndex;
    private String modelKey;
    private boolean isDone;

    /**
     * ModelIterator constructor - initializes a root head ModelObject element for start of traversal
     * @param head - the root ModelObject head to be traversed.
     */
    public ModelIterator(ModelObject head){
        curr = head;
        isDone = false;
        index = -1;
        childIndex = 0;
        childKeys = new ArrayList<>();
        while(childList == null && index < ModelKeys.ModelKeys().size() - 1){
            index++;
            modelKey = ModelKeys.ModelKeys().get(index);
            childList = curr.getModels(modelKey);
        }

        if(index > ModelKeys.ModelKeys().size() - 1){
            isDone = true;
        }

        if(childList != null){
            childKeys.addAll(childList.keySet());
        }else{
            isDone = true;
        }
    }


    /**
     * Iterator hasNext() implementation, checks if the iterator has sibling or child elements
     * @return boolean whether Iterator has more elements
     */
    @Override
    public boolean hasNext() {
        try {
            if (childIndex > childList.keySet().size() - 1) {
                childIndex = 0;
                index++;
            }
            if (index > ModelKeys.ModelKeys().size() - 1) {
                isDone = true;
            } else {
                modelKey = ModelKeys.ModelKeys().get(index);
                childList = curr.getModels(modelKey);
            }

            if (childList == null) {
                isDone = true;
            }
        }catch(NullPointerException e){
            isDone = true;
        }

        return !isDone;
    }

    /**
     * The next feature
     * @return updates the current iterator to the next specified index child
     */
    @Override
    public ModelObject next() {
        String nextKey = childKeys.get(childIndex);
        childIndex++;
        ModelObject nextModel = ModelObject.GetModelObj(childList,nextKey);
        return nextModel;
    }

    /**
     * Get current iterator
     * @return the current iterator place
     */
    public ModelObject getCurrent(){
        return curr;
    }
}
