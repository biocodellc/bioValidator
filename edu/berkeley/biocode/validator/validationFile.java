package edu.berkeley.biocode.validator;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Apr 30, 2010
 * Time: 4:56:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class validationFile {


    private String url = "";
    private String name = "";
    private String description = "";
    private String XSDUrl = "";
    private String XSDName = "";

    public String getXSDName() {
        return XSDName;
    }

    public void setXSDName(String XSDName) {
        this.XSDName = XSDName;
    }

    public String getXSDUrl() {
        return XSDUrl;
    }

    public void setXSDUrl(String XSDUrl) {
        this.XSDUrl = XSDUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String Url) {
        this.url = Url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

