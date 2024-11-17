package org.supsi.model.about;

/**
 * A singleton class for managing application metadata such as the developer's information,
 * release date, and version.
 */
public class AboutModel implements IAboutModel{

    private static AboutModel myself;
    private String developer;
    private String version;
    private String date;

    protected AboutModel() {}

    /**
     * Retrieves the singleton instance of {@code AboutModel}.
     *
     * @return the singleton instance of {@code AboutModel}
     */
    public static AboutModel getInstance(){
        if(myself==null){
            myself=new AboutModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeveloper() {
        return developer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDate() {
        return date;
    }
}
