package p006ti.modules.titanium.xml;

import org.w3c.dom.DocumentType;

/* renamed from: ti.modules.titanium.xml.DocumentTypeProxy */
public class DocumentTypeProxy extends NodeProxy {
    private DocumentType type;

    public DocumentTypeProxy(DocumentType type2) {
        super(type2);
        this.type = type2;
    }

    public DocumentType getDocumentType() {
        return this.type;
    }

    public NamedNodeMapProxy getEntities() {
        return new NamedNodeMapProxy(this.type.getEntities());
    }

    public String getInternalSubset() {
        return this.type.getInternalSubset();
    }

    public String getName() {
        return this.type.getName();
    }

    public NamedNodeMapProxy getNotations() {
        return new NamedNodeMapProxy(this.type.getNotations());
    }

    public String getPublicId() {
        return this.type.getPublicId();
    }

    public String getSystemId() {
        return this.type.getSystemId();
    }

    public String getApiName() {
        return "Ti.XML.DocumentType";
    }
}
