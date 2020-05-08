package p006ti.modules.titanium.xml;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* renamed from: ti.modules.titanium.xml.TextProxy */
public class TextProxy extends CharacterDataProxy {
    private static final String TAG = "Text";
    private Text text;

    public TextProxy(Text text2) {
        super(text2);
        this.text = text2;
    }

    public TextProxy splitText(int offset) throws DOMException {
        String leftValueShouldBe;
        String originalValue = this.text.getNodeValue();
        Text splitResultNode = this.text.splitText(offset);
        if (offset == 0) {
            leftValueShouldBe = "";
        } else {
            leftValueShouldBe = originalValue.substring(0, offset);
        }
        String newValue = this.text.getNodeValue();
        if (newValue == null || !newValue.equals(leftValueShouldBe)) {
            this.text.setData(leftValueShouldBe);
        }
        Text returnNode = splitResultNode;
        if (splitResultNode == this.text) {
            Node sibling = this.text.getNextSibling();
            if (sibling != null && (sibling instanceof Text)) {
                returnNode = (Text) sibling;
            }
        }
        return (TextProxy) getProxy(returnNode);
    }

    public String getTextContent() {
        return this.text.getNodeValue();
    }

    public String getApiName() {
        return "Ti.XML.Text";
    }
}
