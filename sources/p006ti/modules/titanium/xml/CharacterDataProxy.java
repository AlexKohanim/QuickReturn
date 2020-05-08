package p006ti.modules.titanium.xml;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;

/* renamed from: ti.modules.titanium.xml.CharacterDataProxy */
public class CharacterDataProxy extends NodeProxy {
    private CharacterData data;

    public CharacterDataProxy(CharacterData data2) {
        super(data2);
        this.data = data2;
    }

    public void appendData(String arg) throws DOMException {
        this.data.appendData(arg);
    }

    public void deleteData(int offset, int count) throws DOMException {
        this.data.deleteData(offset, count);
    }

    public String getData() throws DOMException {
        return this.data.getData();
    }

    public void setData(String data2) throws DOMException {
        this.data.setData(data2);
    }

    public int getLength() {
        return this.data.getLength();
    }

    public void insertData(int offset, String arg) throws DOMException {
        this.data.insertData(offset, arg);
    }

    public void replaceData(int offset, int count, String arg) throws DOMException {
        this.data.replaceData(offset, count, arg);
    }

    public String substringData(int offset, int count) throws DOMException {
        if (offset + count > this.data.getLength()) {
            count = this.data.getLength() - offset;
        }
        return this.data.substringData(offset, count);
    }

    public String getApiName() {
        return "Ti.XML.CharacterData";
    }
}
