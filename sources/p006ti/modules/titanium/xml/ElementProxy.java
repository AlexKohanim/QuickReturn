package p006ti.modules.titanium.xml;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* renamed from: ti.modules.titanium.xml.ElementProxy */
public class ElementProxy extends NodeProxy {
    private static final String TAG = "Element";
    private Element element;

    public ElementProxy(Element element2) {
        super(element2);
        this.element = element2;
    }

    public String getTextContent() {
        StringBuilder sb = new StringBuilder();
        getTextImpl(this.element, sb);
        return sb.toString();
    }

    private void getTextImpl(Node node, StringBuilder builder) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case 1:
                case 6:
                    getTextImpl(child, builder);
                    break;
                case 3:
                    builder.append(((Text) child).getNodeValue());
                    break;
                case 4:
                    builder.append(((CDATASection) child).getData());
                    break;
            }
        }
    }

    public String getAttribute(String name) {
        return this.element.getAttribute(name);
    }

    public AttrProxy getAttributeNode(String name) {
        return (AttrProxy) getProxy(this.element.getAttributeNode(name));
    }

    public AttrProxy getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return (AttrProxy) getProxy(this.element.getAttributeNodeNS(namespaceURI, localName));
    }

    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return this.element.getAttributeNS(namespaceURI, localName);
    }

    /* access modifiers changed from: protected */
    public NodeListProxy filterThisFromNodeList(NodeList list) {
        int offset = 0;
        if (list.getLength() > 0 && list.item(0).equals(this.element)) {
            offset = 1;
        }
        return new NodeListProxy(list, offset);
    }

    public NodeListProxy getElementsByTagName(String name) {
        return filterThisFromNodeList(this.element.getElementsByTagName(name));
    }

    public NodeListProxy getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return filterThisFromNodeList(this.element.getElementsByTagNameNS(namespaceURI, localName));
    }

    public String getTagName() {
        return this.element.getTagName();
    }

    public boolean hasAttribute(String name) {
        return this.element.hasAttribute(name);
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return this.element.hasAttributeNS(namespaceURI, localName);
    }

    public void removeAttribute(String name) throws DOMException {
        this.element.removeAttribute(name);
    }

    public AttrProxy removeAttributeNode(AttrProxy oldAttr) throws DOMException {
        return (AttrProxy) getProxy(this.element.removeAttributeNode(oldAttr.getAttr()));
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        this.element.removeAttributeNS(namespaceURI, localName);
    }

    public void setAttribute(String name, String value) throws DOMException {
        this.element.setAttribute(name, value);
    }

    public AttrProxy setAttributeNode(AttrProxy newAttr) throws DOMException {
        AttrProxy existedAttr = getAttributeNode(newAttr.getNodeName());
        if (existedAttr != null && existedAttr.getAttr() == newAttr.getAttr()) {
            return null;
        }
        if (existedAttr != null) {
            removeAttributeNode(existedAttr);
        }
        try {
            this.element.setAttributeNode(newAttr.getAttr());
            return existedAttr;
        } catch (DOMException e) {
            if (existedAttr != null) {
                this.element.setAttributeNode(existedAttr.getAttr());
            }
            throw e;
        }
    }

    public AttrProxy setAttributeNodeNS(AttrProxy newAttr) throws DOMException {
        AttrProxy existedAttr = getAttributeNodeNS(newAttr.getNamespaceURI(), newAttr.getLocalName());
        if (existedAttr != null && existedAttr.getAttr() == newAttr.getAttr()) {
            return null;
        }
        if (existedAttr != null) {
            removeAttributeNode(existedAttr);
        }
        try {
            this.element.setAttributeNodeNS(newAttr.getAttr());
            return existedAttr;
        } catch (DOMException e) {
            if (existedAttr != null) {
                this.element.setAttributeNodeNS(existedAttr.getAttr());
            }
            throw e;
        }
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        this.element.setAttributeNS(namespaceURI, qualifiedName, value);
    }

    public String getApiName() {
        return "Ti.XML.Element";
    }
}
