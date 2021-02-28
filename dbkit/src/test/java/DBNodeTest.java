import org.json.JSONObject;
import xyz.dbkit.*;
import org.junit.jupiter.api.Test;
import xyz.model.ModelObject;
import xyz.model.Site;
import xyz.model.Template;


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
        mDataBase.CreateNode("Node1");
        mDataBase.CreateNode("Node2");

        DBNode TemplateNode = mDataBase.GetNode("Sites");

       assertNotNull(TemplateNode);

        Template Header = new Template("Light-UI-Header", "");
        Template Body = new Template("Light-UI-Body", "");
        Template Footer = new Template("Light-UI-Footer", "");
        Site MySite = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");
        MySite.addModel(Header);
        MySite.addModel(Body);
        MySite.addModel(Footer);
        TemplateNode.AddModel(MySite);

        ModelObject modelSite= mDataBase.findKey(TemplateNode,"Site", MySite.getUID());
        assertNotNull(modelSite);
        JSONObject myTemplates = modelSite.getModels("Template");
        System.out.print("Template: " + modelSite.getName() + "{\n");
        for(String key : myTemplates.keySet()){
            Template mObj = (Template)myTemplates.get(key);
            assertNotNull(mObj);
            System.out.println(mObj.getName());
        }

        System.out.println("}");

        TemplateNode.WriteNode(TemplateNode.GetFile());

    }

    @Test
    public void DeleteNode() throws IOException {
            DBNode theme = mDataBase.GetNode("Themes");
            boolean delete = mDataBase.DeleteNode("Themes");
            theme = mDataBase.GetNode("Themes");
           assertTrue(delete);
            assertNull(theme);
    }





}
