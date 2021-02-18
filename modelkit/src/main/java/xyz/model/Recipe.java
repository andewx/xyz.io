package xyz.model;

public class Recipe extends ModelObject{
    protected String Category;
    protected int PrepTime;
    protected int CookTime;
    protected int Serves;
    protected String Caption;
    protected String PostEntry;
    protected String ImageURL;

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
        put("PrepTime", PrepTime);
        put("CookTime", CookTime);
    }

    public Recipe(String name, String img,String category, String caption, String post, int prep, int cook, int serves){
        super();
        ClassName = "Recipe";
        Name = name;
        ImageURL = img;
        PrepTime = prep;
        CookTime = cook;
        Serves = serves;
        Caption = caption;
        PostEntry = post;

        updateKey("ClassName", ClassName);
        updateKey("Name", Name);
        put("Category", Category);
        put("Serves", Serves);
        put("Caption", Caption);
        put("PostEntry", PostEntry);
        put("ImageURL", ImageURL);
        put("PrepTime", PrepTime);
        put("CookTime", CookTime);
    }


    public String getCategory() {
        return Category;
    }

    public int getPrepTime() {
        return PrepTime;
    }

    public int getCookTime() {
        return CookTime;
    }

    public int getServes() {
        return Serves;
    }

    public String getCaption() {
        return Caption;
    }

    public String getPostEntry() {
        return PostEntry;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setCategory(String category) {
        Category = category;
        updateKey("Category", Category);
    }

    public void setPrepTime(int prepTime) {
        PrepTime = prepTime;
        updateKey("PrepTime", PrepTime);
    }

    public void setCookTime(int cookTime) {
        CookTime = cookTime;
        updateKey("CookTime", CookTime);
    }

    public void setServes(int serves) {
        Serves = serves;
        updateKey("Serves", Serves);
    }

    public void setCaption(String caption) {
        Caption = caption;
        updateKey("Caption", Caption);
    }

    public void setPostEntry(String postEntry) {
        PostEntry = postEntry;
        updateKey("PostEntry", PostEntry);
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
        updateKey("ImageURL", ImageURL);
    }
}
