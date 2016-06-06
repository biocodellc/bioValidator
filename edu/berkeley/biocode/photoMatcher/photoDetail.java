package edu.berkeley.biocode.photoMatcher;


import com.flickr4java.flickr.photos.Photo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: May 27, 2010
 * Time: 5:03:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class photoDetail {

    private Photo photo;
    private Object arrSizes[];
    private String specimen;
    private String taxonomy_binomial;
    private String geo_lat;
    private String geo_lon;
    private String bestURL;
    private Collection exif;
    private String absoluteFileName;

    public String getAbsoluteFileName() {
        return absoluteFileName;
    }

    public void setAbsoluteFileName(String absoluteFileName) {
        this.absoluteFileName = absoluteFileName;
    }

    public Collection getExif() {
        return exif;
    }

    public void setExif(Collection exif) {
        this.exif = exif;
    }

    public String getBestURL() {
        return bestURL;
    }

    public void setBestURL(String bestURL) {
        this.bestURL = bestURL;
    }

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public String getTaxonomy_binomial() {
        return taxonomy_binomial;
    }

    public void setTaxonomy_binomial(String taxonomy_binomial) {
        this.taxonomy_binomial = "taxonomy:binomial="+"\""+taxonomy_binomial+"\"";
    }

    public String getGeo_lat() {
        return geo_lat;
    }

    public void setGeo_lat(String geo_lat) {
        this.geo_lat = "geo:lat="+"\""+geo_lat+"\"";
    }

    public String getGeo_lon() {
        return geo_lon;
    }

    public void setGeo_lon(String geo_lon) {
        this.geo_lon = "geo:lon="+"\""+geo_lon+"\"";
    }

    public Object[] getArrSizes() {
        return arrSizes;
    }

    public void setArrSizes(Object[] arrSizes) {
        this.arrSizes = arrSizes;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

}
