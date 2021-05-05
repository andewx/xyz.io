package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author briananderson
 * @version 1.0
 * Represents site page in framework. Pages in the xyz framework are stored internal to the Site model
 * therefore SiteID identifies the holding parent class of a page. Also stores the associated Page HTML representative filename
 * and an associated CSS File. Most importantly, Pages as editable content store themselves in JSON format.
 */
public class Page extends ModelObject{
    protected String SiteID;
    protected String Filename;
    protected String CssFile;
    protected String JsonData;

    /**
     * Constructs page
     * @param name name of page
     * @param siteID Site UID Key
     * @param fileName HTML Filename associated with page
     */
    public Page(String name, String siteID, String fileName){
        super();
        ClassName = "Page";
        Name = name;
        SiteID = siteID;
        UID = Name;
        Filename = fileName;
        JsonData = "{}";
        CssFile = name+".css";
        updateKey("Name", Name);
        updateKey("UID", UID);
        updateKey("ClassName", ClassName);
        put("SiteID", SiteID);
        put("Filename", Filename);
        put("CssFile", CssFile);
        put("JsonData", JsonData);
    }

    /**
     * Constructs page model from JSON string
     * @param json - Well formatted JSON of Page
     */
    public Page(String json){
        super(json);
        SiteID = (String)get("SiteID");
        Filename = getString("Filename");
        CssFile = getString("CssFile");
        JsonData = getString("JsonData");
        put("CssFile", CssFile);
        put("SiteID", SiteID);
        put("Filename", Filename);
        put("JsonData", JsonData);

    }

    /**
     * Constructs page model of JSONOBject
     * @param jObj Page object in JSONObject form
     */
    public Page(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            SiteID = (String)jObj.get("SiteID");
            Filename = jObj.getString("Filename");
            CssFile = getString("CssFile");
            JsonData = getString("JsonData");

            put("CssFile", CssFile);
            put("SiteID", SiteID);
            put("Filename", Filename);
            put("JsonData", JsonData);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    /**
     * Getter for SiteID
     * @return SiteID
     */
    public String getSiteID(){
        return SiteID;
    }

    /**
     * Setter SiteID
     * @param siteID SiteID
     */
    public void setSiteID(String siteID){
        SiteID = siteID;
        updateKey("SiteID", SiteID);
    }

    /**
     * Getter JsonData
     * @return JsonData
     */
    public String getJsonData(){
        return JsonData;
    }

    /**
     * Setter JsonData
     * @param data JsonData to be set
     */
    public void setJsonData(String data){
        JsonData = data;
        updateKey("JsonData", JsonData);
    }

    /**
     * Getter filename
     * @return HTML filename associated with page
     */
    public String getFilename(){ return Filename;}

    /**
     * Setter Filename
     * @param file - filename to be set
     */
    public void setFilename(String file){
        Filename = file;
        updateKey("Filename", Filename);
    }

    /**
     * Getter CSS File
     * @return Css Filename
     */
    public String getCssFile(){return CssFile;}

    /**
     * Setter CSS File
     * @param file Css filename
     */
    public void setCssFile(String file){
        CssFile = file;
        updateKey("CssFile", CssFile);
    }

    /**
     * Form tag element for page if desired
     * @return
     */
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

    /**
     * Input tags for page if desire
     * @param key key value of input tag
     * @return
     */
    @Override
    public String GetInputTag(String key){
        return "<input type='text' maxLength='45'" +" value='" + get(key) + " id='" + key + "' name='" + key + "'></input>";
    }

}

