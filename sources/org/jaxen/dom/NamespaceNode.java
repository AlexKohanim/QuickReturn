package org.jaxen.dom;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class NamespaceNode implements Node {
    public static final short NAMESPACE_NODE = 13;
    private String name;
    private Node parent;
    private HashMap userData = new HashMap();
    private String value;

    private static class EmptyNodeList implements NodeList {
        private EmptyNodeList() {
        }

        public int getLength() {
            return 0;
        }

        public Node item(int index) {
            return null;
        }
    }

    public NamespaceNode(Node parent2, String name2, String value2) {
        this.parent = parent2;
        this.name = name2;
        this.value = value2;
    }

    NamespaceNode(Node parent2, Node attribute) {
        String attributeName = attribute.getNodeName();
        if (attributeName.equals("xmlns")) {
            this.name = "";
        } else if (attributeName.startsWith("xmlns:")) {
            this.name = attributeName.substring(6);
        } else {
            this.name = attributeName;
        }
        this.parent = parent2;
        this.value = attribute.getNodeValue();
    }

    public String getNodeName() {
        return this.name;
    }

    public String getNodeValue() {
        return this.value;
    }

    public void setNodeValue(String value2) throws DOMException {
        disallowModification();
    }

    public short getNodeType() {
        return 13;
    }

    public Node getParentNode() {
        return this.parent;
    }

    public NodeList getChildNodes() {
        return new EmptyNodeList();
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public Document getOwnerDocument() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getOwnerDocument();
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        disallowModification();
        return null;
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        disallowModification();
        return null;
    }

    public Node removeChild(Node oldChild) throws DOMException {
        disallowModification();
        return null;
    }

    public Node appendChild(Node newChild) throws DOMException {
        disallowModification();
        return null;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node cloneNode(boolean deep) {
        return new NamespaceNode(this.parent, this.name, this.value);
    }

    public void normalize() {
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public void setPrefix(String prefix) throws DOMException {
        disallowModification();
    }

    public String getLocalName() {
        return this.name;
    }

    public boolean hasAttributes() {
        return false;
    }

    private void disallowModification() throws DOMException {
        throw new DOMException(7, "Namespace node may not be modified");
    }

    public int hashCode() {
        return hashCode(this.parent) + hashCode(this.name) + hashCode(this.value);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof NamespaceNode)) {
            return false;
        }
        NamespaceNode ns = (NamespaceNode) o;
        if (!equals(this.parent, ns.getParentNode()) || !equals(this.name, ns.getNodeName()) || !equals(this.value, ns.getNodeValue())) {
            return false;
        }
        return true;
    }

    private int hashCode(Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }

    private boolean equals(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    public String getBaseURI() {
        try {
            Class[] args = new Class[0];
            return (String) Node.class.getMethod("getBaseURI", args).invoke(getParentNode(), args);
        } catch (Exception e) {
            return null;
        }
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        throw new DOMException(9, "DOM level 3 interfaces are not fully implemented in Jaxen's NamespaceNode class");
    }

    public String getTextContent() {
        return this.value;
    }

    public void setTextContent(String textContent) throws DOMException {
        disallowModification();
    }

    public boolean isSameNode(Node other) {
        boolean b;
        boolean a = isEqualNode(other);
        Node thisParent = getParentNode();
        Node thatParent = other.getParentNode();
        Class clazz = Node.class;
        try {
            b = ((Boolean) clazz.getMethod("isEqual", new Class[]{clazz}).invoke(thisParent, new Object[]{thatParent})).booleanValue();
        } catch (NoSuchMethodException e) {
            b = thisParent.equals(thatParent);
        } catch (InvocationTargetException e2) {
            b = thisParent.equals(thatParent);
        } catch (IllegalAccessException e3) {
            b = thisParent.equals(thatParent);
        }
        if (!a || !b) {
            return false;
        }
        return true;
    }

    public String lookupPrefix(String namespaceURI) {
        try {
            return (String) Node.class.getMethod("lookupPrefix", new Class[]{String.class}).invoke(this.parent, new String[]{namespaceURI});
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Cannot lookup prefixes in DOM 2");
        } catch (InvocationTargetException e2) {
            throw new UnsupportedOperationException("Cannot lookup prefixes in DOM 2");
        } catch (IllegalAccessException e3) {
            throw new UnsupportedOperationException("Cannot lookup prefixes in DOM 2");
        }
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return namespaceURI.equals(lookupNamespaceURI(null));
    }

    public String lookupNamespaceURI(String prefix) {
        try {
            return (String) Node.class.getMethod("lookupNamespaceURI", new Class[]{String.class}).invoke(this.parent, new String[]{prefix});
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Cannot lookup namespace URIs in DOM 2");
        } catch (InvocationTargetException e2) {
            throw new UnsupportedOperationException("Cannot lookup namespace URIs in DOM 2");
        } catch (IllegalAccessException e3) {
            throw new UnsupportedOperationException("Cannot lookup namespace URIs in DOM 2");
        }
    }

    public boolean isEqualNode(Node arg) {
        if (arg.getNodeType() != getNodeType()) {
            return false;
        }
        NamespaceNode other = (NamespaceNode) arg;
        if (other.name == null && this.name != null) {
            return false;
        }
        if (other.name != null && this.name == null) {
            return false;
        }
        if (other.value == null && this.value != null) {
            return false;
        }
        if (other.value != null && this.value == null) {
            return false;
        }
        if (other.name == null && this.name == null) {
            return other.value.equals(this.value);
        }
        if (!other.name.equals(this.name) || !other.value.equals(this.value)) {
            return false;
        }
        return true;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        Object oldValue = getUserData(key);
        this.userData.put(key, data);
        return oldValue;
    }

    public Object getUserData(String key) {
        return this.userData.get(key);
    }
}
