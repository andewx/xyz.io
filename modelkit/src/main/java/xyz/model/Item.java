package xyz.model;


public class Item extends ModelObject {


    public String Description;
    public String Unit;
    public int Amount;

    public Item() {
        super();
        ClassName = "Item";
        Name = "UniqueItem";

        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);

    }

    public Item(String name){
        super();
        ClassName = "Item";
        Name = name;

        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);
    }

    public void update(){
        super.update();
        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);
    }


}
