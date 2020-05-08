package org.appcelerator.titanium.util;

import android.net.Uri;
import android.net.Uri.Builder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;

public class TiUrl {
    public static final String CURRENT_PATH = ".";
    public static final String CURRENT_PATH_WITH_SEPARATOR = "./";
    public static final String PARENT_PATH = "..";
    public static final String PARENT_PATH_WITH_SEPARATOR = "../";
    public static final String PATH_SEPARATOR = "/";
    public static final String SCHEME_SUFFIX = "://";
    protected static final String TAG = "TiUrl";
    private static HashMap<String, TiUrl> proxyUrlCache = new HashMap<>(5);
    public String baseUrl;
    public String url;

    public TiUrl(String url2) {
        this("app://", url2);
    }

    public TiUrl(String baseUrl2, String url2) {
        if (baseUrl2 == null) {
            baseUrl2 = "app://";
        }
        this.baseUrl = baseUrl2;
        if (url2 == null) {
            url2 = "";
        }
        this.url = url2;
    }

    public String getNormalizedUrl() {
        return normalizeWindowUrl(this.baseUrl, this.url).url;
    }

    protected static String parseRelativeBaseUrl(String path, String baseUrl2, boolean checkAppPrefix) {
        String[] left;
        String[] right = path.split(PATH_SEPARATOR);
        if (!baseUrl2.contains(SCHEME_SUFFIX)) {
            left = baseUrl2.split(PATH_SEPARATOR);
        } else if (!checkAppPrefix) {
            String[] tmp = baseUrl2.split(SCHEME_SUFFIX);
            if (tmp.length > 1) {
                left = tmp[1].split(PATH_SEPARATOR);
            } else {
                left = new String[0];
            }
        } else if (baseUrl2.equals("app://")) {
            left = new String[0];
        } else {
            left = baseUrl2.substring(baseUrl2.indexOf(SCHEME_SUFFIX) + 3).split(PATH_SEPARATOR);
        }
        int rIndex = 0;
        int lIndex = left.length;
        while (right[rIndex].equals(PARENT_PATH)) {
            lIndex--;
            rIndex++;
            if (rIndex > right.length - 1) {
                break;
            }
        }
        String sep = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lIndex; i++) {
            sb.append(sep).append(left[i]);
            sep = PATH_SEPARATOR;
        }
        for (int i2 = rIndex; i2 < right.length; i2++) {
            sb.append(sep).append(right[i2]);
            sep = PATH_SEPARATOR;
        }
        String bUrl = sb.toString();
        if (!bUrl.endsWith(PATH_SEPARATOR)) {
            return bUrl + PATH_SEPARATOR;
        }
        return bUrl;
    }

    public static TiUrl createProxyUrl(String url2) {
        if (proxyUrlCache.containsKey(url2)) {
            return (TiUrl) proxyUrlCache.get(url2);
        }
        if (url2 == null) {
            return new TiUrl(null);
        }
        int lastSlash = url2.lastIndexOf(PATH_SEPARATOR);
        String baseUrl2 = url2.substring(0, lastSlash + 1);
        if (baseUrl2.length() == 0) {
            baseUrl2 = "app://";
        }
        TiUrl result = new TiUrl(baseUrl2, url2.substring(lastSlash + 1));
        proxyUrlCache.put(url2, result);
        return result;
    }

    public static TiUrl normalizeWindowUrl(String url2) {
        String baseUrl2 = url2.substring(0, url2.lastIndexOf(PATH_SEPARATOR) + 1);
        if (baseUrl2.length() == 0) {
            baseUrl2 = "app://";
        }
        return normalizeWindowUrl(baseUrl2, url2);
    }

    public static TiUrl normalizeWindowUrl(String baseUrl2, String url2) {
        String fname;
        String path;
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Window Base URL: " + baseUrl2, Log.DEBUG_MODE);
            if (url2 != null) {
                Log.m29d(TAG, "Window Relative URL: " + url2, Log.DEBUG_MODE);
            }
        }
        try {
            URI uri = new URI(url2);
            String scheme = uri.getScheme();
            if (scheme == null) {
                String path2 = uri.getPath();
                if (path2 != null && path2.startsWith(CURRENT_PATH_WITH_SEPARATOR)) {
                    path2 = path2.length() == 2 ? "" : path2.substring(2);
                }
                int lastIndex = path2.lastIndexOf(PATH_SEPARATOR);
                if (lastIndex > 0) {
                    fname = path2.substring(lastIndex + 1);
                    path = path2.substring(0, lastIndex);
                } else {
                    fname = path2;
                    path = null;
                }
                if (url2.startsWith(PATH_SEPARATOR)) {
                    baseUrl2 = path == null ? "app://" : "app:/" + path;
                    url2 = TiFileHelper2.joinSegments(baseUrl2, fname);
                } else if (path == null && fname != null) {
                    url2 = TiFileHelper2.joinSegments(baseUrl2, fname);
                } else if (path.startsWith(PARENT_PATH_WITH_SEPARATOR)) {
                    baseUrl2 = "app://" + parseRelativeBaseUrl(path, baseUrl2, true);
                    url2 = TiFileHelper2.joinSegments(baseUrl2, fname);
                } else {
                    baseUrl2 = "app://" + path;
                    url2 = TiFileHelper2.joinSegments(baseUrl2, fname);
                }
            } else if (TiC.URL_APP_SCHEME.equals(scheme)) {
                baseUrl2 = url2;
            } else {
                throw new IllegalArgumentException("Scheme not implemented for " + url2);
            }
        } catch (URISyntaxException e) {
            Log.m44w(TAG, "Error parsing url: " + e.getMessage());
        }
        return new TiUrl(baseUrl2, url2);
    }

    public String resolve() {
        return resolve(this.baseUrl, this.url, null);
    }

    public String resolve(String path) {
        return resolve(this.baseUrl, path, null);
    }

    public String resolve(String baseUrl2, String path) {
        return resolve(baseUrl2, path, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x009a, code lost:
        if (r18.contains(CURRENT_PATH_WITH_SEPARATOR) != false) goto L_0x009c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String resolve(java.lang.String r17, java.lang.String r18, java.lang.String r19) {
        /*
            boolean r13 = org.appcelerator.titanium.p005io.TiFileFactory.isLocalScheme(r18)
            if (r13 != 0) goto L_0x000b
            r8 = r18
            r6 = r18
        L_0x000a:
            return r6
        L_0x000b:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "android.resource://"
            java.lang.StringBuilder r13 = r13.append(r14)
            org.appcelerator.titanium.TiApplication r14 = org.appcelerator.titanium.TiApplication.getInstance()
            java.lang.String r14 = r14.getPackageName()
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = "/raw/"
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r13 = r13.toString()
            r0 = r18
            boolean r13 = r0.startsWith(r13)
            if (r13 == 0) goto L_0x0070
            android.net.Uri r11 = android.net.Uri.parse(r18)
            java.lang.String r3 = r11.getLastPathSegment()
            r13 = 46
            int r5 = r3.lastIndexOf(r13)
            if (r5 > 0) goto L_0x006a
            r4 = r3
        L_0x0045:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r14 = 0
            int r15 = r18.length()
            int r16 = r3.length()
            int r15 = r15 - r16
            r0 = r18
            java.lang.String r14 = r0.substring(r14, r15)
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.StringBuilder r13 = r13.append(r4)
            java.lang.String r6 = r13.toString()
            r8 = r18
            goto L_0x000a
        L_0x006a:
            r13 = 0
            java.lang.String r4 = r3.substring(r13, r5)
            goto L_0x0045
        L_0x0070:
            r9 = 0
            if (r19 != 0) goto L_0x0075
            java.lang.String r19 = "app:"
        L_0x0075:
            java.lang.String r13 = "./"
            r0 = r18
            boolean r13 = r0.startsWith(r13)
            if (r13 == 0) goto L_0x0088
            int r13 = r18.length()
            r14 = 2
            if (r13 != r14) goto L_0x00eb
            java.lang.String r18 = ""
        L_0x0088:
            java.lang.String r13 = "../"
            r0 = r18
            boolean r13 = r0.contains(r13)
            if (r13 != 0) goto L_0x009c
            java.lang.String r13 = "./"
            r0 = r18
            boolean r13 = r0.contains(r13)
            if (r13 == 0) goto L_0x00a6
        L_0x009c:
            r0 = r19
            r1 = r18
            r2 = r17
            java.lang.String r18 = absoluteUrl(r0, r1, r2)
        L_0x00a6:
            android.net.Uri r12 = android.net.Uri.parse(r18)
            java.lang.String r13 = r12.getScheme()
            if (r13 != 0) goto L_0x010f
            java.lang.String r13 = "/"
            r0 = r18
            boolean r13 = r0.startsWith(r13)
            if (r13 != 0) goto L_0x00f3
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r0 = r17
            java.lang.StringBuilder r13 = r13.append(r0)
            r0 = r18
            java.lang.StringBuilder r13 = r13.append(r0)
            java.lang.String r9 = r13.toString()
        L_0x00cf:
            java.lang.String r13 = "file:"
            boolean r13 = r9.startsWith(r13)
            if (r13 != 0) goto L_0x00e6
            r13 = 1
            java.lang.String[] r7 = new java.lang.String[r13]
            r13 = 0
            r7[r13] = r9
            r13 = 0
            org.appcelerator.titanium.io.TiBaseFile r10 = org.appcelerator.titanium.p005io.TiFileFactory.createTitaniumFile(r7, r13)
            java.lang.String r9 = r10.nativePath()
        L_0x00e6:
            r8 = r18
            r6 = r9
            goto L_0x000a
        L_0x00eb:
            r13 = 2
            r0 = r18
            java.lang.String r18 = r0.substring(r13)
            goto L_0x0088
        L_0x00f3:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r0 = r19
            java.lang.StringBuilder r13 = r13.append(r0)
            java.lang.String r14 = "/"
            java.lang.StringBuilder r13 = r13.append(r14)
            r0 = r18
            java.lang.StringBuilder r13 = r13.append(r0)
            java.lang.String r9 = r13.toString()
            goto L_0x00cf
        L_0x010f:
            r9 = r18
            goto L_0x00cf
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiUrl.resolve(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0007, code lost:
        if (r10.length() == 0) goto L_0x0009;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String absoluteUrl(java.lang.String r8, java.lang.String r9, java.lang.String r10) {
        /*
            r6 = 1
            if (r10 == 0) goto L_0x0009
            int r5 = r10.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x002a
        L_0x0009:
            if (r9 == 0) goto L_0x0011
            int r5 = r9.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x002a
        L_0x0011:
            if (r8 != 0) goto L_0x0016
            java.lang.String r5 = ""
        L_0x0015:
            return r5
        L_0x0016:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r8)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = "//"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0015
        L_0x002a:
            java.lang.String r0 = ""
            if (r10 == 0) goto L_0x0034
            int r5 = r10.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x0068
        L_0x0034:
            r0 = r9
        L_0x0035:
            java.net.URI r3 = new java.net.URI     // Catch:{ URISyntaxException -> 0x0159 }
            r3.<init>(r0)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = r3.getScheme()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x010a
            java.net.URI r3 = r3.normalize()     // Catch:{ URISyntaxException -> 0x0159 }
        L_0x0044:
            java.lang.String r5 = r3.getScheme()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x0153
            if (r8 == 0) goto L_0x014d
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r8)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = "//"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = r3.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0015
        L_0x0068:
            if (r9 == 0) goto L_0x0070
            int r5 = r9.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x0072
        L_0x0070:
            r0 = r10
            goto L_0x0035
        L_0x0072:
            java.net.URI r3 = new java.net.URI     // Catch:{ URISyntaxException -> 0x0159 }
            r3.<init>(r9)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = r3.getScheme()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 == 0) goto L_0x007f
            r0 = r9
            goto L_0x0035
        L_0x007f:
            java.lang.String r5 = "/"
            boolean r5 = r10.endsWith(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 == 0) goto L_0x00ce
            java.lang.String r5 = "/"
            boolean r5 = r9.startsWith(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 == 0) goto L_0x00ce
            java.lang.String r5 = "file://"
            boolean r5 = r10.equals(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x00ce
            int r5 = r10.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != r6) goto L_0x00a6
            int r5 = r9.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != r6) goto L_0x00a6
            java.lang.String r0 = "/"
            goto L_0x0035
        L_0x00a6:
            int r5 = r10.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != r6) goto L_0x00ae
            r0 = r9
            goto L_0x0035
        L_0x00ae:
            int r5 = r9.length()     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != r6) goto L_0x00b6
            r0 = r10
            goto L_0x0035
        L_0x00b6:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r10)     // Catch:{ URISyntaxException -> 0x0159 }
            r6 = 1
            java.lang.String r6 = r9.substring(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r0 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0035
        L_0x00ce:
            java.lang.String r5 = "/"
            boolean r5 = r10.endsWith(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x00f7
            java.lang.String r5 = "/"
            boolean r5 = r9.startsWith(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            if (r5 != 0) goto L_0x00f7
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r10)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = "/"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r9)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r0 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0035
        L_0x00f7:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r10)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r9)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r0 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0035
        L_0x010a:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = r3.getScheme()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = "://"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r2 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = ""
            java.lang.String r0 = r0.replace(r2, r5)     // Catch:{ URISyntaxException -> 0x0159 }
            java.net.URI r5 = new java.net.URI     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>(r0)     // Catch:{ URISyntaxException -> 0x0159 }
            java.net.URI r3 = r5.normalize()     // Catch:{ URISyntaxException -> 0x0159 }
            java.net.URI r4 = new java.net.URI     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ URISyntaxException -> 0x0159 }
            r5.<init>()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r6 = r3.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ URISyntaxException -> 0x0159 }
            java.lang.String r5 = r5.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            r4.<init>(r5)     // Catch:{ URISyntaxException -> 0x0159 }
            r3 = r4
            goto L_0x0044
        L_0x014d:
            java.lang.String r5 = r3.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0015
        L_0x0153:
            java.lang.String r5 = r3.toString()     // Catch:{ URISyntaxException -> 0x0159 }
            goto L_0x0015
        L_0x0159:
            r1 = move-exception
            java.lang.String r5 = "TiUrl"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Error parsing url: "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r1.getMessage()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            org.appcelerator.kroll.common.Log.m44w(r5, r6)
            r5 = r9
            goto L_0x0015
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiUrl.absoluteUrl(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    public static Uri getCleanUri(String argString) {
        if (argString == null) {
            return null;
        }
        try {
            Uri base = Uri.parse(argString);
            Builder builder = base.buildUpon();
            builder.encodedQuery(Uri.encode(Uri.decode(base.getQuery()), "&="));
            String encodedAuthority = Uri.encode(Uri.decode(base.getAuthority()), "/:@");
            int firstAt = encodedAuthority.indexOf(64);
            if (firstAt >= 0) {
                int lastAt = encodedAuthority.lastIndexOf(64);
                if (lastAt > firstAt) {
                    encodedAuthority = Uri.encode(encodedAuthority.substring(0, lastAt), "/:") + encodedAuthority.substring(lastAt);
                }
            }
            builder.encodedAuthority(encodedAuthority);
            builder.encodedPath(Uri.encode(Uri.decode(base.getPath()), "/:@$+&=;,"));
            return builder.build();
        } catch (Exception e) {
            Log.m32e(TAG, "Exception in getCleanUri argString= " + argString);
            return null;
        }
    }
}
