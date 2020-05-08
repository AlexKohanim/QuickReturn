package p006ti.modules.titanium.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

/* renamed from: ti.modules.titanium.xml.AttrProxy */
public class AttrProxy extends NodeProxy {
    private Attr attr;

    public AttrProxy(Attr attr2) {
        super(attr2);
        this.attr = attr2;
    }

    public Attr getAttr() {
        return this.attr;
    }

    public String getName() {
        return this.attr.getName();
    }

    public ElementProxy getOwnerElement() {
        return (ElementProxy) getProxy(this.attr.getOwnerElement());
    }

    public boolean getSpecified() {
        if (this.attr.getOwnerElement() == null) {
            return true;
        }
        return this.attr.getSpecified();
    }

    public String getValue() {
        return this.attr.getValue();
    }

    public void setValue(String value) throws DOMException {
        this.attr.setValue(value);
    }

    public String getApiName() {
        return "Ti.XML.Attr";
    }
}
