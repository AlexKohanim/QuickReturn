package p006ti.modules.titanium.xml;

import org.w3c.dom.CDATASection;

/* renamed from: ti.modules.titanium.xml.CDATASectionProxy */
public class CDATASectionProxy extends TextProxy {
    public CDATASectionProxy(CDATASection section) {
        super(section);
    }

    public String getApiName() {
        return "Ti.XML.CDATASection";
    }
}
