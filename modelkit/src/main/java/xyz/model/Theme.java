package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Theme extends ModelObject{
    protected String CSS;

    public Theme(String name, String css){
        super();
        ClassName = "Theme";
        Name = name;
        CSS = css;
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("CSS", CSS);
    }


    public Theme(String json){
        super(json);
        CSS = (String)get("CSS");
        put("CSS", CSS);
    }

    public Theme(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            CSS = (String)jObj.get("CSS");
            put("CSS", CSS);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getCSS(){
        return CSS;
    }

    public void setCSS(String css){
        CSS = css;
        updateKey("CSS", CSS);
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
        if(key.equals("CSS")){
            type = "<textarea rows='50' cols'10'" +" value='" + get(key) + " id='" + key + "' name='" + key + "' required></input>";
        }
        return type;
    }

}
