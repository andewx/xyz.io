import org.json.JSONException;
import xyz.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class ModelTest {

    ArrayList<ModelObject> myItems;
    Item i0;
    Item i1;
    Item i2;
    Item i3;

    public ModelTest(){
        myItems = new ArrayList<ModelObject>();
        i0 = new Item();
        i1 = new Item();
        i2 = new Item();
        i3 = new Item();


        i0.setName("Mozarella");
        i0.setDescription("Softened");
        i0.setUnit(  ".oz");
        i0.setAmount(4);

        i1.setName("Dough");
        i1.setDescription("White Flour");
        i1.setUnit(".oz");
        i1.setAmount( 6);

        i2.setName("Marinara");
        i2.setDescription( "Tomato & Herbs");
        i2.setUnit(".oz");
        i2.setAmount( 4);

        i3.setName("Pizza");
        i3.setDescription( "Mozarella & Red Herb Sauce");
        i3.setUnit("");
        i3.setAmount( 1);


        i3.addModel(i0);
        i3.addModel(i1);
        i3.addModel(i2);

        myItems.add(i0);
        myItems.add(i1);
        myItems.add(i2);
        myItems.add(i3);

    }

    @Test
    public void TestProperty(){
        ModelObject pizza = (ModelObject)myItems.get(3);
        ModelObject dough = (ModelObject)myItems.get(1);
        assertEquals(i3.getName(), (String)pizza.get("Name"));

    }

    @Test
    public void TestGetModelUID() {
        ModelObject pizza = (ModelObject)myItems.get(3);
        ModelObject dough = (ModelObject)pizza.getModel(i2.getModelName(), i2.getUID());
        assertEquals( i2.getUID(),(String)dough.get("UID"));
    }

    @Test
    public void TestJSONOut(){
        ModelObject pizza = (ModelObject)myItems.get(3);
        System.out.println(pizza.toString());
    }

    @Test
    public void TestIngestJSON(){
        ModelObject pizza = (ModelObject)myItems.get(3);
        String jsonPizza = pizza.toString();
        ModelObject ingestPizza = new ModelObject(jsonPizza);
        assertEquals(ingestPizza.get("UID"), pizza.get("UID"));
    }

    @Test
    public void TestIngestInternalJSON(){
        try {
            ModelObject pizza = (ModelObject) myItems.get(3);
            ModelObject dough = (ModelObject) myItems.get(1);
            String jsonPizza = pizza.toString();
            ModelObject ingestPizza = new ModelObject(jsonPizza);
            ModelObject ingestDough = (ModelObject) ingestPizza.getModel("Item", dough.getUID());
            assertEquals(ingestDough.getUID(),dough.getUID());
        }catch(JSONException e){
            System.out.println("Internal (Item) of ingestPizza not found\nPrinting Stack Trace\n");

        }

    }




}
