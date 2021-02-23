
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

       DBNode TemplateNode = mDatabase.GetNode("Sites");

       assertNotNull(TemplateNode);

       Template Header = new Template("Light-UI-Header");
       Template Body = new Template("Light-UI-Body");
       Template Footer = new Template("Light-UI-Footer");
       headerUID = Header.getUID();
       Site MySite = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");
       MySite.addModel(Header);
       MySite.addModel(Body);
       MySite.addModel(Footer);
       TemplateNode.AddModel(MySite);

   }

    @Test
    public void FindModelID(){
       DBNode Sites = mDatabase.GetNode("Sites");
       Template header =(Template) mDatabase.findKey(Sites,"Template", "Light-UI-Header");

       assertEquals(0, header.getUID().compareTo(headerUID));
       System.out.println("Light UI Header Found in site");

    }

    @Test
    public void FindExactPropertys(){

       HashMap<String,String> propVals = new HashMap<>();
       propVals.put("Name", "Light-UI-Header");
       DBNode Sites = mDatabase.GetNode("Sites");
       ArrayList<ModelObject> templates = mDatabase.findExact(Sites, "Template", propVals);

       for(ModelObject i : templates){
           Template myTemplate = (Template)i;
           String name = (String)i.get("Name");
           assertEquals(0, name.compareTo("Light-UI-Header"));
       }

    }


}
