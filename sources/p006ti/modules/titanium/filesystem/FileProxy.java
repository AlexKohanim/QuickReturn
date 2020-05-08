package p006ti.modules.titanium.filesystem;

import org.appcelerator.titanium.TiFileProxy;

/* renamed from: ti.modules.titanium.filesystem.FileProxy */
public class FileProxy extends TiFileProxy {
    public FileProxy(String sourceUrl, String[] parts) {
        super(sourceUrl, parts, true);
    }

    public FileProxy(String sourceUrl, String[] parts, boolean resolve) {
        super(sourceUrl, parts, resolve);
    }

    public String getApiName() {
        return "Ti.Filesystem.File";
    }
}
