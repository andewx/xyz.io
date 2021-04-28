import org.json.JSONException;
import xyz.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class ModelTest {

    ArrayList<ModelObject> myPages;
    Page i0;
    Page i1;
    Page i2;
    Site i3;

    public ModelTest(){
        myPages = new ArrayList<ModelObject>();
        i0 = new Page("Light-UI-Header", "MySite","");
        i1 = new Page("Light-UI-Layout", "MySite","");
        i2 = new Page("Light-UI-Footer", "MySite","");
        i3 = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");


        i3.addModel(i0);
        i3.addModel(i1);
        i3.addModel(i2);

        myPages.add(i0);
        myPages.add(i1);
        myPages.add(i2);
        myPages.add(i3);

    }


    @Test
    public void TestGetModelUID() {
        Site site = (Site)myPages.get(3);
        Page Page = (Page)site.getModel(i2.getModelName(), i2.getUID());
        assertEquals( i2.getUID(),(String)Page.get("UID"));
    }

    @Test
    public void TestJSONOut(){
        ModelObject site = (ModelObject)myPages.get(3);
        System.out.println(site.toString());
    }

    @Test
    public void TestIngestJSON(){
        Site site = (Site)myPages.get(3);
        String jsonSite = site.toString();
        Site ingestSite = new Site(jsonSite);
        assertEquals(ingestSite.get("UID"), site.get("UID"));
    }

    @Test
    public void TestIngestInternalJSON(){
        try {
            Site site = (Site) myPages.get(3);
            Page Page = (Page) myPages.get(1);
            String jsonSite = site.toString();
            Site ingestSite = new Site(jsonSite);
            ModelObject ingestPage = (ModelObject) ingestSite.getModel("Page", Page.getUID());
            assertEquals(ingestPage.getUID(),Page.getUID());
        }catch(JSONException e){
            System.out.println("Internal (Page) of ingestSite not found\nPrinting Stack Trace\n");

        }

    }

    @Test
    public void TestIterator(){
        Site site = (Site)myPages.get(3);
        int i = 0;
        ModelIterator myIter = new ModelIterator(site);
        while(myIter.hasNext()){
            ModelObject thisModel = myIter.next();
            if(thisModel.getModelName().equals("Page")){
                Page myPage = (Page)thisModel;
                assertNotNull(myPage);
                i++;
            }
        }
        assertEquals(i,3);
    }




}
