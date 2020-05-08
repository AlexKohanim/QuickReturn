package org.jaxen;

import java.io.Serializable;

class QualifiedName implements Serializable {
    private static final long serialVersionUID = 2734958615642751535L;
    private String localName;
    private String namespaceURI;

    QualifiedName(String namespaceURI2, String localName2) {
        if (namespaceURI2 == null) {
            namespaceURI2 = "";
        }
        this.namespaceURI = namespaceURI2;
        this.localName = localName2;
    }

    public int hashCode() {
        return this.localName.hashCode() ^ this.namespaceURI.hashCode();
    }

    public boolean equals(Object o) {
        QualifiedName other = (QualifiedName) o;
        return this.namespaceURI.equals(other.namespaceURI) && other.localName.equals(this.localName);
    }

    /* access modifiers changed from: 0000 */
    public String getClarkForm() {
        if ("".equals(this.namespaceURI)) {
            return this.localName;
        }
        return "{" + this.namespaceURI + "}" + ":" + this.localName;
    }
}
