package p006ti.modules.titanium.p007ui.widget.webview;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollEventCallback;
import org.appcelerator.kroll.KrollLogging;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewBinding */
public class TiWebViewBinding {
    protected static final String INJECTION_CODE;
    protected static String POLLING_CODE = null;
    protected static final String SCRIPT_INJECTION_ID = "__ti_injection";
    protected static final String SCRIPT_TAG_INJECTION_CODE;
    private static final String TAG = "TiWebViewBinding";
    private ApiBinding apiBinding;
    private AppBinding appBinding;
    /* access modifiers changed from: private */
    public Stack<String> codeSnippets = new Stack<>();
    /* access modifiers changed from: private */
    public boolean destroyed;
    private boolean interfacesAdded = false;
    /* access modifiers changed from: private */
    public Semaphore returnSemaphore = new Semaphore(0);
    /* access modifiers changed from: private */
    public String returnValue;
    private TiReturn tiReturn;
    private WebView webView;

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewBinding$ApiBinding */
    private class ApiBinding {
        private KrollLogging logging = KrollLogging.getDefault();

        public ApiBinding() {
        }

        @JavascriptInterface
        public void log(String level, String arg) {
            this.logging.log(level, arg);
        }

        @JavascriptInterface
        public void info(String arg) {
            this.logging.info(arg);
        }

        @JavascriptInterface
        public void debug(String arg) {
            this.logging.debug(arg);
        }

        @JavascriptInterface
        public void error(String arg) {
            this.logging.error(arg);
        }

        @JavascriptInterface
        public void trace(String arg) {
            this.logging.trace(arg);
        }

