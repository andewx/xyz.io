import xyz.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ModelTest {

    ArrayList<Model> myItems;
    Item i0;
    Item i1;
    Item i2;
    Item i3;

    public ModelTest(){
        myItems = new ArrayList<Model>();
        i0 = new Item();
        i1 = new Item();
        i2 = new Item();
        i3 = new Item();


        i0.Title = "Mozarella";
        i0.Description = "Softened";
        i0.Unit = ".oz";
        i0.Amount = 4;

        i1.Title = "Dough";
        i1.Description ="White Flour";
        i1.Unit = ".oz";
        i1.Amount = 6;

        i2.Title = "Marinara";
        i2.Description = "Tomato & Herbs";
        i2.Unit = ".oz";
        i2.Amount = 4;

        i3.Title = "Pizza";
        i3.Description = "Mozarella & Red Herb Sauce";
        i3.Unit = "";
        i3.Amount = 1;

        i3.addModel(i0);
        i3.addModel(i1);
        i3.addModel(i2);

        myItems.add(i0);
        myItems.add(i1);
        myItems.add(i2);
        myItems.add(i3);

        for (Model i : myItems){
            i.syncModel(false); //Sync Property Lists
        }
    }

    @Test
    public void TestProperty(){
          Item Pizza = (Item)myItems.get(3); //Pizza should have all items
          Item Mozarella = (Item)myItems.get(0);
          assertEquals(Pizza.Title.compareTo(Pizza.getProperty("Title")),0);
    }

    @Test
    public void TestGetModelUID() {

        Item Pizza = (Item) myItems.get(3); //Pizza should have all items
        Item Mozarella = (Item) myItems.get(0);
        //Get Model By UID
        Item PMozz = (Item) Pizza.getModel(Mozarella.UID);
        assertEquals(0, PMozz.Title.compareTo("Mozarella"));
        assertEquals(0, PMozz.UID.compareTo(Mozarella.UID));
    }

    @Test
    public void TestAllItemsExist(){
        //Get Model Listing
        Item Pizza = (Item) myItems.get(3); //Pizza should have all items
        Item Mozarella = (Item) myItems.get(0);
        ArrayList<Model> PizzaItems = Pizza.hasModels("Item");
        int index = 0;
        for(Model item : PizzaItems){
            Item myItem = (Item)item;
            assertEquals(0,myItem.getProperty("Title").compareTo(myItem.Title));
        }
    }




}
