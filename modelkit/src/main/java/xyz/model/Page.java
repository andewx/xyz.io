package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Page extends ModelObject{
    protected String SiteID;
    protected String Filename;
    public Page(String name, String siteID, String fileName){
        super();
        ClassName = "Page";
        Name = name;
        SiteID = siteID;
        Filename = fileName;
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("SiteID", SiteID);
        put("Filename", Filename);
    }


    public Page(String json){
        super(json);
        SiteID = (String)get("SiteID");
        Filename = getString("Filename");
        put("SiteID", SiteID);
        put("Filename", Filename);
    }

    public Page(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            SiteID = (String)jObj.get("SiteID");
            Filename = jObj.getString("Filename");
            put("SiteID", SiteID);
            put("Filename", Filename);

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

    public String getFilename(){ return Filename;}
    public void setFilename(String file){
        Filename = file;
        updateKey("Filename", Filename);
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

