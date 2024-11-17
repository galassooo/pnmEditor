package org.supsi.model.about;

public class AboutModel implements IAboutModel{

    private static AboutModel myself;
    private String developer;
    private String version;
    private String date;

    protected AboutModel() {}

    public static AboutModel getInstance(){
        if(myself==null){
            myself=new AboutModel();
        }
        return myself;
    }

    @Override
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getDeveloper() {
        return developer;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDate() {
        return date;
    }
}
