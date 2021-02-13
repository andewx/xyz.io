package xyz.model;

public class ThemeCSS extends ModelObject{
    public String InnerCSS;

    public ThemeCSS(String name){
        super();
        ClassName = "ThemeCSS";
        Name = name;
        InnerCSS = "";
        put("Name", Name);
        put("ClassName", ClassName);
        put("InnerCSS", InnerCSS);

    }

    public void update(){
        super.update();
        put("InnerCSS", InnerCSS);
    }
}
