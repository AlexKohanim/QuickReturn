package android.support.p000v4.content;

import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

/* renamed from: android.support.v4.content.EditorCompatGingerbread */
class EditorCompatGingerbread {
    EditorCompatGingerbread() {
    }

    public static void apply(@NonNull Editor editor) {
        try {
            editor.apply();
        } catch (AbstractMethodError e) {
            editor.commit();
        }
    }
}
