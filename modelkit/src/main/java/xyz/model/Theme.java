package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Theme extends ModelObject {
    protected String HtmlFile;
    protected String Palette;

    public void setPalette(String palette) {
        Palette = palette;
        updateKey("Palette", Palette);
    }



    public String getHtmlFile() {
        return HtmlFile;
    }

    public void setHtmlFile(String HtmlFile) {
        HtmlFile = HtmlFile;
    }

    public String getPalette() {
        return Palette;
    }


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


    public Theme(String json) {
        super(json);

        UID = Name;
        HtmlFile = (String) get("HtmlFile");
        Palette = (String) get("Palette");
        updateKey("UID", UID);
        put("HtmlFile", HtmlFile);
        put("Palette", Palette);

    }

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
