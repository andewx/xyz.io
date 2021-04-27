import org.json.JSONException;
import xyz.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class ModelTest {

    ArrayList<ModelObject> myTemplates;
    Template i0;
    Template i1;
    Template i2;
    Site i3;

    public ModelTest(){
        myTemplates = new ArrayList<ModelObject>();
        i0 = new Template("Light-UI-Header", "");
        i1 = new Template("Light-UI-Layout", "");
        i2 = new Template("Light-UI-Footer", "");
        i3 = new Site("MySite", "Example Site", "/mysite", "My Site Is Awesome");


        i3.addModel(i0);
        i3.addModel(i1);
        i3.addModel(i2);

        myTemplates.add(i0);
        myTemplates.add(i1);
        myTemplates.add(i2);
        myTemplates.add(i3);

    }


    @Test
    public void TestGetModelUID() {
        Site site = (Site)myTemplates.get(3);
        Template template = (Template)site.getModel(i2.getModelName(), i2.getUID());
        assertEquals( i2.getUID(),(String)template.get("UID"));
    }

    @Test
    public void TestJSONOut(){
        ModelObject site = (ModelObject)myTemplates.get(3);
        System.out.println(site.toString());
    }

    @Test
    public void TestIngestJSON(){
        Site site = (Site)myTemplates.get(3);
        String jsonSite = site.toString();
        Site ingestSite = new Site(jsonSite);
        assertEquals(ingestSite.get("UID"), site.get("UID"));
    }

    @Test
    public void TestIngestInternalJSON(){
        try {
            Site site = (Site) myTemplates.get(3);
            Template template = (Template) myTemplates.get(1);
            String jsonSite = site.toString();
            Site ingestSite = new Site(jsonSite);
            ModelObject ingestTemplate = (ModelObject) ingestSite.getModel("Template", template.getUID());
            assertEquals(ingestTemplate.getUID(),template.getUID());
        }catch(JSONException e){
            System.out.println("Internal (Template) of ingestSite not found\nPrinting Stack Trace\n");

        }

    }

    @Test
    public void TestIterator(){
        Site site = (Site)myTemplates.get(3);
        int i = 0;
        ModelIterator myIter = new ModelIterator(site);
        while(myIter.hasNext()){
            ModelObject thisModel = myIter.next();
            if(thisModel.getModelName().equals("Template")){
                Template myTemplate = (Template)thisModel;
                assertNotNull(myTemplate);
                i++;
            }
        }
        assertEquals(i,3);
    }




}
