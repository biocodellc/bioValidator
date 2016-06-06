package edu.berkeley.biocode.validator;

import java.io.*;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

public class MyCacheResponse extends CacheResponse {
        FileInputStream fis;
        Map<String, List<String>> headers;

        public MyCacheResponse(String filename) {
            try {
                ObjectInputStream ois = null;

                fis = new FileInputStream(new File(filename));
                try {
                    ois = new ObjectInputStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headers = (Map<String, List<String>>) ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } catch (FileNotFoundException e) {
                System.out.println("Cache says nothing there, proceed ...");
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public InputStream getBody() throws IOException {
            return fis;
        }

        public Map getHeaders() throws IOException {
            return headers;
        }
    }

