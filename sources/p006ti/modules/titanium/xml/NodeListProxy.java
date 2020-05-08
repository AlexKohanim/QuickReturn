package p006ti.modules.titanium.xml;

import org.appcelerator.kroll.KrollProxy;
import org.w3c.dom.NodeList;

/* renamed from: ti.modules.titanium.xml.NodeListProxy */
public class NodeListProxy extends KrollProxy {
    private NodeList list;
    private int offset;

    public NodeListProxy(NodeList list2) {
        this(list2, 0);
    }

    public NodeListProxy(NodeList list2, int offset2) {
        this.list = list2;
        this.offset = offset2;
    }

    public int getLength() {
        return this.list.getLength() - this.offset;
    }

    public NodeProxy item(int index) {
        return NodeProxy.getNodeProxy(this.list.item(this.offset + index));
    }

    public String getApiName() {
        return "Ti.XML.NodeList";
    }
}
