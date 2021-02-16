package xyz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class ModelIterator implements Iterator<ModelObject> {
    //BFS Tree Next
    public ModelObject currentNode;
    public ModelObject childNode;
    public ArrayList<String> keys;
    public int index;
    public int length;
    public String key;

    public ModelIterator(ModelObject node){
        super();
        currentNode = node;
        keys = ModelKeys.ModelKeys();
        index = 0;
        length = keys.size();

        if (length > 0) {
            key = keys.get(0);
        }
    }
    @Override
    public boolean hasNext() {
       if(index < (length -1)){
           return true;

       }
        return false;
    }

    @Override
    public ModelObject next() {
        index++;
        key = keys.get(index);
        return (ModelObject)currentNode.get(key);
    }

    @Override
    public void remove() {
        currentNode.remove(key);
    }

    @Override
    public void forEachRemaining(Consumer<? super ModelObject> action) {
        while(hasNext()){
            action.accept(next());
        }
    }
}
