package edu.berkeley.biocode.validator;

import java.io.IOException;
import java.net.*;
import java.util.Map;

class MyResponseCache extends ResponseCache {
        private String filename;

        MyResponseCache(String filename) {
            this.filename = filename;
        }

        public CacheResponse get(URI uri, String rqstMethod, Map rqstHeaders) throws IOException {
            return new MyCacheResponse(filename);
        }

        public CacheRequest put(URI uri, URLConnection conn) throws IOException {
            return new MyCacheRequest(filename, conn.getHeaderFields());
        }
    }