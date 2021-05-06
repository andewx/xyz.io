package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Theme Models have associated HTML files which are currently default set to Index.html by the
 * controller frameworks. Themes are also associated with a Palette string which should be a JSONArray passed
 * by the client application.
 */
public class Theme extends ModelObject {
    protected String HtmlFile;
    protected String Palette;

    /**
     * Palette setter
     * @param palette JSONArray string of palette values 1-5
     * Requirement(Themes 1.3)
     */
    public void setPalette(String palette) {
        Palette = palette;
        updateKey("Palette", Palette);
    }

    /**
     * Getter HTML Filename
     * @return HTML filename associated with theme. For now this should be Index.html
     * Requirement(Modelkit 1.0)
     */
    public String getHtmlFile() {
        return HtmlFile;
    }

    /**
     * Setter for HTML Filename
     * @param HtmlFile Html Filename
     * Requirement(Modelkit 1.0)
     */
    public void setHtmlFile(String HtmlFile) {
        HtmlFile = HtmlFile;
    }

    /**
     * Getter for Palette Json Array String
     * @return Json Array String with palette values.
     * Requirement(Modelkit 1.0)
     */
    public String getPalette() {
        return Palette;
    }


    /**
     * Theme model constructor
     * @param name Theme name
     * @param html Html Filename
     * @param palette Json Array Palette string
     * Requirement(Modelkit 1.0)
     */
    public Theme(String name,  String html, String palette) {
        super();
        ClassName = "Theme";
        Name = name;
        UID = Name;
        HtmlFile = html;
        Palette = palette;

        updateKey("UID", UID);
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("Palette", Palette);
        put("HtmlFile", HtmlFile);
    }


    /**
     * Theme constructor
     * @param json Json string
     * Requirement(Modelkit 1.0)
     */
    public Theme(String json) {
        super(json);

        UID = Name;
        HtmlFile = (String) get("HtmlFile");
        Palette = (String) get("Palette");
        updateKey("UID", UID);
        put("HtmlFile", HtmlFile);
        put("Palette", Palette);

    }

    /**
     * Theme Constructor from JSONObject
     * @param jObj JSON OBject
     * Requirement(Modelkit 1.0)
     */
    public Theme(JSONObject jObj) {
        super(jObj);

        try { //Assumes jObj is a ModelObject internally

            HtmlFile = (String) jObj.get("HtmlFile");
            Palette = (String) jObj.get("Palette");
            UID = Name;
            updateKey("UID", UID);
            put("HtmlFile", HtmlFile);
            put("Palette", Palette);


        } catch (JSONException e) {
            for (String key : jObj.keySet()) {
                JSONObject jModel = (JSONObject) jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

}
