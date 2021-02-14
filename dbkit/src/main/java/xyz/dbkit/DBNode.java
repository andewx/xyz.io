package xyz.dbkit;

import xyz.model.ModelObject;

public class DBNode {
    String name;
    String file;
    public ModelObject rootGraph;
    boolean hasChanged;

    public DBNode(String DBName, String DBFilePath){ //Instantiates New DBNode Object
        super();
        name = DBName;
        file = DBFilePath;
        rootGraph = new ModelObject();
        hasChanged = false;
    }

}
