package org.jaxen.saxpath.helpers;

import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;

public class XPathReaderFactory {
    protected static final String DEFAULT_DRIVER = "org.jaxen.saxpath.base.XPathReader";
    public static final String DRIVER_PROPERTY = "org.saxpath.driver";

    private XPathReaderFactory() {
    }

    public static XPathReader createReader() throws SAXPathException {
        String className = null;
        try {
            className = System.getProperty(DRIVER_PROPERTY);
        } catch (SecurityException e) {
        }
        if (className == null || className.length() == 0) {
            className = DEFAULT_DRIVER;
        }
        return createReader(className);
    }

    public static XPathReader createReader(String className) throws SAXPathException {
        try {
            Class readerClass = Class.forName(className, true, XPathReaderFactory.class.getClassLoader());
            if (!XPathReader.class.isAssignableFrom(readerClass)) {
                throw new SAXPathException("Class [" + className + "] does not implement the org.jaxen.saxpath.XPathReader interface.");
            }
            try {
                return (XPathReader) readerClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new SAXPathException((Throwable) e);
            } catch (InstantiationException e2) {
                throw new SAXPathException((Throwable) e2);
            }
        } catch (ClassNotFoundException e3) {
            throw new SAXPathException((Throwable) e3);
        }
    }
}
