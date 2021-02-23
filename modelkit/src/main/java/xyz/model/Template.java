package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Template extends ModelObject {
    protected String HTML;

    public Template(String name, String html){
        ClassName = "Template";
        Name = name;
        UID = name; //We should check for uniqueness
        HTML = html;
        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        updateKey("UID", UID);
        put("HTML", HTML);
    }

    public Template(String json){
        super(json);
        HTML = (String)get("HTML");
        put("HTML", HTML);
    }

    public Template(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            HTML= (String) jObj.get("HTML");
            put("HTML", HTML);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public void setHTML(String html){
        HTML = html;
        updateKey("HTML", HTML);
    }

    public String getHTML(){
        return HTML;
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
            if (!key.equals("UID") && !key.equals("ClassName")){
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
        String type = "<input type='text' maxLength='45'" +" value='" + get(key) + " id='" + key + "' name='" + key + "' required></input>";
        if(key.equals("HTML")){
            type = "<textarea rows='50' cols='3'" +" value='" + get(key) + " id='" + key + "' name='" + key + "'></textarea>";
        }
        return type;
    }
}
