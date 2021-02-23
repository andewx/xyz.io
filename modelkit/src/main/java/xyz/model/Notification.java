package xyz.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

public class Notification extends Edge{
    //Edge from A to B
    protected boolean Sent;
    protected boolean Read;
    protected String DateCreated;
    protected String DateRead;
    protected String Message;

    //Our Database Engine Is Also Our Model Manager - It will sync the graphs and Do Notification Work

    public Notification(String uidA, String uidB, String message, String NotifierClass, String SubscriberClass, String MessageType){
        super(NotifierClass, SubscriberClass, uidA, uidB, "Notifies");
        ClassName = "Notification";
        Name = MessageType;
        Message = message;
        Sent = false;
        Read = false;
        DateCreated = Instant.now().toString();
        DateRead = "";

        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("Message", Message);
        put("Sent", Sent);
        put("Read", Read);
        put("DateCreated", DateCreated);
        put("DateRead", DateRead);
    }


    public Notification(String json){
        super(json);
        Message = (String)get("Message");
        Sent = (boolean)get("Sent");
        Read = (boolean)get("Read");
        DateCreated = (String)get("DateCreated");
        DateRead = (String)get("DateRead");
        put("Message", Message);
        put("Sent", Sent);
        put("Read", Read);
        put("DateCreated", DateCreated);
        put("DateRead", DateRead);
    }

    public Notification(JSONObject jObj){
        super(jObj);

        try { //Assumes jObj is a ModelObject internally
            Message = (String)jObj.get("Message");
            Sent = (boolean)jObj.get("Sent");
            Read = (boolean)jObj.get("Read");
            DateCreated = (String)jObj.get("DateCreated");
            DateRead = (String)jObj.get("DateRead");
            put("Message", Message);
            put("Sent", Sent);
            put("Read", Read);
            put("DateCreated", DateCreated);
            put("DateRead", DateRead);

        }catch(JSONException e){
            for (String key : jObj.keySet()){
                JSONObject jModel = (JSONObject)jObj.get(key);
                ModelObject mModel = new ModelObject(jModel);
                addModel(mModel);
            }
        }

    }

    public boolean isSent() {
        return Sent;
    }

    public void setSent(boolean sent) {
        Sent = sent;
        updateKey("Sent", Sent);
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
        updateKey("Read", Read);
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
        updateKey("DateCreated", DateCreated);
    }

    public String getDateRead() {
        return DateRead;
    }

    public void setDateRead(String dateRead) {
        DateRead = dateRead;
        updateKey("DateRead", DateRead);
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
        updateKey("Message", Message);
    }

}
