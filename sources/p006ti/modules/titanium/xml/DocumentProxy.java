package p006ti.modules.titanium.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

/* renamed from: ti.modules.titanium.xml.DocumentProxy */
public class DocumentProxy extends NodeProxy {
    private Document doc;

    public DocumentProxy(Document doc2) {
        super(doc2);
        this.doc = doc2;
    }

    public AttrProxy createAttribute(String name) throws DOMException {
        Attr attr = this.doc.createAttribute(name);
        if (attr.getValue() == null) {
            attr.setValue("");
        }
        return (AttrProxy) getProxy(attr);
    }

    public AttrProxy createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        Attr attr = this.doc.createAttributeNS(namespaceURI, qualifiedName);
        if (attr.getValue() == null) {
            attr.setValue("");
        }
        return (AttrProxy) getProxy(attr);
    }

    public CDATASectionProxy createCDATASection(String data) throws DOMException {
        return (CDATASectionProxy) getProxy(this.doc.createCDATASection(data));
    }

    public CommentProxy createComment(String data) {
        return (CommentProxy) getProxy(this.doc.createComment(data));
    }

    public DocumentFragmentProxy createDocumentFragment() {
        return (DocumentFragmentProxy) getProxy(this.doc.createDocumentFragment());
    }

    public ElementProxy createElement(String tagName) throws DOMException {
        return (ElementProxy) getProxy(this.doc.createElement(tagName));
    }

    public ElementProxy createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return (ElementProxy) getProxy(this.doc.createElementNS(namespaceURI, qualifiedName));
    }

    public EntityReferenceProxy createEntityReference(String name) throws DOMException {
        return (EntityReferenceProxy) getProxy(this.doc.createEntityReference(name));
    }

    public ProcessingInstructionProxy createProcessingInstruction(String target, String data) throws DOMException {
        return (ProcessingInstructionProxy) getProxy(this.doc.createProcessingInstruction(target, data));
    }

    public TextProxy createTextNode(String data) {
        return (TextProxy) getProxy(this.doc.createTextNode(data));
    }

    public DocumentTypeProxy getDoctype() {
        return (DocumentTypeProxy) getProxy(this.doc.getDoctype());
    }

    public ElementProxy getDocumentElement() {
        return (ElementProxy) getProxy(this.doc.getDocumentElement());
    }

    public ElementProxy getElementById(String elementId) {
        return (ElementProxy) getProxy(this.doc.getElementById(elementId));
    }

    public NodeListProxy getElementsByTagName(String tagname) {
        return new NodeListProxy(this.doc.getElementsByTagName(tagname));
    }

    public NodeListProxy getElementsByTagNameNS(String namespaceURI, String localName) {
        return new NodeListProxy(this.doc.getElementsByTagNameNS(namespaceURI, localName));
    }

    public DOMImplementationProxy getImplementation() {
        return new DOMImplementationProxy(this.doc.getImplementation());
    }

    public NodeProxy importNode(NodeProxy importedNode, boolean deep) throws DOMException {
        return getProxy(this.doc.importNode(importedNode.getNode(), deep));
    }

    public DocumentProxy getOwnerDocument() {
        return this;
    }

    public String getApiName() {
        return "Ti.XML.Document";
    }
}
