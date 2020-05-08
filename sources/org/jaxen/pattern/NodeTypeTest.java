package org.jaxen.pattern;

import org.appcelerator.titanium.util.TiUrl;
import org.jaxen.Context;

public class NodeTypeTest extends NodeTest {
    public static final NodeTypeTest ATTRIBUTE_TEST = new NodeTypeTest(2);
    public static final NodeTypeTest COMMENT_TEST = new NodeTypeTest(8);
    public static final NodeTypeTest DOCUMENT_TEST = new NodeTypeTest(9);
    public static final NodeTypeTest ELEMENT_TEST = new NodeTypeTest(1);
    public static final NodeTypeTest NAMESPACE_TEST = new NodeTypeTest(13);
    public static final NodeTypeTest PROCESSING_INSTRUCTION_TEST = new NodeTypeTest(7);
    public static final NodeTypeTest TEXT_TEST = new NodeTypeTest(3);
    private short nodeType;

    public NodeTypeTest(short nodeType2) {
        this.nodeType = nodeType2;
    }

    public boolean matches(Object node, Context context) {
        return this.nodeType == context.getNavigator().getNodeType(node);
    }

    public double getPriority() {
        return -0.5d;
    }

    public short getMatchType() {
        return this.nodeType;
    }

    public String getText() {
        switch (this.nodeType) {
            case 1:
                return "child()";
            case 2:
                return "@*";
            case 3:
                return "text()";
            case 7:
                return "processing-instruction()";
            case 8:
                return "comment()";
            case 9:
                return TiUrl.PATH_SEPARATOR;
            case 13:
                return "namespace()";
            default:
                return "";
        }
    }

    public String toString() {
        return super.toString() + "[ type: " + this.nodeType + " ]";
    }
}
