package edu.berkeley.biocode.taxonomy;

import edu.berkeley.biocode.validator.setProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: biocode
 * Date: Aug 5, 2010
 * Time: 5:39:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class lookupTaxonomies {
    private static ArrayList remotetaxonomies;
    private static ArrayList remotetaxonomydates;
    private static ArrayList remotetaxonomyURL;

    private static ArrayList localtaxonomies = new ArrayList();
    private static ArrayList localtaxonomydates = new ArrayList();
    private static ArrayList localtaxonomyfilenames = new ArrayList();

    public static void main(String args[]) {
        lookupTaxonomies lt = new lookupTaxonomies();
        for (int i = 0; i < remotetaxonomies.size(); i++) {                       
            System.out.println(remotetaxonomies.get(i));
        }
    }

    public lookupTaxonomies() {
        remotetaxonomies = new ArrayList();
        remotetaxonomydates = new ArrayList();
        remotetaxonomyURL = new ArrayList();

        localtaxonomies = new ArrayList();
        localtaxonomydates = new ArrayList();
        localtaxonomyfilenames = new ArrayList();
        setProperties sp = new setProperties();

        // These will be in zipped form
        try {

            URL url = new URL(System.getProperty("app.taxonomyIndex"));
            InputStream is = url.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("bioTaxonomy");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                NodeList taxonomies = nodeLst.item(s).getChildNodes();
                for (int t = 0; s < taxonomies.getLength(); s++) {
                    Node nNode = taxonomies.item(s);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        remotetaxonomyURL.add(System.getProperty("app.taxonomyURL") + getTagValue("filename", eElement));
                        remotetaxonomies.add(getTagValue("name", eElement));
                        remotetaxonomydates.add(getTagValue("date", eElement));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // notify the user some-how if this taxonomy exists locally or not, and if it is out of date or not

    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    public ArrayList getRemotetaxonomyfilenames() {
        return remotetaxonomyURL;
    }

    public ArrayList getLocaltaxonomyfilenames() {
        return localtaxonomyfilenames;
    }

    public ArrayList getRemoteTaxonomies() {
        return remotetaxonomies;
    }

    public ArrayList getRemoteTaxonomyDates() {
        return remotetaxonomydates;
    }

    public ArrayList getLocalTaxonomies() {
        return localtaxonomies;
    }

    public ArrayList getLocalTaxonomyDates() {
        return localtaxonomydates;
    }
}
