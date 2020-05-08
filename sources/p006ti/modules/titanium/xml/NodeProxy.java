package p006ti.modules.titanium.xml;

import android.os.Build.VERSION;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* renamed from: ti.modules.titanium.xml.NodeProxy */
public class NodeProxy extends KrollProxy {
    public static final int ATTRIBUTE_NODE = 2;
    public static final int CDATA_SECTION_NODE = 4;
    public static final int COMMENT_NODE = 8;
    public static final int DOCUMENT_FRAGMENT_NODE = 11;
    public static final int DOCUMENT_NODE = 9;
    public static final int DOCUMENT_TYPE_NODE = 10;
    public static final int ELEMENT_NODE = 1;
    public static final int ENTITY_NODE = 6;
    public static final int ENTITY_REFERENCE_NODE = 5;
    public static final int NOTATION_NODE = 12;
    public static final int PROCESSING_INSTRUCTION_NODE = 7;
    private static final String TAG = "TiNodeProxy";
    public static final int TEXT_NODE = 3;
    protected Node node;

    public NodeProxy(Node node2) {
        this.node = node2;
    }

    public Node getNode() {
        return this.node;
    }

    public static NodeProxy getNodeProxy(Node node2) {
        if (node2 == null) {
            return null;
        }
        switch (node2.getNodeType()) {
            case 1:
                return new ElementProxy((Element) node2);
            case 2:
                return new AttrProxy((Attr) node2);
            case 3:
                return new TextProxy((Text) node2);
            case 4:
                return new CDATASectionProxy((CDATASection) node2);
            case 5:
                return new EntityReferenceProxy((EntityReference) node2);
            case 6:
                return new EntityProxy((Entity) node2);
            case 7:
                return new ProcessingInstructionProxy((ProcessingInstruction) node2);
            case 8:
                return new CommentProxy((Comment) node2);
            case 9:
                return new DocumentProxy((Document) node2);
            case 10:
                return new DocumentTypeProxy((DocumentType) node2);
            case 11:
                return new DocumentFragmentProxy((DocumentFragment) node2);
            case 12:
                return new NotationProxy((Notation) node2);
            default:
                return new NodeProxy(node2);
        }
    }

    public static NodeProxy removeProxyForNode(Node node2) {
        return new NodeProxy(node2);
    }

    /* access modifiers changed from: protected */
    public <T extends NodeProxy> T getProxy(Node node2) {
        return getNodeProxy(node2);
    }

    public NodeProxy appendChild(NodeProxy newChild) throws DOMException {
        return getProxy(this.node.appendChild(newChild.node));
    }

    public NodeProxy cloneNode(boolean deep) {
        if (VERSION.SDK_INT < 11) {
            Log.m44w(TAG, "cloneNode will often throw exception in versions prior to Honeycomb.");
        }
        return getProxy(this.node.cloneNode(deep));
    }

    public NamedNodeMapProxy getAttributes() {
        return new NamedNodeMapProxy(this.node.getAttributes());
    }

    public NodeListProxy getChildNodes() {
        return new NodeListProxy(this.node.getChildNodes());
    }

    public NodeProxy getFirstChild() {
        return getProxy(this.node.getFirstChild());
    }

    public NodeProxy getLastChild() {
        return getProxy(this.node.getLastChild());
    }

    public String getLocalName() {
        return this.node.getLocalName();
    }

    public String getNamespaceURI() {
        return this.node.getNamespaceURI();
    }

    public NodeProxy getNextSibling() {
        return getProxy(this.node.getNextSibling());
    }

    public String getNodeName() {
        return this.node.getNodeName();
    }

    public short getNodeType() {
        return this.node.getNodeType();
    }

    public String getNodeValue() throws DOMException {
        return this.node.getNodeValue();
    }

    public DocumentProxy getOwnerDocument() {
        return new DocumentProxy(this.node.getOwnerDocument());
    }

    public NodeProxy getParentNode() {
        return getProxy(this.node.getParentNode());
    }

    public String getPrefix() {
        return this.node.getPrefix();
    }

    public NodeProxy getPreviousSibling() {
        return getProxy(this.node.getPreviousSibling());
    }

    public boolean hasAttributes() {
        return this.node.hasAttributes();
    }

    public boolean hasChildNodes() {
        return this.node.hasChildNodes();
    }

    public NodeProxy insertBefore(NodeProxy newChild, NodeProxy refChild) throws DOMException {
        return getProxy(this.node.insertBefore(newChild.node, refChild.node));
    }

    public boolean isSupported(String feature, String version) {
        return this.node.isSupported(feature, version);
    }

    public void normalize() {
        this.node.normalize();
    }

    public NodeProxy removeChild(NodeProxy oldChild) throws DOMException {
        return removeProxyForNode(this.node.removeChild(oldChild.node));
    }

    public NodeProxy replaceChild(NodeProxy newChild, NodeProxy oldChild) throws DOMException {
        return removeProxyForNode(this.node.replaceChild(newChild.node, oldChild.node));
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.node.setNodeValue(nodeValue);
    }

    public void setPrefix(String prefix) throws DOMException {
        this.node.setPrefix(prefix);
    }

    public XPathNodeListProxy evaluate(String xpath) {
        return XPathUtil.evaluate(this, xpath);
    }

    public boolean equals(Object o) {
        if (this.node == null || !(o instanceof NodeProxy)) {
            return super.equals(o);
        }
        return this.node.equals(((NodeProxy) o).node);
    }

    public int hashCode() {
        if (this.node == null) {
            return super.hashCode();
        }
        return this.node.hashCode();
    }

    public String getApiName() {
        return "Ti.XML.Node";
    }
}
