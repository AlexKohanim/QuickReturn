package org.appcelerator.titanium.util;

import android.os.Build.VERSION;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.digest.DigestUtils;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.analytics.TiAnalyticsEventFactory;

public class TiResponseCache extends ResponseCache {
    static final /* synthetic */ boolean $assertionsDisabled = (!TiResponseCache.class.desiredAssertionStatus());
    private static final String BODY_SUFFIX = ".bdy";
    private static final String CACHE_SIZE_KEY = "ti.android.cache.size.max";
    private static final int CLEANUP_DELAY = 60000;
    private static final int DEFAULT_CACHE_SIZE = 26214400;
    private static final String HEADER_SUFFIX = ".hdr";
    private static final int INITIAL_DELAY = 10000;
    private static final String TAG = "TiResponseCache";
    private static ScheduledExecutorService cleanupExecutor = null;
    private static HashMap<String, ArrayList<CompleteListener>> completeListeners = new HashMap<>();
    private static long maxCacheSize = 0;
    private static final List<String> videoFormats = new ArrayList(Arrays.asList(new String[]{"mkv", "webm", "3gp", "mp4", "ts"}));
    private File cacheDir = null;

    public interface CompleteListener {
        void cacheCompleted(URI uri);
    }

    private static class TiCacheCleanup implements Runnable {
        private File cacheDir;
        private long maxSize;

        public TiCacheCleanup(File cacheDir2, long maxSize2) {
            this.cacheDir = cacheDir2;
            this.maxSize = maxSize2;
        }

