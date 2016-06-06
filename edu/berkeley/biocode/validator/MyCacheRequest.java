package edu.berkeley.biocode.validator;

import java.io.*;
import java.net.CacheRequest;
import java.util.List;
import java.util.Map;

public class MyCacheRequest extends CacheRequest {
        FileOutputStream fos;

        public MyCacheRequest(String filename, Map<String, List<String>> rspHeaders) {
            try {
                File file = new File(filename);
                fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(rspHeaders);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        public OutputStream getBody() throws IOException {
            return fos;
        }

        public void abort() {
            // we abandon the cache by closing the stream
            try {
                fos.close();
            } catch (Exception e) {

            }
        }
    }