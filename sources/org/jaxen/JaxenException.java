package org.jaxen;

import org.jaxen.saxpath.SAXPathException;

public class JaxenException extends SAXPathException {
    static double javaVersion = 0.0d;
    private static final long serialVersionUID = 7132891439526672639L;

    static {
        javaVersion = 1.4d;
        try {
            javaVersion = Double.valueOf(System.getProperty("java.version").substring(0, 3)).doubleValue();
        } catch (RuntimeException e) {
        }
    }

    public JaxenException(String message) {
        super(message);
    }

    public JaxenException(Throwable rootCause) {
        super(rootCause);
    }

    public JaxenException(String message, Throwable nestedException) {
        super(message, nestedException);
    }
}
