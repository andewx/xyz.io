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

        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("Message", Message);
        put("Sent", Sent);
        put("Read", Read);
        put("DateCreated", DateCreated);
        put("DateRead", DateRead);
        put("idA", idA);
        put("idB", idB);
    }

    public String getClassA() {
        return ClassA;
    }

    public void setClassA(String classA) {
        ClassA = classA;
    }

    public String getClassB() {
        return ClassB;
    }

    public void setClassB(String classB) {
        ClassB = classB;
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    public boolean isSent() {
        return Sent;
    }

    public void setSent(boolean sent) {
        Sent = sent;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public String getDateRead() {
        return DateRead;
    }

    public void setDateRead(String dateRead) {
        DateRead = dateRead;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public void update(){
        super.update();
        updateKey("DateRead", DateRead);
        updateKey("Message", Message);
    }
}
