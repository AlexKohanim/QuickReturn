package p006ti.modules.titanium.xml;

import org.appcelerator.kroll.KrollProxy;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

/* renamed from: ti.modules.titanium.xml.DOMImplementationProxy */
public class DOMImplementationProxy extends KrollProxy {
    private DOMImplementation impl;

    public DOMImplementationProxy(DOMImplementation impl2) {
        this.impl = impl2;
    }

    public DocumentProxy createDocument(String namespaceURI, String qualifiedName, DocumentTypeProxy doctype) throws DOMException {
        DocumentType documentType;
        DOMImplementation dOMImplementation = this.impl;
        if (doctype == null) {
            documentType = null;
        } else {
            documentType = doctype.getDocumentType();
        }
        return (DocumentProxy) NodeProxy.getNodeProxy(dOMImplementation.createDocument(namespaceURI, qualifiedName, documentType));
    }

    public DocumentTypeProxy createDocumentType(String qualifiedName, String publicId, String systemId) throws DOMException {
        return (DocumentTypeProxy) NodeProxy.getNodeProxy(this.impl.createDocumentType(qualifiedName, publicId, systemId));
    }

    public boolean hasFeature(String feature, String version) {
        return this.impl.hasFeature(feature, version);
    }

    public String getApiName() {
        return "Ti.XML.DOMImplementation";
    }
}
