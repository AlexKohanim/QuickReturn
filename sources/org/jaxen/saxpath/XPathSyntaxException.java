package org.jaxen.saxpath;

public class XPathSyntaxException extends SAXPathException {
    private static final String lineSeparator = System.getProperty("line.separator");
    private static final long serialVersionUID = 3567675610742422397L;
    private int position;
    private String xpath;

    public XPathSyntaxException(String xpath2, int position2, String message) {
        super(message);
        this.position = position2;
        this.xpath = xpath2;
    }

    public int getPosition() {
        return this.position;
    }

    public String getXPath() {
        return this.xpath;
    }

    public String toString() {
        return getClass() + ": " + getXPath() + ": " + getPosition() + ": " + getMessage();
    }

    private String getPositionMarker() {
        int pos = getPosition();
        StringBuffer buf = new StringBuffer(pos + 1);
        for (int i = 0; i < pos; i++) {
            buf.append(" ");
        }
        buf.append("^");
        return buf.toString();
    }

    public String getMultilineMessage() {
        StringBuffer buf = new StringBuffer();
        buf.append(getMessage());
        buf.append(lineSeparator);
        buf.append(getXPath());
        buf.append(lineSeparator);
        buf.append(getPositionMarker());
        return buf.toString();
    }
}
