package xyz.model;

import java.time.Instant;

public class Notification extends ModelObject{
    //Edge from A to B
    String ClassA;
    String ClassB;
    String idA;
    String idB;
    boolean Sent;
    boolean Read;
    String DateCreated;
    String DateRead;
    String Message;

    //Our Database Engine Is Also Our Model Manager - It will sync the graphs and Do Notification Work

    public Notification(String uidA, String uidB, String message, String NotifierClass, String SubscriberClass, String MessageType){
        super();
        ClassName = "Notification";
        Name = MessageType;
        Message = message;
        Sent = false;
        Read = false;
        DateCreated = Instant.now().toString();
        DateRead = "N.A";
        idA = uidA;
        idB = uidB;
        ClassA = NotifierClass;
        ClassB = SubscriberClass;

        put("ClassName", ClassName);
        put("Name", Name);
        put("Message", Message);
        put("Sent", Sent);
        put("Read", Read);
        put("DateCreated", DateCreated);
        put("DateRead", DateRead);
        put("idA", idA);
        put("idB", idB);
    }

    public void update(){
        super.update();
        put("DateRead", DateRead);
        put("Message", Message);
    }
}
