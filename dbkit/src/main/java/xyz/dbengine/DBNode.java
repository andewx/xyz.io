package xyz.dbengine;

import xyz.model.ModelObject;

import java.io.*;

public class DBNode {
    String name;
    File file;
    public ModelObject rootGraph;
    String syncID; //Last sync id
    String modifyID; //Last modify id

    public DBNode(String DBName, String DBFilePath){ //Instantiates New DBNode Object
        super();
        name = DBName;
        file = new File(DBFilePath);
        rootGraph = new ModelObject();
    }

    public void Open() throws FileNotFoundException {

        try {
            FileReader myReader = new FileReader(file);
            StringBuilder myBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(myReader);
            String inStr;
            while((inStr = br.readLine()) != null){
                myBuilder.append(inStr); //Internally JSON records a single line
            }
            myReader.close();
            ModelObject newGraph = new ModelObject(inStr);
            rootGraph = newGraph;

        }catch(FileNotFoundException e){
            throw e;
        } catch (IOException e) {
            //End of file reached
        }

    }

    public void Write() {
        try{
            FileWriter myWrite = new FileWriter(file);
            myWrite.write(rootGraph.toJson());
            myWrite.close();
        }catch(IOException e){
            //End of write
        }
    }



}
