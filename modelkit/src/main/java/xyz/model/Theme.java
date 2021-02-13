package xyz.model;

public class Theme extends ModelObject{
    public String InnerCSS;

    public Theme(String name){
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
