package p006ti.modules.titanium.xml;

import org.w3c.dom.EntityReference;

/* renamed from: ti.modules.titanium.xml.EntityReferenceProxy */
public class EntityReferenceProxy extends NodeProxy {
    public EntityReferenceProxy(EntityReference ref) {
        super(ref);
    }

    public String getApiName() {
        return "Ti.XML.EntityReference";
    }
}
