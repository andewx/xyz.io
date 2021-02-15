package xyz.model;

public class Theme extends ModelObject{
    public String InnerCSS;

    public Theme(String name){
        super();
        ClassName = "ThemeCSS";
        Name = name;
        InnerCSS = "";
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("InnerCSS", InnerCSS);

    }

    public void update(){
        super.update();
        updateKey("InnerCSS", InnerCSS);
    }
}
