package android.support.p000v4.content;

import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

/* renamed from: android.support.v4.content.SharedPreferencesCompat */
public final class SharedPreferencesCompat {

    /* renamed from: android.support.v4.content.SharedPreferencesCompat$EditorCompat */
    public static final class EditorCompat {
        private static EditorCompat sInstance;
        private final Helper mHelper;

        /* renamed from: android.support.v4.content.SharedPreferencesCompat$EditorCompat$EditorHelperApi9Impl */
        private static class EditorHelperApi9Impl implements Helper {
            private EditorHelperApi9Impl() {
            }

            public void apply(@NonNull Editor editor) {
                EditorCompatGingerbread.apply(editor);
            }
        }

        /* renamed from: android.support.v4.content.SharedPreferencesCompat$EditorCompat$EditorHelperBaseImpl */
        private static class EditorHelperBaseImpl implements Helper {
            private EditorHelperBaseImpl() {
            }

            public void apply(@NonNull Editor editor) {
                editor.commit();
            }
        }

        /* renamed from: android.support.v4.content.SharedPreferencesCompat$EditorCompat$Helper */
        private interface Helper {
            void apply(@NonNull Editor editor);
        }

        private EditorCompat() {
            if (VERSION.SDK_INT >= 9) {
                this.mHelper = new EditorHelperApi9Impl();
            } else {
                this.mHelper = new EditorHelperBaseImpl();
            }
        }

        public static EditorCompat getInstance() {
            if (sInstance == null) {
                sInstance = new EditorCompat();
            }
            return sInstance;
        }

        public void apply(@NonNull Editor editor) {
            this.mHelper.apply(editor);
        }
    }

    private SharedPreferencesCompat() {
    }
}
