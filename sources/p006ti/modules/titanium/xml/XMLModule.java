package p006ti.modules.titanium.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.xml.sax.SAXException;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: ti.modules.titanium.xml.XMLModule */
public class XMLModule extends KrollModule {
    private static final String TAG = "XMLModule";
    private static DocumentBuilder builder;
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    static {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Log.m34e(TAG, "Error finding DOM implementation", (Throwable) e);
        }
    }

    public DocumentProxy parseString(String xml) throws SAXException, IOException {
        return parse(xml);
    }

    public static DocumentProxy parse(String xml) throws SAXException, IOException {
        return parse(xml, System.getProperty("file.encoding", HttpUrlConnectionUtils.UTF_8));
    }

    public static DocumentProxy parse(String xml, String encoding) throws SAXException, IOException {
        if (builder == null) {
            return null;
        }
        try {
            return new DocumentProxy(builder.parse(new ByteArrayInputStream(xml.getBytes(encoding))));
        } catch (SAXException e) {
            Log.m34e(TAG, "Error parsing XML", (Throwable) e);
            throw e;
        } catch (IOException e2) {
            Log.m34e(TAG, "Error reading XML", (Throwable) e2);
            throw e2;
        }
    }

    public String serializeToString(NodeProxy node) throws TransformerConfigurationException, TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node.getNode()), new StreamResult(writer));
        return writer.toString();
    }

    public String getApiName() {
        return "Ti.XML";
    }
}
