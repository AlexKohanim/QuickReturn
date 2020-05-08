package android.support.p000v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.p000v4.content.IntentCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: android.support.v4.app.ShareCompat */
public final class ShareCompat {
    public static final String EXTRA_CALLING_ACTIVITY = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_PACKAGE = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
    /* access modifiers changed from: private */
    public static ShareCompatImpl IMPL;

    /* renamed from: android.support.v4.app.ShareCompat$IntentBuilder */
    public static class IntentBuilder {
        private Activity mActivity;
        private ArrayList<String> mBccAddresses;
        private ArrayList<String> mCcAddresses;
        private CharSequence mChooserTitle;
        private Intent mIntent = new Intent().setAction(AndroidModule.ACTION_SEND);
        private ArrayList<Uri> mStreams;
        private ArrayList<String> mToAddresses;

        public static IntentBuilder from(Activity launchingActivity) {
            return new IntentBuilder(launchingActivity);
        }

        private IntentBuilder(Activity launchingActivity) {
            this.mActivity = launchingActivity;
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, launchingActivity.getPackageName());
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, launchingActivity.getComponentName());
            this.mIntent.addFlags(524288);
        }

        public Intent getIntent() {
            boolean needsSendMultiple = true;
            if (this.mToAddresses != null) {
                combineArrayExtra(AndroidModule.EXTRA_EMAIL, this.mToAddresses);
                this.mToAddresses = null;
            }
            if (this.mCcAddresses != null) {
                combineArrayExtra(AndroidModule.EXTRA_CC, this.mCcAddresses);
                this.mCcAddresses = null;
            }
            if (this.mBccAddresses != null) {
                combineArrayExtra(AndroidModule.EXTRA_BCC, this.mBccAddresses);
                this.mBccAddresses = null;
            }
            if (this.mStreams == null || this.mStreams.size() <= 1) {
                needsSendMultiple = false;
            }
            boolean isSendMultiple = this.mIntent.getAction().equals(AndroidModule.ACTION_SEND_MULTIPLE);
            if (!needsSendMultiple && isSendMultiple) {
                this.mIntent.setAction(AndroidModule.ACTION_SEND);
                if (this.mStreams == null || this.mStreams.isEmpty()) {
                    this.mIntent.removeExtra(AndroidModule.EXTRA_STREAM);
                } else {
                    this.mIntent.putExtra(AndroidModule.EXTRA_STREAM, (Parcelable) this.mStreams.get(0));
                }
                this.mStreams = null;
            }
            if (needsSendMultiple && !isSendMultiple) {
                this.mIntent.setAction(AndroidModule.ACTION_SEND_MULTIPLE);
                if (this.mStreams == null || this.mStreams.isEmpty()) {
                    this.mIntent.removeExtra(AndroidModule.EXTRA_STREAM);
                } else {
                    this.mIntent.putParcelableArrayListExtra(AndroidModule.EXTRA_STREAM, this.mStreams);
                }
            }
            return this.mIntent;
        }

        /* access modifiers changed from: 0000 */
        public Activity getActivity() {
            return this.mActivity;
        }

        private void combineArrayExtra(String extra, ArrayList<String> add) {
            int currentLength;
            String[] currentAddresses = this.mIntent.getStringArrayExtra(extra);
            if (currentAddresses != null) {
                currentLength = currentAddresses.length;
            } else {
                currentLength = 0;
            }
            String[] finalAddresses = new String[(add.size() + currentLength)];
            add.toArray(finalAddresses);
            if (currentAddresses != null) {
                System.arraycopy(currentAddresses, 0, finalAddresses, add.size(), currentLength);
            }
            this.mIntent.putExtra(extra, finalAddresses);
        }

        private void combineArrayExtra(String extra, String[] add) {
            int oldLength;
            Intent intent = getIntent();
            String[] old = intent.getStringArrayExtra(extra);
            if (old != null) {
                oldLength = old.length;
            } else {
                oldLength = 0;
            }
            String[] result = new String[(add.length + oldLength)];
            if (old != null) {
                System.arraycopy(old, 0, result, 0, oldLength);
            }
            System.arraycopy(add, 0, result, oldLength, add.length);
            intent.putExtra(extra, result);
        }

        public Intent createChooserIntent() {
            return Intent.createChooser(getIntent(), this.mChooserTitle);
        }

        public void startChooser() {
            this.mActivity.startActivity(createChooserIntent());
        }

        public IntentBuilder setChooserTitle(CharSequence title) {
            this.mChooserTitle = title;
            return this;
        }

        public IntentBuilder setChooserTitle(@StringRes int resId) {
            return setChooserTitle(this.mActivity.getText(resId));
        }

        public IntentBuilder setType(String mimeType) {
            this.mIntent.setType(mimeType);
            return this;
        }

        public IntentBuilder setText(CharSequence text) {
            this.mIntent.putExtra(AndroidModule.EXTRA_TEXT, text);
            return this;
        }

        public IntentBuilder setHtmlText(String htmlText) {
            this.mIntent.putExtra(IntentCompat.EXTRA_HTML_TEXT, htmlText);
            if (!this.mIntent.hasExtra(AndroidModule.EXTRA_TEXT)) {
                setText(Html.fromHtml(htmlText));
            }
            return this;
        }

        public IntentBuilder setStream(Uri streamUri) {
            if (!this.mIntent.getAction().equals(AndroidModule.ACTION_SEND)) {
                this.mIntent.setAction(AndroidModule.ACTION_SEND);
            }
            this.mStreams = null;
            this.mIntent.putExtra(AndroidModule.EXTRA_STREAM, streamUri);
            return this;
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public IntentBuilder addStream(Uri streamUri) {
            Uri currentStream = (Uri) this.mIntent.getParcelableExtra(AndroidModule.EXTRA_STREAM);
            if (this.mStreams == null && currentStream == null) {
                return setStream(streamUri);
            }
            if (this.mStreams == null) {
                this.mStreams = new ArrayList<>();
            }
            if (currentStream != null) {
                this.mIntent.removeExtra(AndroidModule.EXTRA_STREAM);
                this.mStreams.add(currentStream);
            }
            this.mStreams.add(streamUri);
            return this;
        }

        public IntentBuilder setEmailTo(String[] addresses) {
            if (this.mToAddresses != null) {
                this.mToAddresses = null;
            }
            this.mIntent.putExtra(AndroidModule.EXTRA_EMAIL, addresses);
            return this;
        }

        public IntentBuilder addEmailTo(String address) {
            if (this.mToAddresses == null) {
                this.mToAddresses = new ArrayList<>();
            }
            this.mToAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailTo(String[] addresses) {
            combineArrayExtra(AndroidModule.EXTRA_EMAIL, addresses);
            return this;
        }

        public IntentBuilder setEmailCc(String[] addresses) {
            this.mIntent.putExtra(AndroidModule.EXTRA_CC, addresses);
            return this;
        }

        public IntentBuilder addEmailCc(String address) {
            if (this.mCcAddresses == null) {
                this.mCcAddresses = new ArrayList<>();
            }
            this.mCcAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailCc(String[] addresses) {
            combineArrayExtra(AndroidModule.EXTRA_CC, addresses);
            return this;
        }

        public IntentBuilder setEmailBcc(String[] addresses) {
            this.mIntent.putExtra(AndroidModule.EXTRA_BCC, addresses);
            return this;
        }

        public IntentBuilder addEmailBcc(String address) {
            if (this.mBccAddresses == null) {
                this.mBccAddresses = new ArrayList<>();
            }
            this.mBccAddresses.add(address);
            return this;
        }

        public IntentBuilder addEmailBcc(String[] addresses) {
            combineArrayExtra(AndroidModule.EXTRA_BCC, addresses);
            return this;
        }

        public IntentBuilder setSubject(String subject) {
            this.mIntent.putExtra(AndroidModule.EXTRA_SUBJECT, subject);
            return this;
        }
    }

    /* renamed from: android.support.v4.app.ShareCompat$IntentReader */
    public static class IntentReader {
        private static final String TAG = "IntentReader";
        private Activity mActivity;
        private ComponentName mCallingActivity;
        private String mCallingPackage;
        private Intent mIntent;
        private ArrayList<Uri> mStreams;

        public static IntentReader from(Activity activity) {
            return new IntentReader(activity);
        }

        private IntentReader(Activity activity) {
            this.mActivity = activity;
            this.mIntent = activity.getIntent();
            this.mCallingPackage = ShareCompat.getCallingPackage(activity);
            this.mCallingActivity = ShareCompat.getCallingActivity(activity);
        }

        public boolean isShareIntent() {
            String action = this.mIntent.getAction();
            return AndroidModule.ACTION_SEND.equals(action) || AndroidModule.ACTION_SEND_MULTIPLE.equals(action);
        }

        public boolean isSingleShare() {
            return AndroidModule.ACTION_SEND.equals(this.mIntent.getAction());
        }

        public boolean isMultipleShare() {
            return AndroidModule.ACTION_SEND_MULTIPLE.equals(this.mIntent.getAction());
        }

        public String getType() {
            return this.mIntent.getType();
        }

        public CharSequence getText() {
            return this.mIntent.getCharSequenceExtra(AndroidModule.EXTRA_TEXT);
        }

        public String getHtmlText() {
            String result = this.mIntent.getStringExtra(IntentCompat.EXTRA_HTML_TEXT);
            if (result != null) {
                return result;
            }
            CharSequence text = getText();
            if (text instanceof Spanned) {
                return Html.toHtml((Spanned) text);
            }
            if (text != null) {
                return ShareCompat.IMPL.escapeHtml(text);
            }
            return result;
        }

        public Uri getStream() {
            return (Uri) this.mIntent.getParcelableExtra(AndroidModule.EXTRA_STREAM);
        }

        public Uri getStream(int index) {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(AndroidModule.EXTRA_STREAM);
            }
            if (this.mStreams != null) {
                return (Uri) this.mStreams.get(index);
            }
            if (index == 0) {
                return (Uri) this.mIntent.getParcelableExtra(AndroidModule.EXTRA_STREAM);
            }
            throw new IndexOutOfBoundsException("Stream items available: " + getStreamCount() + " index requested: " + index);
        }

        public int getStreamCount() {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(AndroidModule.EXTRA_STREAM);
            }
            if (this.mStreams != null) {
                return this.mStreams.size();
            }
            return this.mIntent.hasExtra(AndroidModule.EXTRA_STREAM) ? 1 : 0;
        }

        public String[] getEmailTo() {
            return this.mIntent.getStringArrayExtra(AndroidModule.EXTRA_EMAIL);
        }

        public String[] getEmailCc() {
            return this.mIntent.getStringArrayExtra(AndroidModule.EXTRA_CC);
        }

        public String[] getEmailBcc() {
            return this.mIntent.getStringArrayExtra(AndroidModule.EXTRA_BCC);
        }

        public String getSubject() {
            return this.mIntent.getStringExtra(AndroidModule.EXTRA_SUBJECT);
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public ComponentName getCallingActivity() {
            return this.mCallingActivity;
        }

        public Drawable getCallingActivityIcon() {
            Drawable drawable = null;
            if (this.mCallingActivity == null) {
                return drawable;
            }
            try {
                return this.mActivity.getPackageManager().getActivityIcon(this.mCallingActivity);
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling activity", e);
                return drawable;
            }
        }

        public Drawable getCallingApplicationIcon() {
            Drawable drawable = null;
            if (this.mCallingPackage == null) {
                return drawable;
            }
            try {
                return this.mActivity.getPackageManager().getApplicationIcon(this.mCallingPackage);
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling application", e);
                return drawable;
            }
        }

        public CharSequence getCallingApplicationLabel() {
            CharSequence charSequence = null;
            if (this.mCallingPackage == null) {
                return charSequence;
            }
            PackageManager pm = this.mActivity.getPackageManager();
            try {
                return pm.getApplicationLabel(pm.getApplicationInfo(this.mCallingPackage, 0));
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve label for calling application", e);
                return charSequence;
            }
        }
    }

    /* renamed from: android.support.v4.app.ShareCompat$ShareCompatImpl */
    interface ShareCompatImpl {
        void configureMenuItem(MenuItem menuItem, IntentBuilder intentBuilder);

        String escapeHtml(CharSequence charSequence);
    }

    /* renamed from: android.support.v4.app.ShareCompat$ShareCompatImplBase */
    static class ShareCompatImplBase implements ShareCompatImpl {
        ShareCompatImplBase() {
        }

        public void configureMenuItem(MenuItem item, IntentBuilder shareIntent) {
            item.setIntent(shareIntent.createChooserIntent());
        }

        public String escapeHtml(CharSequence text) {
            StringBuilder out = new StringBuilder();
            withinStyle(out, text, 0, text.length());
            return out.toString();
        }

        private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
            int i = start;
            while (i < end) {
                char c = text.charAt(i);
                if (c == '<') {
                    out.append("&lt;");
                } else if (c == '>') {
                    out.append("&gt;");
                } else if (c == '&') {
                    out.append("&amp;");
                } else if (c > '~' || c < ' ') {
                    out.append("&#" + c + ";");
                } else if (c == ' ') {
                    while (i + 1 < end && text.charAt(i + 1) == ' ') {
                        out.append("&nbsp;");
                        i++;
                    }
                    out.append(' ');
                } else {
                    out.append(c);
                }
                i++;
            }
        }
    }

    /* renamed from: android.support.v4.app.ShareCompat$ShareCompatImplICS */
    static class ShareCompatImplICS extends ShareCompatImplBase {
        ShareCompatImplICS() {
        }

        public void configureMenuItem(MenuItem item, IntentBuilder shareIntent) {
            ShareCompatICS.configureMenuItem(item, shareIntent.getActivity(), shareIntent.getIntent());
            if (shouldAddChooserIntent(item)) {
                item.setIntent(shareIntent.createChooserIntent());
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean shouldAddChooserIntent(MenuItem item) {
            return !item.hasSubMenu();
        }
    }

    /* renamed from: android.support.v4.app.ShareCompat$ShareCompatImplJB */
    static class ShareCompatImplJB extends ShareCompatImplICS {
        ShareCompatImplJB() {
        }

        public String escapeHtml(CharSequence html) {
            return ShareCompatJB.escapeHtml(html);
        }

        /* access modifiers changed from: 0000 */
        public boolean shouldAddChooserIntent(MenuItem item) {
            return false;
        }
    }

    static {
        if (VERSION.SDK_INT >= 16) {
            IMPL = new ShareCompatImplJB();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new ShareCompatImplICS();
        } else {
            IMPL = new ShareCompatImplBase();
        }
    }

    private ShareCompat() {
    }

    public static String getCallingPackage(Activity calledActivity) {
        String result = calledActivity.getCallingPackage();
        if (result == null) {
            return calledActivity.getIntent().getStringExtra(EXTRA_CALLING_PACKAGE);
        }
        return result;
    }

    public static ComponentName getCallingActivity(Activity calledActivity) {
        ComponentName result = calledActivity.getCallingActivity();
        if (result == null) {
            return (ComponentName) calledActivity.getIntent().getParcelableExtra(EXTRA_CALLING_ACTIVITY);
        }
        return result;
    }

    public static void configureMenuItem(MenuItem item, IntentBuilder shareIntent) {
        IMPL.configureMenuItem(item, shareIntent);
    }

    public static void configureMenuItem(Menu menu, int menuItemId, IntentBuilder shareIntent) {
        MenuItem item = menu.findItem(menuItemId);
        if (item == null) {
            throw new IllegalArgumentException("Could not find menu item with id " + menuItemId + " in the supplied menu");
        }
        configureMenuItem(item, shareIntent);
    }
}