        public void run() {
            File[] listFiles;
            HashMap<Long, File> lastTime = new HashMap<>();
            for (File hdrFile : this.cacheDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(TiResponseCache.HEADER_SUFFIX);
                }
            })) {
                lastTime.put(Long.valueOf(hdrFile.lastModified()), hdrFile);
            }
            List<Long> sz = new ArrayList<>(lastTime.keySet());
            Collections.sort(sz);
            Collections.reverse(sz);
            long cacheSize = 0;
            for (Long last : sz) {
                File hdrFile2 = (File) lastTime.get(last);
                File bdyFile = new File(this.cacheDir, hdrFile2.getName().substring(0, hdrFile2.getName().lastIndexOf(46)) + TiResponseCache.BODY_SUFFIX);
                cacheSize = cacheSize + hdrFile2.length() + bdyFile.length();
                if (cacheSize > this.maxSize) {
                    hdrFile2.delete();
                    bdyFile.delete();
                }
            }
        }
    }

    private static class TiCacheOutputStream extends FileOutputStream {
        private URI uri;

        public TiCacheOutputStream(URI uri2, File file) throws FileNotFoundException {
            super(file);
            this.uri = uri2;
        }

        public void close() throws IOException {
            super.close();
            TiResponseCache.fireCacheCompleted(this.uri);
        }
    }

    private static class TiCacheRequest extends CacheRequest {
        private File bFile;
        private long contentLength;
        private File hFile;
        private URI uri;

        public TiCacheRequest(URI uri2, File bFile2, File hFile2, long contentLength2) {
            this.uri = uri2;
            this.bFile = bFile2;
            this.hFile = hFile2;
            this.contentLength = contentLength2;
        }

        public OutputStream getBody() throws IOException {
            return new TiCacheOutputStream(this.uri, this.bFile);
        }

        public void abort() {
            if (this.bFile.length() != this.contentLength) {
                Log.m32e(TiResponseCache.TAG, "Failed to add item to the cache!");
                if (this.bFile.exists()) {
                    this.bFile.delete();
                }
                if (this.hFile.exists()) {
                    this.hFile.delete();
                }
            }
        }
    }

    private static class TiCacheResponse extends CacheResponse {
        private Map<String, List<String>> headers;
        private InputStream istream;

        public TiCacheResponse(Map<String, List<String>> hdrs, InputStream istr) {
            this.headers = hdrs;
            this.istream = istr;
        }

        public Map<String, List<String>> getHeaders() throws IOException {
            return this.headers;
        }

        public InputStream getBody() throws IOException {
            return this.istream;
        }
    }

    public static boolean peek(URI uri) {
        ResponseCache rcc = getDefault();
        if (rcc instanceof TiResponseCache) {
            TiResponseCache rc = (TiResponseCache) rcc;
            if (rc.cacheDir == null) {
                return false;
            }
            String hash = DigestUtils.shaHex(uri.toString());
            File hFile = new File(rc.cacheDir, hash + HEADER_SUFFIX);
            if (!new File(rc.cacheDir, hash + BODY_SUFFIX).exists() || !hFile.exists()) {
                return false;
            }
            return true;
        } else if (rcc != null) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        return r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return r10;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0082 A[ExcHandler: FileNotFoundException (e java.io.FileNotFoundException), Splitter:B:9:0x005a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.InputStream openCachedStream(java.net.URI r14) {
        /*
            r10 = 0
            java.net.ResponseCache r8 = getDefault()
            boolean r11 = r8 instanceof org.appcelerator.titanium.util.TiResponseCache
            if (r11 == 0) goto L_0x0086
            r7 = r8
            org.appcelerator.titanium.util.TiResponseCache r7 = (org.appcelerator.titanium.util.TiResponseCache) r7
            java.io.File r11 = r7.cacheDir
            if (r11 != 0) goto L_0x0011
        L_0x0010:
            return r10
        L_0x0011:
            java.lang.String r11 = r14.toString()
            java.lang.String r4 = org.apache.commons.codec.digest.DigestUtils.shaHex(r11)
            java.io.File r3 = new java.io.File
            java.io.File r11 = r7.cacheDir
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.StringBuilder r12 = r12.append(r4)
            java.lang.String r13 = ".hdr"
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            r3.<init>(r11, r12)
            java.io.File r0 = new java.io.File
            java.io.File r11 = r7.cacheDir
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.StringBuilder r12 = r12.append(r4)
            java.lang.String r13 = ".bdy"
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            r0.<init>(r11, r12)
            boolean r11 = r0.exists()
            if (r11 == 0) goto L_0x0010
            boolean r11 = r3.exists()
            if (r11 == 0) goto L_0x0010
            r6 = 0
            java.util.Map r5 = readHeaders(r3)     // Catch:{ IOException -> 0x00a0, FileNotFoundException -> 0x0082 }
            java.lang.String r11 = "content-encoding"
            java.lang.String r1 = getHeader(r5, r11)     // Catch:{ IOException -> 0x00a0, FileNotFoundException -> 0x0082 }
            java.lang.String r11 = "gzip"
            boolean r11 = r11.equalsIgnoreCase(r1)     // Catch:{ IOException -> 0x00a0, FileNotFoundException -> 0x0082 }
            if (r11 == 0) goto L_0x006d
            r6 = 1
        L_0x006d:
            if (r6 == 0) goto L_0x007b
            java.util.zip.GZIPInputStream r11 = new java.util.zip.GZIPInputStream     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            java.io.FileInputStream r12 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            r12.<init>(r0)     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            r11.<init>(r12)     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            r10 = r11
            goto L_0x0010
        L_0x007b:
            java.io.FileInputStream r11 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            r11.<init>(r0)     // Catch:{ FileNotFoundException -> 0x0082, IOException -> 0x0084 }
            r10 = r11
            goto L_0x0010
        L_0x0082:
            r2 = move-exception
            goto L_0x0010
        L_0x0084:
            r2 = move-exception
            goto L_0x0010
        L_0x0086:
            if (r8 == 0) goto L_0x0010
            java.net.URL r11 = r14.toURL()     // Catch:{ Exception -> 0x009d }
            java.net.URLConnection r9 = r11.openConnection()     // Catch:{ Exception -> 0x009d }
            java.lang.String r11 = "Cache-Control"
            java.lang.String r12 = "only-if-cached"
            r9.setRequestProperty(r11, r12)     // Catch:{ Exception -> 0x009d }
            java.io.InputStream r10 = r9.getInputStream()     // Catch:{ Exception -> 0x009d }
            goto L_0x0010
        L_0x009d:
            r2 = move-exception
            goto L_0x0010
        L_0x00a0:
            r11 = move-exception
            goto L_0x006d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiResponseCache.openCachedStream(java.net.URI):java.io.InputStream");
    }

    public static void addCompleteListener(URI uri, CompleteListener listener) {
        synchronized (completeListeners) {
            String hash = DigestUtils.shaHex(uri.toString());
            if (!completeListeners.containsKey(hash)) {
                completeListeners.put(hash, new ArrayList());
            }
            ((ArrayList) completeListeners.get(hash)).add(listener);
        }
    }

    public TiResponseCache(File cachedir, TiApplication tiApp) {
        if ($assertionsDisabled || cachedir.isDirectory()) {
            this.cacheDir = cachedir;
            maxCacheSize = (long) (tiApp.getAppProperties().getInt(CACHE_SIZE_KEY, DEFAULT_CACHE_SIZE) * 1024);
            Log.m29d(TAG, "max cache size is:" + maxCacheSize, Log.DEBUG_MODE);
            cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
            cleanupExecutor.scheduleWithFixedDelay(new TiCacheCleanup(this.cacheDir, maxCacheSize), 10000, TiAnalyticsEventFactory.MAX_GEO_ANALYTICS_FREQUENCY, TimeUnit.MILLISECONDS);
            return;
        }
        throw new AssertionError("cachedir MUST be a directory");
    }

    public CacheResponse get(URI uri, String rqstMethod, Map<String, List<String>> map) throws IOException {
        if (!TiFileHelper2.hasStoragePermission() || uri == null || this.cacheDir == null) {
            return null;
        }
        if (videoFormats.contains(TiMimeTypeHelper.getFileExtensionFromUrl(uri.toString()).toLowerCase()) && VERSION.SDK_INT >= 21) {
            return null;
        }
        String hash = DigestUtils.shaHex(uri.toString());
        File hFile = new File(this.cacheDir, hash + HEADER_SUFFIX);
        File bFile = new File(this.cacheDir, hash + BODY_SUFFIX);
        if (!bFile.exists() || !hFile.exists()) {
            return null;
        }
        Map<String, List<String>> headers = readHeaders(hFile);
        hFile.setLastModified(System.currentTimeMillis());
        return new TiCacheResponse(headers, new FileInputStream(bFile));
    }

    private static Map<String, List<String>> readHeaders(File hFile) throws IOException {
        Map<String, List<String>> headers = new HashMap<>();
        BufferedReader rdr = new BufferedReader(new FileReader(hFile), 1024);
        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
            String[] keyval = line.split("=", 2);
            if (keyval.length >= 2) {
                if ("null".equals(keyval[0])) {
                    keyval[0] = null;
                }
                if (!headers.containsKey(keyval[0])) {
                    headers.put(keyval[0], new ArrayList());
                }
                ((List) headers.get(keyval[0])).add(keyval[1]);
            }
        }
        rdr.close();
        return headers;
    }

    protected static String getHeader(Map<String, List<String>> headers, String header) {
        List<String> values = (List) headers.get(header);
        if (values == null || values.size() == 0) {
            return null;
        }
        return (String) values.get(values.size() - 1);
    }

    /* access modifiers changed from: protected */
    public int getHeaderInt(Map<String, List<String>> headers, String header, int defaultValue) {
        String value = getHeader(headers, header);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Map<String, List<String>> makeLowerCaseHeaders(Map<String, List<String>> origHeaders) {
        Map<String, List<String>> headers = new HashMap<>(origHeaders.size());
        for (String key : origHeaders.keySet()) {
            if (key != null) {
                headers.put(key.toLowerCase(), origHeaders.get(key));
            } else {
                headers.put("null", origHeaders.get(key));
            }
        }
        return headers;
    }

    public CacheRequest put(URI uri, URLConnection conn) throws IOException {
        if (this.cacheDir == null || !TiFileHelper2.hasStoragePermission()) {
            return null;
        }
        if (videoFormats.contains(TiMimeTypeHelper.getFileExtensionFromUrl(uri.toString()).toLowerCase()) && VERSION.SDK_INT >= 21) {
            return null;
        }
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }
        Map<String, List<String>> headers = makeLowerCaseHeaders(conn.getHeaderFields());
        String cacheControl = getHeader(headers, "cache-control");
        if (cacheControl != null && cacheControl.matches("^.*(no-cache|no-store|must-revalidate|max-age=0).*")) {
            return null;
        }
        boolean skipTransferEncodingHeader = false;
        String tEncoding = getHeader(headers, "transfer-encoding");
        if (tEncoding != null && tEncoding.toLowerCase().equals("chunked")) {
            skipTransferEncodingHeader = true;
        }
        String newl = System.getProperty("line.separator");
        long contentLength = (long) getHeaderInt(headers, "content-length", 0);
        StringBuilder sb = new StringBuilder();
        for (String hdr : headers.keySet()) {
            if (!skipTransferEncodingHeader || !hdr.equals("transfer-encoding")) {
                for (String val : (List) headers.get(hdr)) {
                    sb.append(hdr);
                    sb.append("=");
                    sb.append(val);
                    sb.append(newl);
                }
            }
        }
        if (((long) sb.length()) + contentLength > maxCacheSize) {
            return null;
        }
        try {
            uri = conn.getURL().toURI();
        } catch (URISyntaxException e) {
        }
        String hash = DigestUtils.shaHex(uri.toString());
        File hFile = new File(this.cacheDir, hash + HEADER_SUFFIX);
        File bFile = new File(this.cacheDir, hash + BODY_SUFFIX);
        FileWriter hWriter = new FileWriter(hFile);
        try {
            hWriter.write(sb.toString());
            synchronized (this) {
                if (!bFile.createNewFile()) {
                    return null;
                }
                TiCacheRequest tiCacheRequest = new TiCacheRequest(uri, bFile, hFile, contentLength);
                return tiCacheRequest;
            }
        } finally {
            hWriter.close();
        }
    }

    public void setCacheDir(File dir) {
        this.cacheDir = dir;
    }

    /* access modifiers changed from: private */
    public static final void fireCacheCompleted(URI uri) {
        synchronized (completeListeners) {
            String hash = DigestUtils.shaHex(uri.toString());
            if (completeListeners.containsKey(hash)) {
                Iterator it = ((ArrayList) completeListeners.get(hash)).iterator();
                while (it.hasNext()) {
                    ((CompleteListener) it.next()).cacheCompleted(uri);
                }
                completeListeners.remove(hash);
            }
        }
    }
}
