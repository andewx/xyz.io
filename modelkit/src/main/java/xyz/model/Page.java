package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Page extends ModelObject{
    protected String SiteID;

    public Page(String name, String siteID){
        super();
        ClassName = "Page";
        Name = name;
        SiteID = siteID;
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("SiteID", SiteID);
    }


    public Page(String json){
        super(json);
        SiteID = (String)get("SiteID");
        put("SiteID", SiteID);
    }

    public Page(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            SiteID = (String)jObj.get("SiteID");
            put("SiteID", SiteID);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getSiteID(){
        return SiteID;
    }

    public void setSiteID(String siteID){
        SiteID = siteID;
        updateKey("SiteID", SiteID);
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
        return "<input type='text' maxLength='45'" +" value='" + get(key) + " id='" + key + "' name='" + key + "'></input>";
    }

}

