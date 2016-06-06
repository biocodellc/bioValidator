package edu.berkeley.biocode.flickr;


import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.machinetags.NamespacesList;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.tags.Tag;
import edu.berkeley.biocode.photoMatcher.photoDetail;
import edu.berkeley.biocode.validator.setProperties;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: May 26, 2010
 * Time: 6:24:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class searchMachineTagsFlickr {

    private AuthorizeFlickrUpload auth = null;

    public searchMachineTagsFlickr(AuthorizeFlickrUpload auth, String namespace, String predicate, int days) {
        this.auth = auth;
        // Construct the query
        String query = namespace + ":" + predicate + "=*";
        ArrayList photos = null;
        try {
            photos = this.search(query, days);
        } catch (FlickrException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for (int i = 0; i < photos.size(); i++) {
            photoDetail pd = null;
            try {
                pd = getPhotoDetails(
                        (photoDetail) photos.get(i),
                        predicate);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //System.out.println("OWNER: " + pd.getPhoto().getOwner().getUsername());
            //System.out.println("TITLE: " + pd.getPhoto().getTitle());
            System.out.println("BEST: " + pd.getBestURL());
            System.out.println("SPECIMEN: " + pd.getSpecimen());

            Object[] exif = pd.getExif().toArray();
            for (int j = 0; j < exif.length; j++) {
                Exif e = (Exif)exif[j];
                System.out.println(e.getTag() + "=" + e.getRaw());
                //System.out.println("LABEL:" + e.getLabel());
                //System.out.println("CLEAN:" + e.getClean());
                //System.out.println(e.getRaw());

            }
            //System.out.println("EXIF: " + pd.getExif());
        }
    }


    /**
     * This part of the application is bundled into a jar file so it can be run a server to harvest
     * images that were uploaded from the client run bioValidator.
     * The one-jar application that is called from the ant task bundled here does this automatically,
     * so all you have to do to harvest any bioValidator images loaded in the last day, for instance,
     * would be:
     * <p/>
     * java -jar bioValidatorSearchMachineTags.jar bioValidator specimen -1
     *
     * @param args
     */
    public static void main(String[] args) {
        setProperties sp = new setProperties();
        Integer days;
        String query;
        String namespace;
        String predicate;
        try {
            namespace = args[0];
            predicate = args[1];
            days = Integer.valueOf(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("usage: java -jar bioValidatorSearchMachineTags.jar [namespace] [predicate] [days]");
            return;
        }

        AuthorizeFlickrUpload authorizedUpload = null;
        //authorizedUpload = new AuthorizeFlickrUpload();
       // try {
        //    authorizedUpload.init();
        //} catch (Exception e) {
         ///   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        //}

        searchMachineTagsFlickr mts = new searchMachineTagsFlickr(authorizedUpload, namespace, predicate, days);
    }

    private static photoDetail getPhotoDetails(photoDetail pd, String predicate) throws Exception {
        Object size[] = pd.getArrSizes();
        Size large = null, medium = null, original = null, small = null, square = null, thumb = null, best = null;
        int width = 0, height = 0;
        for (int j = 0; j < size.length; j++) {
            Size s = (Size) size[j];
            if (s.getLabel() == Size.LARGE) large = s;
            if (s.getLabel() == Size.MEDIUM) medium = s;
            if (s.getLabel() == Size.ORIGINAL) original = s;
            if (s.getLabel() == Size.SMALL) small = s;
            if (s.getLabel() == Size.SQUARE) square = s;
            if (s.getLabel() == Size.THUMB) thumb = s;
        }

        // Determine best size
        if (original != null) {
            width = original.getWidth();
            height = original.getHeight();
            best = original;
        } else if (original == null && large != null) {
            width = large.getWidth();
            height = large.getHeight();
            best = large;
        } else if (large == null && medium != null) {
            width = medium.getWidth();
            height = medium.getHeight();
            best = medium;
        } else {
            width = small.getWidth();
            height = small.getHeight();
            best = small;
        }
        pd.setBestURL(best.getSource());

        // Get specimen from machine tag
        /*
        Collection tags = pd.getPhoto().getTags();
        Iterator ti = tags.iterator();
        String specimen = "";
        while (ti.hasNext()) {
            String tag = ((Tag) ti.next()).getRaw();
            String thisPredicate = tag.split(":")[1].split("=")[0];
            String thisValue = tag.split(":")[1].split("=")[1];
            if (thisPredicate.equals(predicate)) {
                specimen = thisValue;
                pd.setSpecimen(specimen);
            }
        }
        */

        return pd;

    }

    public ArrayList search(String query, int days) throws
            FlickrException,
            IOException,
            ParserConfigurationException,
            SAXException {

        PhotosInterface photoInt = null;
        photoInt = new PhotosInterface(auth.apiKey, auth.sharedSecret, new REST());
        String strRet = "";

        SearchParameters searchp = new SearchParameters();

        // Set User
        //searchp.setUserId(auth.auth.getUser().getId());

        // Set DATE
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime().toString());
        c.setTime(c.getTime());
        c.add(Calendar.DATE, days);  // number of days to add
        searchp.setMinUploadDate(c.getTime());
        System.out.println("SETTING MIN UPLOAD DATE = " + c.getTime().toString());

        // Set Tags
        String[] tags = new String[1];
        tags[0] = query;
        searchp.setMachineTags(tags);
        searchp.setMachineTagMode("all");

        System.out.println("SETTING QUERY = " + query);


        ArrayList photos = new ArrayList();
        ArrayList photodetails = new ArrayList();

        // Loop Through Results and assign to return array
        PhotoList pl = photoInt.search(searchp, 1000000, 0);

        for (int i = 0; i < pl.size(); i++) {
            // Fetch the basic photo
            Photo p = (Photo) pl.get(i);
            // Once we fetch the photo, get ALL the info. Yes, this appears to be ineffecient but there
            // does not appear to be a better way
            p = photoInt.getInfo(p.getId(), p.getSecret());

            Object arrSizes[] = photoInt.getSizes(p.getId()).toArray();

            photoDetail photoDetail = new photoDetail();
           // photoDetail.setPhoto(p);
            photoDetail.setExif(photoInt.getExif(p.getId(), p.getSecret()));
            //photoDetail.setLinkToUrl(p.getUrl());
            photoDetail.setArrSizes(arrSizes);
            photodetails.add(photoDetail);
        }
        return photodetails;
    }

    public NamespacesList getValues(String namespace, String predicate, int perPage, int page)
            throws FlickrException, IOException, SAXException {

        ArrayList parameters = new ArrayList();
        NamespacesList valuesList = new NamespacesList();
        /*
        //parameters.add(new Parameter("method", METHOD_GET_VALUES));
        parameters.add(new Parameter("api_key", auth.apiKey));

        if (namespace != null) {
            parameters.add(new Parameter("namespace", namespace));
        }
        if (predicate != null) {
            parameters.add(new Parameter("predicate", predicate));
        }
        if (perPage > 0) {
            parameters.add(new Parameter("per_page", 1));
        }
        if (page > 0) {
            parameters.add(new Parameter("page", 1));
        }

        Response response = transportAPI.get(transportAPI.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        Element nsElement = response.getPayload();
        NodeList nsNodes = nsElement.getElementsByTagName("value");
        valuesList.setPage(nsElement.getAttribute("page"));
        valuesList.setPages(nsElement.getAttribute("pages"));
        valuesList.setPerPage(nsElement.getAttribute("perPage"));
        valuesList.setTotal("" + nsNodes.getLength());
        for (int i = 0; i < nsNodes.getLength(); i++) {
            Element element = (Element) nsNodes.item(i);
            valuesList.add(parseValue(element));
        }
        */
        return valuesList;
    }
}






