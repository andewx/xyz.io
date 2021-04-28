
import xyz.dbkit.*;
import org.junit.jupiter.api.Test;
import xyz.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DBFindTests {

   DBMain mDatabase;
   String headerUID;

   public DBFindTests() throws IOException {
       try {
           mDatabase = new DBMain("MyDatabase");
       } catch (IOException e) {
           System.out.println("Error Creating Database");
           throw e;
       }

       DBNode PageNode = mDatabase.GetNode("Sites");

       assertNotNull(PageNode);

       Page Header = new Page("Light-UI-Header", "<div>","");
       Page Body = new Page("Light-UI-Body", "<div>","");
       Page Footer = new Page("Light-UI-Footer", "<div>","");
       headerUID = Header.getUID();
       Site MySite = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");
       MySite.addModel(Header);
       MySite.addModel(Body);
       MySite.addModel(Footer);
       PageNode.AddModel(MySite);

   }

    @Test
    public void FindModelID(){
       DBNode Sites = mDatabase.GetNode("Sites");
       Page header =(Page) mDatabase.findKey(Sites,"Page", "Light-UI-Header");
        if(header != null) {
            assertEquals(0, header.getUID().compareTo(headerUID));
        }else{
            assertNotNull(header);
        }
       System.out.println("Light UI Header Found in site");

    }

    @Test
    public void FindExactPropertys(){
       HashMap<String,String> propVals = new HashMap<>();
       propVals.put("Name", "Light-UI-Header");
       DBNode Sites = mDatabase.GetNode("Sites");
       ArrayList<ModelObject> Pages = mDatabase.findExact(Sites, "Page", propVals);
       for(ModelObject i : Pages){
           Page myPage = (Page)i;
           String name = (String)i.get("Name");
           assertEquals(0, name.compareTo("Light-UI-Header"));
       }
    }

    @Test
    public void FindSome(){
        HashMap<String,String> propVals = new HashMap<>();
        propVals.put("Name", "Light-UIX");
        propVals.put("HTML", "<div>");
        DBNode Sites = mDatabase.GetNode("Sites");
        ArrayList<ModelObject> Pages = mDatabase.findSome(Sites, "Page", propVals);
        assertEquals(3, Pages.size());
    }

    @Test
    public void FindSimilar(){
        DBNode Sites = mDatabase.GetNode("Sites");
        ArrayList<ModelObject> Pages = mDatabase.findStartsWith(Sites, "Page","Light");
        assertEquals(3, Pages.size());
    }


}
