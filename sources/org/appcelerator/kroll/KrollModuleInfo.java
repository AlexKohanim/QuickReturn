package org.appcelerator.kroll;

public class KrollModuleInfo {
    protected String author;
    protected String copyright;
    protected String description;
    protected String guid;

    /* renamed from: id */
    protected String f26id;
    protected boolean isJSModule = false;
    protected String license;
    protected String licenseKey;
    protected String name;
    protected String version;

    public KrollModuleInfo(String name2, String id, String guid2, String version2, String description2, String author2, String license2, String copyright2) {
        this.name = name2;
        this.f26id = id;
        this.guid = guid2;
        this.version = version2;
        this.description = description2;
        this.author = author2;
        this.license = license2;
        this.copyright = copyright2;
        this.licenseKey = null;
        this.isJSModule = false;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.f26id;
    }

    public String getGuid() {
        return this.guid;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getLicense() {
        return this.license;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public void setLicenseKey(String licenseKey2) {
        this.licenseKey = licenseKey2;
    }

    public boolean getIsJSModule() {
        return this.isJSModule;
    }

    public void setIsJSModule(boolean value) {
        this.isJSModule = value;
    }
}
