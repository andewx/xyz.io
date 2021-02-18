
import xyz.dbkit.*;
import org.junit.jupiter.api.Test;
import xyz.model.Item;
import xyz.model.ModelObject;
import xyz.model.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DBFindTests {

   DBMain mDatabase;
   String milkUID;

   public DBFindTests() throws IOException {
       try {
           mDatabase = new DBMain("MyDatabase");
       } catch (IOException e) {
           System.out.println("Error Creating Database");
           throw e;
       }

       ModelObject RecipeMapping = mDatabase.GetFileManager().getModel("FileMap","Recipes" );
       assertNotNull(RecipeMapping);
       DBNode RecipeNode = mDatabase.GetNode("Recipes");

       if(RecipeNode == null){
          RecipeNode = mDatabase.CreateNode("Recipes", "Recipes.keys");
       }

       Item Coffee = new Item("Coffee", "fresh", "oz", 12);
       Item Sugar = new Item("Sugar", "white", "gram", 4);
       Item Milk = new Item("Milk", "whole", "oz", 2);
       milkUID = Milk.getUID();
       Recipe Latte = new Recipe("Latte","none","drink", "hot", "cafe", 0, 4, 2);
       Latte.addModel(Coffee);
       Latte.addModel(Sugar);
       Latte.addModel(Milk);
       RecipeNode.AddModel(Latte);

   }

    @Test
    public void FindModelID(){
       DBNode recipes = mDatabase.GetNode("Recipes");
       Item milk =(Item) mDatabase.findKey(recipes,"Item", milkUID);

       assertEquals(0, milk.getUID().compareTo(milkUID));
       System.out.println("Milk found in recipe Coffee");

    }

    @Test
    public void FindExactPropertys(){

       HashMap<String,String> propVals = new HashMap<>();
       propVals.put("Unit", "oz");
       DBNode recipes = mDatabase.GetNode("Recipes");
       ArrayList<ModelObject> liquids = mDatabase.findExact(recipes, "Item", propVals);

       for(ModelObject i : liquids){
           Item item = (Item)i;
           String unit = (String)i.get("Unit");
           assertEquals(0, unit.compareTo("oz"));
       }

    }


}
