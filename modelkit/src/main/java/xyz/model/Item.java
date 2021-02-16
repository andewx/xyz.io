package xyz.model;


public class Item extends ModelObject {


    public String Description;
    public String Unit;
    public int Amount;

    public Item() {
        super();
        ClassName = "Item";
        Name = "UniqueItem";

        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);

    }

    public Item(String name){
        super();
        ClassName = "Item";
        Name = name;
        updateKey("Name", Name);
        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);
    }

    public Item(String name, String descrip, String unit, int amount){
        super();
        ClassName = "Item";
        Name = name;
        Description = descrip;
        Unit = unit;
        Amount = amount;

        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("Description", Description);
        put("Unit", Unit);
        put("Amount", Amount);
    }

    public void update(){
        super.update();
        updateKey("Description", Description);
        updateKey("Unit", Unit);
        updateKey("Amount", Amount);
    }


}
