package ch.supsi.model.about;

public class AboutModel implements IAboutModel{

    private static AboutModel myself;

    private String developer;
    private String version;
    private String date;

    public static AboutModel getInstance(){
        if(myself==null){
            myself=new AboutModel();
        }
        return myself;
    }

    private AboutModel() {}

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getVersion() {
        return version;
    }

    public String getDate() {
        return date;
    }
}
