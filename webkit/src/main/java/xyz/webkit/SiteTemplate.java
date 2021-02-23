package xyz.webkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.HashMap;

public class SiteTemplate {
    String PathFolder;
    String HTML;
    HashMap<String,String> PropertyMap;
    Pattern KeyPattern;
    Matcher KeyMatcher;

    public SiteTemplate(){
        PathFolder = "templates";
        HTML = "";
        PropertyMap = new HashMap<String,String>();
        KeyPattern = Pattern.compile("@:([a-zA-Z0-9]+)");
        KeyMatcher = null;
    }

    public String GetHtml(){
        return HTML;
    }

    public void AddKey(String key, String Value){
        String val = PropertyMap.get(key);
        if(val == null){
            PropertyMap.put(key,Value);
        }
        else{
            PropertyMap.replace(key,Value);
        }
    }

    public HashMap<String,String> GetMap(){
        return PropertyMap;
    }

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

    public void setHTML(String html){
        HTML = html;
    }

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

    public ArrayList<String> GetKeys(){
        ArrayList<String> myList = new ArrayList<String>();
        KeyMatcher = KeyPattern.matcher(HTML);
        while(KeyMatcher.find()){
            myList.add(KeyMatcher.group(1));
        }
        return myList;
    }




}
