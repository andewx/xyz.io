import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.webkit.SiteTemplate;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateTest {
    SiteTemplate myTemplate;
    ArrayList<String> keys;
    public TemplateTest(){
        keys = new ArrayList<String>();
        keys.add("title");
        keys.add("first");
        keys.add("last");
        myTemplate = new SiteTemplate("");
        myTemplate.GetTemplate("index.html");
    }

    @Test
    public void TestMatches(){
        String matchStr = "@:title";
        boolean done = false;
        Pattern patt = Pattern.compile("@:([a-zA-Z0-9]+)");
        Matcher myMatch = patt.matcher(matchStr);
        boolean match = myMatch.matches();
        Assertions.assertTrue(match);

    }


    @Test
    public void KeysTest(){
        ArrayList<String> myKeys = myTemplate.GetKeys();
        int index =0;
        for(String item : keys){
            assertEquals(0, item.compareTo(myKeys.get(index++)));
        }
    }

    @Test
    public void TestReplace(){
        myTemplate.AddKey("title", "Template Parser");
        myTemplate.AddKey("first", "Brian");
        myTemplate.AddKey("last", "Anderson");
        myTemplate.ReplaceKeys();
        SiteTemplate nTemplate = new SiteTemplate("");
        nTemplate.GetTemplate("test-index.html");
        System.out.println(myTemplate.GetHtml());
        assertEquals(0, myTemplate.GetHtml().compareTo(nTemplate.GetHtml()));
    }

}
