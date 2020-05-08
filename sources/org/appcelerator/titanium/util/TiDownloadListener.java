package org.appcelerator.titanium.util;

import java.net.URI;

public interface TiDownloadListener {
    void downloadTaskFailed(URI uri);

    void downloadTaskFinished(URI uri);

    void postDownload(URI uri);
}
