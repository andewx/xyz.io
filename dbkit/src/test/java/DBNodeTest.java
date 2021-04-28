import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import xyz.dbkit.DBMain;
import xyz.dbkit.DBNode;
import xyz.model.ModelObject;
import xyz.model.Page;
import xyz.model.Site;

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

        DBNode PageNode = mDataBase.GetNode("Sites");

       assertNotNull(PageNode);

        Page Header = new Page("Light-UI-Header", "","");
        Page Body = new Page("Light-UI-Body", "","");
        Page Footer = new Page("Light-UI-Footer", "","");
        Site MySite = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");
        MySite.addModel(Header);
        MySite.addModel(Body);
        MySite.addModel(Footer);
        PageNode.AddModel(MySite);

        ModelObject modelSite= mDataBase.findKey(PageNode,"Site", MySite.getUID());
        assertNotNull(modelSite);
        JSONObject myPages = modelSite.getModels("Page");
        System.out.print("Page: " + modelSite.getName() + "{\n");
        for(String key : myPages.keySet()){
            Page mObj = (Page)myPages.get(key);
            assertNotNull(mObj);
            System.out.println(mObj.getName());
        }

        System.out.println("}");

        PageNode.WriteNode(PageNode.GetFile());

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
