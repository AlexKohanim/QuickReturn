package p006ti.modules.titanium.xml;

import org.w3c.dom.Entity;

/* renamed from: ti.modules.titanium.xml.EntityProxy */
public class EntityProxy extends NodeProxy {
    private Entity entity;

    public EntityProxy(Entity entity2) {
        super(entity2);
        this.entity = entity2;
    }

    public String getNotationName() {
        return this.entity.getNotationName();
    }

    public String getPublicId() {
        return this.entity.getPublicId();
    }

    public String getSystemId() {
        return this.entity.getSystemId();
    }

    public String getApiName() {
        return "Ti.XML.Entity";
    }
}
