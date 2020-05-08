package p006ti.modules.titanium.xml;

import org.appcelerator.kroll.KrollProxy;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

/* renamed from: ti.modules.titanium.xml.NamedNodeMapProxy */
public class NamedNodeMapProxy extends KrollProxy {
    private NamedNodeMap map;

    public NamedNodeMapProxy(NamedNodeMap map2) {
        this.map = map2;
    }

    public int getLength() {
        return this.map.getLength();
    }

    public NodeProxy getNamedItem(String name) {
        return NodeProxy.getNodeProxy(this.map.getNamedItem(name));
    }

    public NodeProxy getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return NodeProxy.getNodeProxy(this.map.getNamedItemNS(namespaceURI, localName));
    }

    public NodeProxy item(int index) {
        if (index >= getLength()) {
            return null;
        }
        return NodeProxy.getNodeProxy(this.map.item(index));
    }

    public NodeProxy removeNamedItem(String name) throws DOMException {
        return NodeProxy.getNodeProxy(this.map.removeNamedItem(name));
    }

    public NodeProxy removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return NodeProxy.getNodeProxy(this.map.removeNamedItemNS(namespaceURI, localName));
    }

    public NodeProxy setNamedItem(NodeProxy arg) throws DOMException {
        return NodeProxy.getNodeProxy(this.map.setNamedItem(arg.getNode()));
    }

    public NodeProxy setNamedItemNS(NodeProxy arg) throws DOMException {
        return NodeProxy.getNodeProxy(this.map.setNamedItemNS(arg.getNode()));
    }

    public String getApiName() {
        return "Ti.XML.NamedNodeMap";
    }
}
