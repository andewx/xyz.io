package xyz.model;


public class Item extends ModelObject {


    protected String Description;
    protected String Unit;
    protected int Amount;

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


    public String GetUnits(){
        return Unit;
    }

    public String GetDescription(){
        return Description;
    }

    public int GetAmount(){
        return Amount;
    }
    public void setDescription(String description) {
        Description = description;
        updateKey("Description", Description);
    }

    public void setUnit(String unit) {
        Unit = unit;
        updateKey("Unit", Unit);
    }

    public void setAmount(int amount) {
        Amount = amount;
        updateKey("Amount", Amount);
    }


}
