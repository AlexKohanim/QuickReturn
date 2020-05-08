package p006ti.modules.titanium.app.properties;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiProperties;

/* renamed from: ti.modules.titanium.app.properties.PropertiesModule */
public class PropertiesModule extends KrollModule {
    private TiProperties appProperties = TiApplication.getInstance().getAppProperties();

    public boolean getBool(String key) {
        return this.appProperties.getBool(key, false);
    }

    public double getDouble(String key) {
        return this.appProperties.getDouble(key, 0.0d);
    }

    public int getInt(String key) {
        return this.appProperties.getInt(key, 0);
    }

    public String getString(String key) {
        return this.appProperties.getString(key, null);
    }

    public boolean hasProperty(String key) {
        return this.appProperties.hasProperty(key);
    }

    public String[] listProperties() {
        return this.appProperties.listProperties();
    }

    public void removeProperty(String key) {
        if (hasProperty(key)) {
            this.appProperties.removeProperty(key);
            fireEvent("change", null);
        }
    }

    public void removeAllProperties() {
        this.appProperties.removeAllProperties();
    }

    public void setBool(String key, boolean value) {
        Object boolValue = this.appProperties.getPreference(key);
        if (boolValue == null || !boolValue.equals(Boolean.valueOf(value))) {
            this.appProperties.setBool(key, value);
            fireEvent("change", null);
        }
    }

    public void setDouble(String key, double value) {
        Object doubleValue = this.appProperties.getPreference(key);
        if (doubleValue == null || !doubleValue.equals(String.valueOf(value))) {
            this.appProperties.setDouble(key, value);
            fireEvent("change", null);
        }
    }

    public void setInt(String key, int value) {
        Object intValue = this.appProperties.getPreference(key);
        if (intValue == null || !intValue.equals(Integer.valueOf(value))) {
            this.appProperties.setInt(key, value);
            fireEvent("change", null);
        }
    }

    public void setString(String key, String value) {
        Object stringValue = this.appProperties.getPreference(key);
        if (stringValue == null || !stringValue.equals(value)) {
            this.appProperties.setString(key, value);
            fireEvent("change", null);
        }
    }

    public String getApiName() {
        return "Ti.App.Properties";
    }
}
