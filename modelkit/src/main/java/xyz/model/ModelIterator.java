package xyz.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class ModelIterator implements Iterator<ModelObject> {
    //BFS Tree Next
    public ModelObject currentNode;
    public ModelObject childNode;
    public Iterator<String> keyIter;

    public ModelIterator(ModelObject node){
        super();
        currentNode = node;
        keyIter = currentNode.keys();
        if (keyIter.hasNext()) {
            childNode = (ModelObject) currentNode.get(keyIter.next());
        }
    }
    @Override
    public boolean hasNext() {
       return keyIter.hasNext();
    }

    @Override
    public ModelObject next() {
        childNode = (ModelObject) currentNode.get(keyIter.next());
        return childNode;
    }

    @Override
    public void remove() {
        currentNode.remove(childNode.getUID());
    }

    @Override
    public void forEachRemaining(Consumer<? super ModelObject> action) {
        while(hasNext()){
            action.accept(next());
        }
    }
}
