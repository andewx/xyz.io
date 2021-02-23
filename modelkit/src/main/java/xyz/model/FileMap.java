package xyz.model;


import org.json.JSONException;
import org.json.JSONObject;

public class FileMap extends ModelObject {
   protected String FilePath;

    public FileMap(String name, String pathName){
        super();
        FilePath = pathName;
        Name = name;
        ClassName = "FileMap";
        UID = Name;
        updateKey("UID", UID);
        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("FilePath", FilePath);
    }

    public FileMap(String json){
        super(json);
        FilePath = (String)get("FilePath");
        put("FilePath",FilePath);
    }

    public FileMap(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            FilePath = (String)jObj.get("FilePath");
            put("FilePath",FilePath);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public String getKey(){
        return Name;
    }


    public String GetFilepath(){
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
        updateKey("FilePath", FilePath);
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
                sb.append(" value='" + get(key) + " id='" + key + "' name='" + key + "'></input></div></li>");
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
