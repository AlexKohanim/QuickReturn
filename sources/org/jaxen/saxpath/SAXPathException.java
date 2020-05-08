package org.jaxen.saxpath;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SAXPathException extends Exception {
    private static double javaVersion = 0.0d;
    private static final long serialVersionUID = 4826444568928720706L;
    private Throwable cause;
    private boolean causeSet = false;

    static {
        javaVersion = 1.4d;
        try {
            javaVersion = Double.valueOf(System.getProperty("java.version").substring(0, 3)).doubleValue();
        } catch (Exception e) {
        }
    }

    public SAXPathException(String message) {
        super(message);
    }

    public SAXPathException(Throwable cause2) {
        super(cause2.getMessage());
        initCause(cause2);
    }

    public SAXPathException(String message, Throwable cause2) {
        super(message);
        initCause(cause2);
    }

    public Throwable getCause() {
        return this.cause;
    }

    public Throwable initCause(Throwable cause2) {
        if (this.causeSet) {
            throw new IllegalStateException("Cause cannot be reset");
        } else if (cause2 == this) {
            throw new IllegalArgumentException("Exception cannot be its own cause");
        } else {
            this.causeSet = true;
            this.cause = cause2;
            return this;
        }
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (javaVersion < 1.4d && getCause() != null) {
            s.print("Caused by: ");
            getCause().printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (javaVersion < 1.4d && getCause() != null) {
            s.print("Caused by: ");
            getCause().printStackTrace(s);
        }
    }
}
