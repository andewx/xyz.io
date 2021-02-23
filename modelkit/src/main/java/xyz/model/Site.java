package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Site extends ModelObject{
    protected String Description;
    protected String RestURL;
    protected String Title;

    public Site(String name, String description, String restURL, String title){
        super();
        Description = description;
        RestURL = restURL;
        ClassName = "Site";
        Name = name;
        UID = name;
        Title = title;

        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        updateKey("UID", UID);
        put("Description", Description);
        put("RestURL", RestURL);
        put("Title", Title);

    }

    public Site(String json){
        super(json);
        Description = (String)get("Description");
        RestURL = (String)get("RestURL");
        Title = (String)get("Title");
        put("Description", Description);
        put("RestURL", RestURL);
        put("Title", Title);
    }

    public Site(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            Description= (String) jObj.get("Description");
            RestURL = (String) jObj.get("RestURL");
            Title = (String) jObj.get("Title");
            put("Title", Title);
            put("Description", Description);
            put("RestURL", RestURL);

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
