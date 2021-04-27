package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Menu extends ModelObject{
    protected String URL;

    public Menu(String name, String url){
        super();
        ClassName = "Menu";
        Name = name;
        URL = url;
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("URL", URL);
    }


    public Menu(String json){
        super(json);
        URL = (String)get("URL");
        put("URL", URL);
    }

    public Menu(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            URL = (String)jObj.get("URL");
            put("URL", URL);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getURL(){
        return URL;
    }

    public void setURL(String URL){
        URL = URL;
        updateKey("URL", URL);
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
        if(key.equals("URL")){
            type = "<textarea rows='50' cols'10'" +" value='" + get(key) + " id='" + key + "' name='" + key + "' required></input>";
        }
        return type;
    }

}
