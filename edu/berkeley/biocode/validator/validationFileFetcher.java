package edu.berkeley.biocode.validator;

import edu.berkeley.biocode.bioValidator.mainPage;
import org.apache.commons.digester.Digester;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;


public class validationFileFetcher extends JButton {
    private static String localAbsoluteFilename;
    private static String localXSDAbsoluteFilename;
    private static String body;
    private static String xsdbody;

    private static URL XMLconfig;
    private static URL XSDconfig;
    private static URLConnection conn;
    private static URLConnection xsdconn;

    private Map<String, List<String>> map = new HashMap<String, List<String>>();
    private static int hours = 24;
    public static boolean internet;
    public static boolean schemaValidated;

    MyCacheResponse mcr = null;
    MyCacheResponse xsdmcr = null;

    private validationFiles vf;
    public validationFile vfChoice;

    private String messageString = "";


    /**
     * validationFileFetcher is a class that fetches validation file from the server and
     * caches on the client box.  There is a reasonable amount of checking to see if the
     * cache file grows stale (more than 1 day old) and attempts to replace the cache file
     * if it is older than this.  If there is no internet connection, it uses the cached copy
     * of the validation file, regardless of age of the file.
     */
    public validationFileFetcher() {

        // Initialize validation file elements (fetch the available schemas online or in cache)
        this.initValidationElementsSchema(24);

        // Create validationFiles Object
        vf = new validationFiles();
        setText("Load Validation");

        // Loading validationFile Digester
        Digester vD = new Digester();
        validationFile validationFile = new validationFile();
        vD.push(this.vf);

        vD.addObjectCreate("validationSchemas/validationSchema", validationFile.class);
        vD.addSetProperties("validationSchemas/validationSchema");
        vD.addSetNext("validationSchemas/validationSchema", "addValidationFile");
        //d.addCallMethod("Validate/Metadata/metadata/field", "addField", 0);


        try {
            vD.parse(new StringReader(this.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param clickButton
     * @return
     */
    public validationFile choose(boolean clickButton) {
        Preferences prefs = Preferences.userRoot().node(System.getProperty("app.bioValidatorUserPreferences"));

        // Run if clicked a button or nothing set in validationSchema
        if (clickButton || prefs.get("validationSchema", null) == null) {
            vfChoice = vf.showDialog();
        } else {
            vfChoice = vf.getValidationFile(prefs.get("validationSchema", null));
        }

        return vfChoice;

    }

    /**
     * Initialize validationFileFetcher for the Rules Schema
     *
     * @param hours
     */
    public void initRulesSchema(int hours) {
        // Initialize class variables
        this.messageString = "";
        internet = false;
        schemaValidated = false;
        this.localAbsoluteFilename = System.getProperty("app.bvFilesDirectory") +
                vfChoice.getName() +
                System.getProperty("app.cacheExtension");
        this.localXSDAbsoluteFilename = System.getProperty("app.bvFilesDirectory") +
                vfChoice.getXSDName() +
                System.getProperty("app.cacheExtension");

        init(hours,
                vfChoice.getUrl(),
                vfChoice.getXSDUrl(),
                true
        );

    }

    /**
     * See if this is a Biocode Schema, or relative of a biocode schema
     *
     * @return
     */
    public boolean isBiocode() {
        String haystack = XMLconfig.toString();
        int index = haystack.indexOf("biocodeValidator");
        int index2 = haystack.indexOf("slabValidator");
        if (index != -1 || index2 != 1) {
            System.out.println("found biocodeValidator in " + haystack);
            return true;
        } else {
            System.out.println("did not find biocodeValidator in " + haystack);
            return false;
        }
    }

    /**
     * Initialize validationFileFetcher for the Rules Schema
     *
     * @param hours
     */
    public void initValidationElementsSchema(int hours) {
        // Initialize class variables
        this.messageString = "";
        internet = false;
        schemaValidated = false;

        this.localAbsoluteFilename = System.getProperty("app.bvFilesDirectory") +
                System.getProperty("app.validationSchemasFileName") +
                System.getProperty("app.cacheExtension");
        this.localXSDAbsoluteFilename = System.getProperty("app.bvFilesDirectory") +
                System.getProperty("app.validationXSDSchemasFileName") +
                System.getProperty("app.cacheExtension");
        init(hours,
                System.getProperty("app.validationSchemasBaseURL") + System.getProperty("app.validationSchemasFileName"),
                System.getProperty("app.validationXSDSchemasBaseURL") + System.getProperty("app.validationXSDSchemasFileName"),
                false
        );
    }

    /**
     * generic initiliazer (can be used for multiple XML Documents, passing in XML and XSD files)
     *
     * @param hours
     */
    private boolean init(int hours, String XMLUrl, String XSDUrl, boolean runSchemaValidation) {
        this.hours = hours;
        // Attempt to read from cache to initialize variables
        this.readBodyFromCache();

        try {
            this.XMLconfig = new URL(XMLUrl);
            this.XSDconfig = new URL(XSDUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Create Connections
        try {
            conn = XMLconfig.openConnection();
            conn.setUseCaches(false);
            xsdconn = XSDconfig.openConnection();
            xsdconn.setUseCaches(false);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // No Internet Connection
        if (!this.internet()) {
            System.out.println("No internet connection.");
            messageString += "No internet. ";
            if (this.cacheExists()) {
                System.out.println("... cache exists, we can use that");
                messageString += "Cache Available. ";
                if (this.readBodyFromCache()) {
                    // Just set schema validation to true if there is not internet connection
                    // as we can't validate it anyway
                    schemaValidated = true;
                }
            } else {
                System.out.println("... no cached copy.  Must connect and get file!");
                messageString += "No Cache. ";
            }

        } else {
            // Check if cached copy is expired
            if (this.expired()) {
                // Write to Cache
                if (this.writeToCache()) {
                    System.out.println("Succesfully wrote validation file and XSD to cache");
                } else {
                    if (this.cacheExists()) {
                        System.out.println("Unable to update cache, but able to use existing cache files. Possibly closed internet cxn.");
                    } else {
                        System.out.println("Unable to update cache and not able to use any existing cache files");
                    }
                }
            }

            if (this.readBodyFromCache()) {
                if (runSchemaValidation) {
                    schemaValidated = schemaValidation();
                } else {
                    schemaValidated = true;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) throws Exception {
        //URL url = new URL("http://biocode.berkeley.edu/biocodeValidator-0.8.xml");
        URL url = new URL("http://www.w3schools.com/xml/note_encode_none_u.xml");
        URLConnection thisconn = url.openConnection();

        // Another method
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                thisconn.getInputStream()));
        String inputLine2;
        while ((inputLine2 = in2.readLine()) != null)
            System.out.println(inputLine2);
        in2.close();

        System.out.println("-----");

        // The method i'm using
        MyCacheRequest req = new MyCacheRequest("/tmp/testing.xml", thisconn.getHeaderFields());
        FileOutputStream fos = (FileOutputStream) req.getBody();
        BufferedReader in = new BufferedReader(new InputStreamReader(thisconn.getInputStream(), "UTF-8"));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            //fos.write(inputLine.getBytes("UTF-8"));
            System.out.println(inputLine);
        }

        /*String content = "";
        String filename = System.getProperty("app.validationAbsoluteFileName") + System.getProperty("app.validationCacheExtension");
        Integer hours = Integer.getInteger(System.getProperty("validationExpireHours"));
        validationFileFetcher ucr = null;
        */
        //ucr = new validationFileFetcher(hours, localAbsoluteFilename, new validationFileFetcher());
        //System.out.println("FINAL OUTPUT: " + ucr.getBody());
    }

    private static boolean internet() {
        // Test to see if there is an internet connection
        if (conn.getLastModified() == 0) {
            internet = false;
            return false;
        } else {
            internet = true;
            return true;
        }
    }

    /**
     * Write validationFileFetcher and XSD Schema to Cache
     *
     * @return
     */
    private static boolean writeToCache() {
        // Write to Cache
        try {

            MyResponseCache mc = new MyResponseCache(localAbsoluteFilename);
            mc.put(XMLconfig.toURI(), conn);

            MyResponseCache xsdmc = new MyResponseCache(localXSDAbsoluteFilename);
            mc.put(XSDconfig.toURI(), xsdconn);

            MyCacheRequest req = new MyCacheRequest(localAbsoluteFilename, conn.getHeaderFields());
            MyCacheRequest xsdreq = new MyCacheRequest(localXSDAbsoluteFilename, xsdconn.getHeaderFields());

            // Write body to output stream
            //System.out.println(conn.getURL().getHost() + "/" + conn.getURL().getPath());

            FileOutputStream fos = (FileOutputStream) req.getBody();
            // Following works when content encoding is ISO-8859-1, but not as UTF-8
            // But, the XML file we're reading is UTF-8??
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;

            FileOutputStream xsdfos = (FileOutputStream) xsdreq.getBody();
            BufferedReader xsdin = new BufferedReader(new InputStreamReader(xsdconn.getInputStream(), "UTF-8"));
            String xsdinputLine;

            while ((inputLine = in.readLine()) != null) {
                fos.write(inputLine.getBytes("UTF-8"));
                //System.out.println(inputLine);
            }
            while ((xsdinputLine = xsdin.readLine()) != null) xsdfos.write(xsdinputLine.getBytes("UTF-8"));

            in.close();
            fos.close();

            xsdin.close();
            xsdfos.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Both validationFileFetcher and XSD file need to exist
     *
     * @return
     */
    private boolean cacheExists() {
        try {
            File f = new File(localAbsoluteFilename);
            File xsdf = new File(localXSDAbsoluteFilename);

            if (f.exists() && xsdf.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the file is expired or not ---
     * use this to see if we should fetch new file from server.
     * Setting this value to 1 day.
     */
    private boolean expired() {
        try {
            if (!cacheExists()) {
                return true;
            }

            String strDate = mcr.getHeaders().get("Date").toString();
            DateFormat formatter = new SimpleDateFormat("[EEE, dd MMM yyyy HH:mm:ss z]", Locale.ENGLISH);
            Date dateCached = (Date) formatter.parse(strDate);
            Date dateNow = Calendar.getInstance().getTime();

            long diffHours = (dateNow.getTime() - dateCached.getTime()) / (60 * 60 * 1000);
            System.out.println(localAbsoluteFilename + " is " + diffHours + " hours old");
            if (diffHours >= this.hours) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean readBodyFromCache() {
        String retVal = "";
        String xsdretVal = "";

        try {
            mcr = new MyCacheResponse(localAbsoluteFilename);
            xsdmcr = new MyCacheResponse(localXSDAbsoluteFilename);

            FileInputStream fis = mcr.fis;
            FileInputStream xsdfis = xsdmcr.fis;

            BufferedReader in2 = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            BufferedReader xsdin2 = new BufferedReader(new InputStreamReader(xsdfis, "UTF-8"));

            String inputLine2;
            String xsdinputLine2;

            while ((inputLine2 = in2.readLine()) != null) {
                retVal += (inputLine2);
            }
            while ((xsdinputLine2 = xsdin2.readLine()) != null) xsdretVal += (xsdinputLine2);

            in2.close();
            xsdin2.close();

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Unable to read from Cache");
            return false;
        }
        body = retVal;
        xsdbody = xsdretVal;

        if (retVal.equals("") || xsdretVal.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public String getBody(String pbody, String sheetname) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        // return only the XML found at element = sheetname
        //return body;

//Parse the input document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(pbody.getBytes("UTF-8")));

        //Set up the transformer to write the output string
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        //Find the first child node - this could be done with xpath as well


        Element e = doc.getDocumentElement();
        NodeList nodelist = e.getChildNodes();
        DOMSource source = null;
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node childNode = nodelist.item(i);
            // this will match metadata
            if (childNode.getNodeName().equals(sheetname)) {
                source = new DOMSource(childNode);
                break;
            }

            // This matches individual worksheets
            NamedNodeMap nnm = childNode.getAttributes();
            for (int j = 0; j < nnm.getLength(); j++) {
                Attr attribute = (Attr) nnm.item(j);
                if ((attribute.getName().equalsIgnoreCase("sheetname") &&
                        attribute.getValue().equals(sheetname))) {
                    source = new DOMSource(childNode);
                    break;
                }
            }
        }

        //Do the transformation and output

        transformer.transform(source, result);

        String endresult = sw.toString();
        String nofirstline = endresult.substring(endresult.indexOf("\n"), endresult.length());

        return nofirstline;
    }

    /**
     * @return returns the body of the validation file for use in the bioValidator application
     */
    public String getBody() {
        return body;
    }

    /**
     * @return returns the body of the validation XSD file for use in the bioValidator application
     */
    public String getXSDBody() {
        return xsdbody;
    }

    /**
     * @return returns descriptive text of this file and date that it was cached to display on UI
     */
    public String getDescriptiveText() {

        if (!cacheExists()) {
            return "Unable to read " + vfChoice.getName() + " from cache or internet.  ";
        }
        String strDate = "";
        try {
            strDate = mcr.getHeaders().get("Date").toString();
        } catch (IOException e) {

        }
        DateFormat formatter = new SimpleDateFormat("[EEE, dd MMM yyyy HH:mm:ss z]", Locale.ENGLISH);
        formatter.setTimeZone(Calendar.getInstance().getTimeZone());
        Date dateCached = null;
        try {
            dateCached = (Date) formatter.parse(strDate);
        } catch (java.text.ParseException e) {

        }

        DateFormat outputformatter = new SimpleDateFormat("dd MMM yyyy z HH:mm:ss", Locale.ENGLISH);
        System.out.println(messageString + outputformatter.format(dateCached) + " (" + localAbsoluteFilename + ")");
        return messageString + outputformatter.format(dateCached) + " (" + localAbsoluteFilename + ")";
    }

    /**
     * @return returns the body of the validation file as an InputStream
     */
    public InputStream getBodyAsInputStream() {

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(body.getBytes("UTF-8"));
            //is = new ByteArrayInputStream(body.getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return is;
    }

    private boolean schemaValidation() {
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        // 2. Compile the schema.
        Schema schema = null;
        try {
            schema = factory.newSchema(XSDconfig);
            //schema = factory.newSchema(new URL("http://biocode.berkeley.edu/bioValidator.xsd"));
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        }

        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();

        // 4. Parse the document you want to check (just read the string that has been read already)
        Source source = new StreamSource(new StringReader(this.getBody()));

        // 5. Check the document
        try {
            validator.validate(source);
            System.out.println("IS valid.");
            return true;
        } catch (SAXException ex) {
            System.out.println("NOT valid because ");
            System.out.println(ex.getMessage());
            this.messageString += "Schema validation error. ";
            ex.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse the validationFile to get worksheets, they take the form:
     * <worksheetname type="Worksheet" />
     *
     * @return
     */
    public ArrayList getWorksheets() {

        ArrayList worksheets = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document d = null;
        try {

            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(body));
            d = builder.parse(is);

            Element e = d.getDocumentElement();
            NodeList nodelist = e.getChildNodes();

            for (int i = 0; i < nodelist.getLength(); i++) {
                Node childNode = nodelist.item(i);
                NamedNodeMap nnm = childNode.getAttributes();
                for (int j = 0; j < nnm.getLength(); j++) {
                    Attr attribute = (Attr) nnm.item(j);
                    //if (attribute.getName().equalsIgnoreCase("type") &&
                    //        attribute.getValue().equalsIgnoreCase("Worksheet")) {
                    //   worksheets.add(childNode.getNodeName());
                    //}
                    if (attribute.getName().equalsIgnoreCase("sheetname")) {
                        //System.out.println("worksheet name = " + attribute.getValue());
                        //System.out.println("name ofthis element= " + childNode.getNodeName());
                        worksheets.add(attribute.getValue());
                    }

                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            return worksheets;
        }
    }

}