        @JavascriptInterface
        public void warn(String arg) {
            this.logging.warn(arg);
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewBinding$AppBinding */
    private class AppBinding {
        private HashMap<String, Integer> appListeners = new HashMap<>();
        private String code = null;
        private int counter = 0;
        private KrollModule module = TiApplication.getInstance().getModuleByName("App");

        public AppBinding() {
        }

        @JavascriptInterface
        public void fireEvent(String event, String json) {
            try {
                KrollDict dict = new KrollDict();
                if (json != null && !json.equals("undefined")) {
                    dict = new KrollDict(new JSONObject(json));
                }
                this.module.fireEvent(event, dict);
            } catch (JSONException e) {
                Log.m34e(TiWebViewBinding.TAG, "Error parsing event JSON", (Throwable) e);
            }
        }

        @JavascriptInterface
        public int addEventListener(String event, int id) {
            int result = this.module.addEventListener(event, new WebViewCallback(id));
            this.appListeners.put(event, Integer.valueOf(result));
            return result;
        }

        @JavascriptInterface
        public void removeEventListener(String event, int id) {
            this.module.removeEventListener(event, id);
        }

        @JavascriptInterface
        public void clearEventListeners() {
            for (String event : this.appListeners.keySet()) {
                removeEventListener(event, ((Integer) this.appListeners.get(event)).intValue());
            }
        }

        @JavascriptInterface
        public String getJSCode() {
            if (TiWebViewBinding.this.destroyed) {
                return null;
            }
            return this.code;
        }

        @JavascriptInterface
        public int hasResult() {
            if (TiWebViewBinding.this.destroyed) {
                return -1;
            }
            int result = 0;
            synchronized (TiWebViewBinding.this.codeSnippets) {
                if (TiWebViewBinding.this.codeSnippets.empty()) {
                    this.code = "";
                } else {
                    result = 1;
                    this.code = (String) TiWebViewBinding.this.codeSnippets.pop();
                }
            }
            return result;
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewBinding$TiReturn */
    private class TiReturn {
        private TiReturn() {
        }

        @JavascriptInterface
        public void setValue(String value) {
            if (value != null) {
                TiWebViewBinding.this.returnValue = value;
            }
            TiWebViewBinding.this.returnSemaphore.release();
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewBinding$WebViewCallback */
    private class WebViewCallback implements KrollEventCallback {

        /* renamed from: id */
        private int f61id;

        public WebViewCallback(int id) {
            this.f61id = id;
        }

        public void call(Object data) {
            String dataString;
            if (data == null) {
                dataString = "";
            } else if (data instanceof HashMap) {
                dataString = ", " + String.valueOf(TiConvert.toJSON((HashMap) data));
            } else {
                dataString = ", " + String.valueOf(data);
            }
            String code = "Ti.executeListener(" + this.f61id + dataString + ");";
            synchronized (TiWebViewBinding.this.codeSnippets) {
                TiWebViewBinding.this.codeSnippets.push(code);
            }
        }
    }

    static {
        POLLING_CODE = "";
        StringBuilder jsonCode = readResourceFile("json2.js");
        StringBuilder tiCode = readResourceFile("binding.min.js");
        StringBuilder pollingCode = readResourceFile("polling.min.js");
        if (pollingCode == null) {
            Log.m44w(TAG, "Unable to read polling code");
        } else {
            POLLING_CODE = pollingCode.toString();
        }
        StringBuilder scriptCode = new StringBuilder();
        StringBuilder injectionCode = new StringBuilder();
        scriptCode.append("\n<script id=\"__ti_injection\">\n");
        if (jsonCode == null) {
            Log.m44w(TAG, "Unable to read JSON code for injection");
        } else {
            scriptCode.append(jsonCode);
            injectionCode.append(jsonCode);
        }
        if (tiCode == null) {
            Log.m44w(TAG, "Unable to read Titanium binding code for injection");
        } else {
            scriptCode.append("\n");
            scriptCode.append(tiCode.toString());
            injectionCode.append(tiCode.toString());
        }
        scriptCode.append("\n</script>\n");
        SCRIPT_TAG_INJECTION_CODE = scriptCode.toString();
        INJECTION_CODE = injectionCode.toString();
    }

    public TiWebViewBinding(WebView webView2) {
        this.webView = webView2;
        this.apiBinding = new ApiBinding();
        this.appBinding = new AppBinding();
        this.tiReturn = new TiReturn();
    }

    public void addJavascriptInterfaces() {
        if (this.webView != null && !this.interfacesAdded) {
            this.webView.addJavascriptInterface(this.appBinding, "TiApp");
            this.webView.addJavascriptInterface(this.apiBinding, "TiAPI");
            this.webView.addJavascriptInterface(this.tiReturn, "_TiReturn");
            this.interfacesAdded = true;
        }
    }

    public void destroy() {
        this.appBinding.clearEventListeners();
        this.webView = null;
        this.returnSemaphore.release();
        this.codeSnippets.clear();
        this.destroyed = true;
    }

    private static StringBuilder readResourceFile(String fileName) {
        String line = TiWebViewBinding.class.getClassLoader().getResourceAsStream("ti/modules/titanium/ui/widget/webview/" + fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(line));
        StringBuilder code = new StringBuilder();
        try {
            for (String line2 = reader.readLine(); line != null; line2 = reader.readLine()) {
                code.append(line + "\n");
            }
            if (line != null) {
                try {
                    line.close();
                } catch (IOException e) {
                    Log.m46w(TAG, "Problem closing input stream.", (Throwable) e);
                }
            }
        } catch (IOException e2) {
            Log.m34e(TAG, "Error reading input stream", (Throwable) e2);
            code = null;
            if (line != null) {
                try {
                    line.close();
                } catch (IOException e3) {
                    Log.m46w(TAG, "Problem closing input stream.", (Throwable) e3);
                }
            }
        } finally {
            if (line != null) {
                try {
                    line.close();
                } catch (IOException e4) {
                    Log.m46w(TAG, "Problem closing input stream.", (Throwable) e4);
                }
            }
        }
        return code;
    }

    public synchronized String getJSValue(String expression) {
        String str;
        if (!this.destroyed && this.interfacesAdded) {
            String code = "_TiReturn.setValue((function(){try{return " + expression + "+\"\";}catch(ti_eval_err){return '';}})());";
            Log.m29d(TAG, "getJSValue:" + code, Log.DEBUG_MODE);
            this.returnSemaphore.drainPermits();
            synchronized (this.codeSnippets) {
                this.codeSnippets.push(code);
            }
            try {
                if (!this.returnSemaphore.tryAcquire(3500, TimeUnit.MILLISECONDS)) {
                    synchronized (this.codeSnippets) {
                        this.codeSnippets.removeElement(code);
                    }
                    Log.m44w(TAG, "Timeout waiting to evaluate JS");
                }
                str = this.returnValue;
            } catch (InterruptedException e) {
                Log.m34e(TAG, "Interrupted", (Throwable) e);
            }
        }
        str = null;
        return str;
    }
}
