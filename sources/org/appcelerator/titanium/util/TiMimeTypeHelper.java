package org.appcelerator.titanium.util;

import android.webkit.MimeTypeMap;
import java.util.HashMap;
import org.appcelerator.titanium.TiC;

public class TiMimeTypeHelper {
    public static final HashMap<String, String> EXTRA_MIMETYPES = new HashMap<>();
    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_JAVASCRIPT = "text/javascript";

    static {
        EXTRA_MIMETYPES.put("js", MIME_TYPE_JAVASCRIPT);
        EXTRA_MIMETYPES.put(TiC.PROPERTY_HTML, MIME_TYPE_HTML);
        EXTRA_MIMETYPES.put("htm", MIME_TYPE_HTML);
    }

    public static String getMimeType(String url) {
        return getMimeType(url, "application/octet-stream");
    }

    public static String getMimeTypeFromFileExtension(String extension, String defaultType) {
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        String mimetype = defaultType;
        if (extension == null) {
            return mimetype;
        }
        String type = mtm.getMimeTypeFromExtension(extension);
        if (type != null) {
            return type;
        }
        String lowerExtension = extension.toLowerCase();
        if (EXTRA_MIMETYPES.containsKey(lowerExtension)) {
            return (String) EXTRA_MIMETYPES.get(lowerExtension);
        }
        return mimetype;
    }

    public static String getFileExtensionFromUrl(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    public static String getMimeType(String url, String defaultType) {
        String extension = "";
        int pos = url.lastIndexOf(46);
        if (pos > 0) {
            extension = url.substring(pos + 1);
        }
        return getMimeTypeFromFileExtension(extension, defaultType);
    }

    public static String getFileExtensionFromMimeType(String mimeType, String defaultExtension) {
        String result = defaultExtension;
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (extension != null) {
            result = extension;
        } else {
            for (String ext : EXTRA_MIMETYPES.keySet()) {
                if (((String) EXTRA_MIMETYPES.get(ext)).equalsIgnoreCase(mimeType)) {
                    String str = result;
                    return ext;
                }
            }
        }
        String str2 = result;
        return result;
    }

    public static boolean isBinaryMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        String mimeType2 = mimeType.split(";")[0];
        if (mimeType2.startsWith("application/") && !mimeType2.endsWith("xml")) {
            return true;
        }
        if ((!mimeType2.startsWith("image/") || mimeType2.endsWith("xml")) && !mimeType2.startsWith("audio/") && !mimeType2.startsWith("video/")) {
            return false;
        }
        return true;
    }
}
