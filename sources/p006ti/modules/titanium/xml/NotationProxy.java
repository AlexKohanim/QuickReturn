package p006ti.modules.titanium.xml;

import org.w3c.dom.Notation;

/* renamed from: ti.modules.titanium.xml.NotationProxy */
public class NotationProxy extends NodeProxy {
    private Notation notation;

    public NotationProxy(Notation notation2) {
        super(notation2);
        this.notation = notation2;
    }

    public String getPublicId() {
        return this.notation.getPublicId();
    }

    public String getSystemId() {
        return this.notation.getSystemId();
    }

    public String getApiName() {
        return "Ti.XML.Notation";
    }
}
