package p006ti.modules.titanium.xml;

import org.w3c.dom.Comment;

/* renamed from: ti.modules.titanium.xml.CommentProxy */
public class CommentProxy extends CharacterDataProxy {
    public CommentProxy(Comment comment) {
        super(comment);
    }

    public String getApiName() {
        return "Ti.XML.Comment";
    }
}
