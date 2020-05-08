package org.jaxen;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JaxenRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -930309761511911193L;
    private Throwable cause;
    private boolean causeSet = false;

    public JaxenRuntimeException(Throwable cause2) {
        super(cause2.getMessage());
        initCause(cause2);
    }

    public JaxenRuntimeException(String message) {
        super(message);
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
        if (JaxenException.javaVersion < 1.4d && getCause() != null) {
            s.print("Caused by: ");
            getCause().printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (JaxenException.javaVersion < 1.4d && getCause() != null) {
            s.print("Caused by: ");
            getCause().printStackTrace(s);
        }
    }
}
