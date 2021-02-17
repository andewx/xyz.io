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
    }

    public void update(){
        super.update();
        updateKey("Category", Category);
        updateKey("Serves", Serves);
        updateKey("Caption", Caption);
        updateKey("PostEntry", PostEntry);
        updateKey("ImageURL", ImageURL);
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
    }

    public void setPrepTime(int prepTime) {
        PrepTime = prepTime;
    }

    public void setCookTime(int cookTime) {
        CookTime = cookTime;
    }

    public void setServes(int serves) {
        Serves = serves;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public void setPostEntry(String postEntry) {
        PostEntry = postEntry;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
