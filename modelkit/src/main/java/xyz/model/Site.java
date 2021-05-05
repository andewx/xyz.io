package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Site extends ModelObject{
    protected String Description;
    protected String RestURL;
    protected String Title;
    protected String ThemeID;

    public Site(String name, String description, String restURL, String title){
        super();
        Description = description;
        RestURL = restURL;
        ClassName = "Site";
        Name = name;
        UID = name;
        Title = title;
        ThemeID = "";

        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        updateKey("UID", UID);
        put("Description", Description);
        put("RestURL", RestURL);
        put("Title", Title);
        put("ThemeID", ThemeID);
    }

    public Site(String name, String description, String restURL, String title, String theme){
        super();
        Description = description;
        RestURL = restURL;
        ClassName = "Site";
        Name = name;
        UID = name;
        Title = title;
        ThemeID = theme;

        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        updateKey("UID", UID);
        put("Description", Description);
        put("RestURL", RestURL);
        put("Title", Title);
        put("ThemeID", ThemeID);
    }

    public Site(String json){
        super(json);
        Description = (String)get("Description");
        RestURL = (String)get("RestURL");
        UID = Name;
        Title = (String)get("Title");
        ThemeID = (String)get("ThemeID");
        updateKey("UID", UID);
        put("Description", Description);
        put("RestURL", RestURL);
        put("Title", Title);
        put("ThemeID", ThemeID);
    }

    public Site(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            Description= (String) jObj.getString("Description");
            RestURL = (String) jObj.getString("RestURL");
            Title = (String) jObj.getString("Title");
            UID = Name;
            ThemeID = (String) jObj.getString("ThemeID");
            updateKey("UID", UID);
            put("Title", Title);
            put("Description", Description);
            put("RestURL", RestURL);
            put("ThemeID", ThemeID);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
        updateKey("Description", Description);
    }

    public String getThemeID(){return ThemeID;}

    public void setThemeID(String theme){
        ThemeID = theme;
        updateKey("ThemeID", ThemeID);
    }

    public String getRestURL() {
        return RestURL;
    }

    public void setRestURL(String restURL) {
        RestURL = restURL;
        updateKey("RestURL", RestURL);
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
        updateKey("Title", Title);
    }

    public String getKey(){
        return "Name";
    }

    public void AddPage(Page pgObj){
        try{
            JSONObject pages = this.getJSONObject("Pages");
            pages.put(pgObj.getUID(), pgObj);
        }catch(Exception e){
            System.out.println("Could not add page to site");
        }
    }

    public Page getPage(String page){
        try{
            JSONObject pages = this.getJSONObject("Pages");
            return new Page(pages.getJSONObject(page));
        }catch(Exception e){
            System.out.println("Could not find site " + Name + " page " + page);
            return null;
        }
    }

    public void removePage(String page){
        try{
            JSONObject pages = this.getJSONObject("Pages");
            pages.remove(page);
        }catch(Exception e){
            System.out.println("Could not find page " + page);
        }
    }

    @Override
    public String Form(){ //Override for custom form element processing
        StringBuilder sb = new StringBuilder();
        sb.append("<form id='" + getModelName() + "' class='model-form'><ul><li><div class='form-label'>");
        boolean required = false;
        for(String key : keySet()){ //No UID / ClassName
            if (!key.equals("UID") && !key.equals("ClassName")){ //Hidden Fields
                sb.append(key);
                sb.append(GetInputTag(key));
                sb.append( "</div></li>");
            }
        }
        sb.append("</ul></form>");
        return sb.toString();
    }

    @Override
    public String GetInputTag(String key){
        String type = "<input type='text' maxLength='45'" +" value='" + get(key) + " id='" + key + "' name='" + key + "'></input>";
        if(key.equals("Description")){
            type = "<textarea rows='50' cols'3'" +" value='" + get(key) + " id='" + key + "' name='" + key + "' required></input>";
        }
        return type;
    }
}
