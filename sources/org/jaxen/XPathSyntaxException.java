package org.jaxen;

public class XPathSyntaxException extends JaxenException {
    private static final long serialVersionUID = 1980601567207604059L;
    private int position;
    private String xpath;

    public XPathSyntaxException(org.jaxen.saxpath.XPathSyntaxException e) {
        super((Throwable) e);
        this.xpath = e.getXPath();
        this.position = e.getPosition();
    }

    public XPathSyntaxException(String xpath2, int position2, String message) {
        super(message);
        this.xpath = xpath2;
        this.position = position2;
    }

    public int getPosition() {
        return this.position;
    }

    public String getXPath() {
        return this.xpath;
    }

    public String getPositionMarker() {
        StringBuffer buf = new StringBuffer();
        int pos = getPosition();
        for (int i = 0; i < pos; i++) {
            buf.append(" ");
        }
        buf.append("^");
        return buf.toString();
    }

    public String getMultilineMessage() {
        StringBuffer buf = new StringBuffer(getMessage());
        buf.append("\n");
        buf.append(getXPath());
        buf.append("\n");
        buf.append(getPositionMarker());
        return buf.toString();
    }
}
