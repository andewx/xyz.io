package xyz.model;


public class Item extends ModelBase {


    public String Description;
    public String Title;
    public String Unit;
    public int Amount;


    public Item() {
        super();
        Name = "Item";
        //Add Item Properties - All New Models Do this
        this.mPropertyMap.put("Title", Title);
        this.mTypeMap.put("Title", "String");
        this.mPropertyMap.put("Description", Description);
        this.mTypeMap.put("Description", "String");
        this.mPropertyMap.put("Unit", Unit);
        this.mTypeMap.put("Unit", "String");
        this.mPropertyMap.put("Amount", String.format("%d", Amount));
        this.mTypeMap.put("Amount", "int");
    }



    @Override
    public void syncModel(boolean syncInternal) {
       super.syncModel(syncInternal);

        mPropertyMap.replace("Title", Title);
        mPropertyMap.replace("Description", Description);
        mPropertyMap.replace("Unit", Unit);
        mPropertyMap.replace("Amount", String.format("%d", Amount));
    }
}
