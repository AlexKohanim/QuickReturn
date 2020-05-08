package p006ti.modules.titanium.network.httpurlconnection;

/* renamed from: ti.modules.titanium.network.httpurlconnection.NameValuePair */
public class NameValuePair {
    private String name;
    private String value;

    public NameValuePair() {
        this(null, null);
    }

    public NameValuePair(String name2, String value2) {
        this.name = name2;
        this.value = value2;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value2) {
        this.value = value2;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "name=" + this.name + ", value=" + this.value;
    }
}
