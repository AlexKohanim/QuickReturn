package org.jaxen.dom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class DocumentNavigator extends DefaultNavigator {
    private static final DocumentNavigator SINGLETON = new DocumentNavigator();
    private static final long serialVersionUID = 8460943068889528115L;

    private static class AttributeIterator implements Iterator {
        private int lastAttribute = -1;
        private NamedNodeMap map;
        private int pos;

        AttributeIterator(Node parent) {
            this.map = parent.getAttributes();
            this.pos = 0;
            for (int i = this.map.getLength() - 1; i >= 0; i--) {
                if (!"http://www.w3.org/2000/xmlns/".equals(this.map.item(i).getNamespaceURI())) {
                    this.lastAttribute = i;
                    return;
                }
            }
        }

        public boolean hasNext() {
            return this.pos <= this.lastAttribute;
        }

        public Object next() {
            NamedNodeMap namedNodeMap = this.map;
            int i = this.pos;
            this.pos = i + 1;
            Node attr = namedNodeMap.item(i);
            if (attr == null) {
                throw new NoSuchElementException();
            } else if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
                return next();
            } else {
                return attr;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    abstract class NodeIterator implements Iterator {
        private Node node;

        /* access modifiers changed from: protected */
        public abstract Node getFirstNode(Node node2);

        /* access modifiers changed from: protected */
        public abstract Node getNextNode(Node node2);

        public NodeIterator(Node contextNode) {
            this.node = getFirstNode(contextNode);
            while (!isXPathNode(this.node)) {
                this.node = getNextNode(this.node);
            }
        }

        public boolean hasNext() {
            return this.node != null;
        }

        public Object next() {
            if (this.node == null) {
                throw new NoSuchElementException();
            }
            Node ret = this.node;
            this.node = getNextNode(this.node);
            while (!isXPathNode(this.node)) {
                this.node = getNextNode(this.node);
            }
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private boolean isXPathNode(Node node2) {
            if (node2 == null) {
                return true;
            }
            switch (node2.getNodeType()) {
                case 5:
                case 6:
                case 10:
                case 11:
                case 12:
                    return false;
                default:
                    return true;
            }
        }
    }

    public static Navigator getInstance() {
        return SINGLETON;
    }

    public Iterator getChildAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node node) {
                return node.getFirstChild();
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node node) {
                try {
                    return node.getNextSibling();
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }
        };
    }

    public Iterator getParentAxisIterator(Object contextNode) {
        Node node = (Node) contextNode;
        return node.getNodeType() == 2 ? new NodeIterator(node) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node n) {
                return ((Attr) n).getOwnerElement();
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node n) {
                return null;
            }
        } : new NodeIterator(node) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node n) {
                return n.getParentNode();
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node n) {
                return null;
            }
        };
    }

    public Object getParentNode(Object child) {
        Node node = (Node) child;
        if (node.getNodeType() == 2) {
            return ((Attr) node).getOwnerElement();
        }
        return node.getParentNode();
    }

    public Iterator getFollowingSiblingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node node) {
                return getNextNode(node);
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node node) {
                return node.getNextSibling();
            }
        };
    }

    public Iterator getPrecedingSiblingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node node) {
                return getNextNode(node);
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node node) {
                return node.getPreviousSibling();
            }
        };
    }

    public Iterator getFollowingAxisIterator(Object contextNode) {
        return new NodeIterator((Node) contextNode) {
            /* access modifiers changed from: protected */
            public Node getFirstNode(Node node) {
                if (node == null) {
                    return null;
                }
                Node sibling = node.getNextSibling();
                if (sibling == null) {
                    return getFirstNode(node.getParentNode());
                }
                return sibling;
            }

            /* access modifiers changed from: protected */
            public Node getNextNode(Node node) {
                if (node == null) {
                    return null;
                }
                Node n = node.getFirstChild();
                if (n == null) {
                    n = node.getNextSibling();
                }
                if (n == null) {
                    return getFirstNode(node.getParentNode());
                }
                return n;
            }
        };
    }

    public Iterator getAttributeAxisIterator(Object contextNode) {
        if (isElement(contextNode)) {
            return new AttributeIterator((Node) contextNode);
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getNamespaceAxisIterator(Object contextNode) {
        if (!isElement(contextNode)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        HashMap nsMap = new HashMap();
        for (Node n = (Node) contextNode; n != null; n = n.getParentNode()) {
            String myNamespace = n.getNamespaceURI();
            if (myNamespace != null && !"".equals(myNamespace)) {
                String myPrefix = n.getPrefix();
                if (!nsMap.containsKey(myPrefix)) {
                    nsMap.put(myPrefix, new NamespaceNode((Node) contextNode, myPrefix, myNamespace));
                }
            }
            if (n.hasAttributes()) {
                NamedNodeMap atts = n.getAttributes();
                int length = atts.getLength();
                for (int i = 0; i < length; i++) {
                    Attr att = (Attr) atts.item(i);
                    String attributeNamespace = att.getNamespaceURI();
                    if (!"http://www.w3.org/2000/xmlns/".equals(attributeNamespace) && attributeNamespace != null) {
                        String prefix = att.getPrefix();
                        NamespaceNode ns = new NamespaceNode((Node) contextNode, prefix, attributeNamespace);
                        if (!nsMap.containsKey(prefix)) {
                            nsMap.put(prefix, ns);
                        }
                    }
                }
                for (int i2 = 0; i2 < length; i2++) {
                    Attr att2 = (Attr) atts.item(i2);
                    if ("http://www.w3.org/2000/xmlns/".equals(att2.getNamespaceURI())) {
                        NamespaceNode ns2 = new NamespaceNode((Node) contextNode, att2);
                        String name = ns2.getNodeName();
                        if (!nsMap.containsKey(name)) {
                            nsMap.put(name, ns2);
                        }
                    }
                }
            }
        }
        NamespaceNode namespaceNode = new NamespaceNode((Node) contextNode, "xml", "http://www.w3.org/XML/1998/namespace");
        nsMap.put("xml", namespaceNode);
        NamespaceNode defaultNS = (NamespaceNode) nsMap.get("");
        if (defaultNS != null && defaultNS.getNodeValue().length() == 0) {
            nsMap.remove("");
        }
        return nsMap.values().iterator();
    }

    public XPath parseXPath(String xpath) throws SAXPathException {
        return new DOMXPath(xpath);
    }

    public Object getDocumentNode(Object contextNode) {
        return isDocument(contextNode) ? contextNode : ((Node) contextNode).getOwnerDocument();
    }

    public String getElementNamespaceUri(Object element) {
        try {
            Node node = (Node) element;
            if (node.getNodeType() == 1) {
                return node.getNamespaceURI();
            }
        } catch (ClassCastException e) {
        }
        return null;
    }

    public String getElementName(Object element) {
        if (!isElement(element)) {
            return null;
        }
        String name = ((Node) element).getLocalName();
        if (name == null) {
            return ((Node) element).getNodeName();
        }
        return name;
    }

    public String getElementQName(Object element) {
        try {
            Node node = (Node) element;
            if (node.getNodeType() == 1) {
                return node.getNodeName();
            }
        } catch (ClassCastException e) {
        }
        return null;
    }

    public String getAttributeNamespaceUri(Object attribute) {
        try {
            Node node = (Node) attribute;
            if (node.getNodeType() == 2) {
                return node.getNamespaceURI();
            }
        } catch (ClassCastException e) {
        }
        return null;
    }

    public String getAttributeName(Object attribute) {
        if (!isAttribute(attribute)) {
            return null;
        }
        String name = ((Node) attribute).getLocalName();
        if (name == null) {
            return ((Node) attribute).getNodeName();
        }
        return name;
    }

    public String getAttributeQName(Object attribute) {
        try {
            Node node = (Node) attribute;
            if (node.getNodeType() == 2) {
                return node.getNodeName();
            }
        } catch (ClassCastException e) {
        }
        return null;
    }

    public boolean isDocument(Object object) {
        return (object instanceof Node) && ((Node) object).getNodeType() == 9;
    }

    public boolean isNamespace(Object object) {
        return object instanceof NamespaceNode;
    }

    public boolean isElement(Object object) {
        return (object instanceof Node) && ((Node) object).getNodeType() == 1;
    }

    public boolean isAttribute(Object object) {
        return (object instanceof Node) && ((Node) object).getNodeType() == 2 && !"http://www.w3.org/2000/xmlns/".equals(((Node) object).getNamespaceURI());
    }

    public boolean isComment(Object object) {
        return (object instanceof Node) && ((Node) object).getNodeType() == 8;
    }

    public boolean isText(Object object) {
        if (!(object instanceof Node)) {
            return false;
        }
        switch (((Node) object).getNodeType()) {
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    public boolean isProcessingInstruction(Object object) {
        return (object instanceof Node) && ((Node) object).getNodeType() == 7;
    }

    public String getElementStringValue(Object object) {
        if (isElement(object)) {
            return getStringValue((Node) object, new StringBuffer()).toString();
        }
        return null;
    }

    private StringBuffer getStringValue(Node node, StringBuffer buffer) {
        if (isText(node)) {
            buffer.append(node.getNodeValue());
        } else {
            NodeList children = node.getChildNodes();
            int length = children.getLength();
            for (int i = 0; i < length; i++) {
                getStringValue(children.item(i), buffer);
            }
        }
        return buffer;
    }

    public String getAttributeStringValue(Object object) {
        if (isAttribute(object)) {
            return ((Node) object).getNodeValue();
        }
        return null;
    }

    public String getTextStringValue(Object object) {
        if (isText(object)) {
            return ((Node) object).getNodeValue();
        }
        return null;
    }

    public String getCommentStringValue(Object object) {
        if (isComment(object)) {
            return ((Node) object).getNodeValue();
        }
        return null;
    }

    public String getNamespaceStringValue(Object object) {
        if (isNamespace(object)) {
            return ((NamespaceNode) object).getNodeValue();
        }
        return null;
    }

    public String getNamespacePrefix(Object object) {
        if (isNamespace(object)) {
            return ((NamespaceNode) object).getLocalName();
        }
        return null;
    }

    public String translateNamespacePrefixToUri(String prefix, Object element) {
        Iterator it = getNamespaceAxisIterator(element);
        while (it.hasNext()) {
            NamespaceNode ns = (NamespaceNode) it.next();
            if (prefix.equals(ns.getNodeName())) {
                return ns.getNodeValue();
            }
        }
        return null;
    }

    public Object getDocument(String uri) throws FunctionCallException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().parse(uri);
        } catch (ParserConfigurationException e) {
            throw new FunctionCallException("JAXP setup error in document() function: " + e.getMessage(), e);
        } catch (SAXException e2) {
            throw new FunctionCallException("XML error in document() function: " + e2.getMessage(), e2);
        } catch (IOException e3) {
            throw new FunctionCallException("I/O error in document() function: " + e3.getMessage(), e3);
        }
    }

    public String getProcessingInstructionTarget(Object obj) {
        if (isProcessingInstruction(obj)) {
            return ((ProcessingInstruction) obj).getTarget();
        }
        throw new ClassCastException(obj + " is not a processing instruction");
    }

    public String getProcessingInstructionData(Object obj) {
        if (isProcessingInstruction(obj)) {
            return ((ProcessingInstruction) obj).getData();
        }
        throw new ClassCastException(obj + " is not a processing instruction");
    }

    public Object getElementById(Object object, String elementId) {
        Document doc = (Document) getDocumentNode(object);
        if (doc != null) {
            return doc.getElementById(elementId);
        }
        return null;
    }
}
