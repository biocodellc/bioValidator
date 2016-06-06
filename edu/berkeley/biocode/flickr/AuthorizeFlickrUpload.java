package edu.berkeley.biocode.flickr;


import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.collections.Collection;
import com.flickr4java.flickr.test.TestInterface;
import com.flickr4java.flickr.uploader.Uploader;
import edu.berkeley.biocode.utils.BareBonesBrowserLaunch;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * AuthorizeFlickrUpload is generic class to authorize flickr uploads.
 */
public class AuthorizeFlickrUpload {
    Flickr f;
    RequestContext requestContext;
    String frob = "";
    public String token = "";
    public String apiKey = "7b9b362c4249b8be92ccea602f5a1850";
    public String sharedSecret = "4659049922e5f50b";
    public Uploader up;
    public Auth auth = null;
    public static final String TokenPreferenceName = "FlickrToken";

    public AuthorizeFlickrUpload() throws Exception {
        f = new Flickr(
                apiKey,
                sharedSecret,
                new REST()
        );

        f.setSharedSecret(sharedSecret);

        TestInterface testInterface = f.getTestInterface();
        java.util.Collection<Element> results = testInterface.echo(Collections.EMPTY_MAP);

        Flickr.debugStream = false;
    }


    /**
     * The following method is for command-line or local applications and stores the token in the preferences file
     * @return
     * @throws Exception
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public Auth getAuthCommandLine() throws Exception, SAXException, ParserConfigurationException {
        Preferences prefsRoot = Preferences.userRoot();
            Preferences prefs = prefsRoot.node("fimsPhotoLoader");

        Auth auth = null;
        AuthInterface authInterface = f.getAuthInterface();

        // Attempt to get a token from our preferences
        String tokenString = prefs.get(TokenPreferenceName, null);

        // no token, need to fetch it
        if (tokenString == null) {
            Scanner scanner = new Scanner(System.in);
            Token token = authInterface.getRequestToken();
            System.out.println("token: " + token);

            String url = authInterface.getAuthorizationUrl(token, Permission.DELETE);

            System.out.println("Follow this URL to authorise yourself on Flickr");
            System.out.println(url);
            System.out.println("Paste in the token it gives you:");
            System.out.print(">>");

            String tokenKey = scanner.nextLine();
            scanner.close();

            Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
            System.out.println("Authentication success");

            auth = authInterface.checkToken(requestToken);

            // Save token in Preferences
            prefs.put(TokenPreferenceName, auth.getToken());

            return auth;

        } else {
            try {
                System.out.println("reading token from system preferences!");
                return authInterface.checkToken(tokenString, sharedSecret);
            } catch (FlickrException e) {
                prefs.remove("FlickrToken");
                e.printStackTrace();
                //AuthorizeFlickrUpload a = new AuthorizeFlickrUpload();
                //a.init();

                return null;
            }
        }
    }


    /**
     * The following method is for the user interface.
     * @return
     */
    public Auth getAuthInterface() {
        Auth auth = null;

        return auth;
    }

    public static void main(String[] args) {
        try {
            AuthorizeFlickrUpload t = new AuthorizeFlickrUpload();

            Auth auth = t.getAuthCommandLine();

            System.out.println("Authentication success");
            // This token can be used until the user revokes it.
            System.out.println("Token: " + auth.getToken());
            System.out.println("nsid: " + auth.getUser().getId());
            System.out.println("Realname: " + auth.getUser().getRealName());
            //System.out.println("Username: " + auth.getUser().getUsername());
            //System.out.println("Permission: " + auth.getPermission().getType());

            t.up = new Uploader(t.apiKey, t.sharedSecret);
            t.up = t.f.getUploader();


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
