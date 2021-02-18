package xyz.model;

public class Theme extends ModelObject{
    protected String CSS;

    public Theme(String name){
        super();
        ClassName = "ThemeCSS";
        Name = name;
        CSS = "";
        updateKey("Name", Name);
        updateKey("ClassName", ClassName);
        put("CSS", CSS);

    }

    public String getCSS(){
        return CSS;
    }

    public void setCSS(String css){
        CSS = css;
        updateKey("CSS", CSS);
    }

}
