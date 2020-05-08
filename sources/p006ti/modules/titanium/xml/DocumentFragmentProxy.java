package p006ti.modules.titanium.xml;

import org.w3c.dom.DocumentFragment;

/* renamed from: ti.modules.titanium.xml.DocumentFragmentProxy */
public class DocumentFragmentProxy extends NodeProxy {
    public DocumentFragmentProxy(DocumentFragment fragment) {
        super(fragment);
    }

    public String getApiName() {
        return "Ti.XML.DocumentFragment";
    }
}
