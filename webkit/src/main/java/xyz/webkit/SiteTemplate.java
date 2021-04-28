package xyz.webkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SiteTemplate does the lifting for the parsing of Template HTML files (does not need to be HTML) replacing keys
 * identified with @:SomeKey to be replaced. This helps our Framework inject, combine, and format HTML responses.
 */
public class SiteTemplate {
    String PathFolder;
    String HTML;
    HashMap<String,String> PropertyMap;
    Pattern KeyPattern;
    Matcher KeyMatcher;

    /**
     * Constructor for Site Template, looks to path folder templates when loading in Files
     */
    public SiteTemplate(){
        PathFolder = "templates";
        HTML = "";
        PropertyMap = new HashMap<String,String>();
        KeyPattern = Pattern.compile("@:([a-zA-Z0-9]+)");
        KeyMatcher = null;
    }

    /**
     * Returns file string HTML
     * @return HTML string
     */
    public String GetHtml(){
        return HTML;
    }

    /**
     * Adds a key/value pair identifier into the object for searching and replacing raw HTML string
     * @param key @:SomeKey
     * @param Value Value to replace with
     */
    public void AddKey(String key, String Value){
        String val = PropertyMap.get(key);
        if(val == null){
            PropertyMap.put(key,Value);
        }
        else{
            PropertyMap.replace(key,Value);
        }
    }

    /**
     * Gets @:SomeKey map stored in the current object
     * @return
     */
    public HashMap<String,String> GetMap(){
        return PropertyMap;
    }

    /**
     * Loads in a template file from the given string path
     * @param Name string path of the file to be loaded
     * @return loaded HTML
     */
    public String GetTemplate(String Name){
        String TemplateFile = Name;
        Path TemplatePath = Path.of(TemplateFile);
        try{
            HTML = Files.readString(TemplatePath);
            return HTML;

        }catch(IOException e){
            System.out.println("Could not get Template");
            return "";
        }
    }

    /**
     * Sets HTML content of the SiteTemplate Object
     * @param html
     */
    public void setHTML(String html){
        HTML = html;
    }

    /**
     * Replaces keys from the stored Key/Value pair map
     * @return HTML string
     */
    public String ReplaceKeys(){
        KeyMatcher = KeyPattern.matcher(HTML);
        StringBuffer sb = new StringBuffer();
        while(KeyMatcher.find()){
            String key = KeyMatcher.group(1);
            String value = PropertyMap.get(key);
            try {
                KeyMatcher.appendReplacement(sb, value);
            }catch(NullPointerException e){
                System.out.println("Key: " + key + "Could not be replaced");
            }
        }
        KeyMatcher.appendTail(sb);
        HTML = sb.toString();
        return HTML;
    }

    /**
     * Gets the associated keys in the current SiteTemplate object
     * @return
     */
    public ArrayList<String> GetKeys(){
        ArrayList<String> myList = new ArrayList<String>();
        KeyMatcher = KeyPattern.matcher(HTML);
        while(KeyMatcher.find()){
            myList.add(KeyMatcher.group(1));
        }
        return myList;
    }




}
