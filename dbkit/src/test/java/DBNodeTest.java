import org.json.JSONObject;
import xyz.dbkit.*;
import org.junit.jupiter.api.Test;
import xyz.model.Item;
import xyz.model.ModelObject;
import xyz.model.Recipe;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class DBNodeTest {
    DBMain mDataBase;

    public DBNodeTest(){
        boolean db_create = true;
        try {
            mDataBase = new DBMain("xyz-database");
            assertTrue(db_create);
        }catch(IOException e){
            System.out.println("Database Error\n");
            db_create = false;
            assertTrue(db_create);
            e.printStackTrace();
        }
    }


    @Test
    public void CreateNodes() throws IOException {
        mDataBase.CreateNode("Users", "Users.keys");
        mDataBase.CreateNode("Groups", "Groups.keys");
        mDataBase.CreateNode("Recipes", "Recipes.keys");

        ModelObject RecipeMapping = mDataBase.GetFileManager().getModel("FileMap","Recipes" );
        assertNotNull(RecipeMapping);
        DBNode RecipeNode = mDataBase.GetNode("Recipes");
        assertNotNull(RecipeMapping);

        Item Coffee = new Item("Coffee", "fresh", "oz", 12);
        Item Sugar = new Item("Sugar", "white", "gram", 4);
        Item Milk = new Item("Milk", "whole", "oz", 2);
        Recipe Latte = new Recipe("Latte","none","drink", "hot", "cafe", 0, 4, 2);
        Latte.addModel(Coffee);
        Latte.addModel(Sugar);
        Latte.addModel(Milk);
        RecipeNode.AddModel(Latte);

        ModelObject modelLatte = RecipeNode.rootGraph.getModel("Recipe", Latte.getUID());
        assertNotNull(modelLatte);
        JSONObject latteItems = modelLatte.getModels("Item");
        System.out.print("Recipe: " + modelLatte.getName() + "{\n");
        for(String key : latteItems.keySet()){
            Item mObj = (Item)latteItems.get(key);
            assertNotNull(mObj);
            System.out.print(mObj.getName()+ ": " + String.format("%d .",(mObj.get("Amount"))) + mObj.get("Unit") + "\n");
        }

        System.out.println("}");

        RecipeNode.WriteNode(RecipeNode.GetFile());

    }

    @Test
    public void DeleteNode() throws IOException {
            DBNode Recipe = mDataBase.GetNode("Recipes");
            boolean delete = mDataBase.DeleteNode("Recipes");
            Recipe = mDataBase.GetNode("Recipes");
           assertTrue(delete);
            assertNull(Recipe);
    }





}
