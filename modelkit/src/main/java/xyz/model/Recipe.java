package xyz.model;

public class Recipe extends ModelObject{
    public String Category;
    public int PrepTime;
    public int CookTime;
    public int Serves;
    public String Caption;
    public String PostEntry;
    public String ImageURL;

    public Recipe() {
        super();
        ClassName = "Recipe";
        Name = "New Recipe";
        ImageURL = "";
        PrepTime = 0;
        CookTime = 0;
        Serves = 1;
        Caption = "Caption Text";
        PostEntry = "Recipe Instructions";

        put("ClassName", ClassName);
        put("Name", Name);
        put("Category", Category);
        put("Serves", Serves);
        put("Caption", Caption);
        put("PostEntry", PostEntry);
        put("ImageURL", ImageURL);

    }

    public Recipe(String name){
        super();
        ClassName = "Recipe";
        Name = name;
        ImageURL = "";

        PrepTime = 0;
        CookTime = 0;
        Serves = 1;
        Caption = "Caption Text";
        PostEntry = "Recipe Instructions";

        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("Category", Category);
        put("Serves", Serves);
        put("Caption", Caption);
        put("PostEntry", PostEntry);
        put("ImageURL", ImageURL);
    }

    public void update(){
        super.update();
        updateKey("Category", Category);
        updateKey("Serves", Serves);
        updateKey("Caption", Caption);
        updateKey("PostEntry", PostEntry);
        updateKey("ImageURL", ImageURL);
    }
}
