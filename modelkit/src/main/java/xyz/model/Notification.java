package xyz.model;

import java.time.Instant;

public class Notification extends ModelObject{
    //Edge from A to B
    protected String ClassA;
    protected String ClassB;
    protected String idA;
    protected String idB;
    protected boolean Sent;
    protected boolean Read;
    protected String DateCreated;
    protected String DateRead;
    protected String Message;

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
        put("ClassA", ClassA);
        put("ClassB", ClassB);
    }

    public String getClassA() {
        return ClassA;
    }

    public void setClassA(String classA) {
        ClassA = classA;
        updateKey("ClassA", ClassA);
    }

    public String getClassB() {
        return ClassB;
    }

    public void setClassB(String classB) {
        ClassB = classB;
        updateKey("ClassB", ClassB);
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
        updateKey("idA", idA);
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
        updateKey("idB", idB);
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
