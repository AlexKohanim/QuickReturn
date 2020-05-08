package p006ti.modules.titanium.network.httpurlconnection;

import java.io.IOException;
import java.io.OutputStream;

/* renamed from: ti.modules.titanium.network.httpurlconnection.SingleBody */
public abstract class SingleBody implements Body {
    private Entity parent = null;

    public abstract void writeTo(OutputStream outputStream) throws IOException;

    protected SingleBody() {
    }

    public Entity getParent() {
        return this.parent;
    }

    public void setParent(Entity parent2) {
        this.parent = parent2;
    }
}
