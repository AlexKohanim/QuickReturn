package p006ti.modules.titanium.xml;

import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;

/* renamed from: ti.modules.titanium.xml.ProcessingInstructionProxy */
public class ProcessingInstructionProxy extends NodeProxy {

    /* renamed from: pi */
    private ProcessingInstruction f62pi;

    public ProcessingInstructionProxy(ProcessingInstruction pi) {
        super(pi);
        this.f62pi = pi;
    }

    public String getData() {
        return this.f62pi.getData();
    }

    public String getTarget() {
        return this.f62pi.getTarget();
    }

    public void setData(String data) throws DOMException {
        this.f62pi.setData(data);
    }

    public String getApiName() {
        return "Ti.XML.ProcessingInstruction";
    }
}
