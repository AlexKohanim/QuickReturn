package p006ti.modules.titanium.network;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/* renamed from: ti.modules.titanium.network.TiAuthenticator */
public class TiAuthenticator extends Authenticator {
    private static final int MAX_RETRY_COUNT = 3;
    String domain;
    String password;
    private int retryCount;
    String user;

    public TiAuthenticator(String user2, String password2) {
        this.retryCount = 0;
        this.domain = null;
        this.user = user2;
        this.password = password2;
    }

    public TiAuthenticator(String domain2, String user2, String password2) {
        this.retryCount = 0;
        this.domain = domain2;
        this.user = user2;
        this.password = password2;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        if (this.domain != null && !this.domain.isEmpty()) {
            this.user = this.domain + "\\" + this.user;
        }
        if (this.retryCount >= 3) {
            return null;
        }
        this.retryCount++;
        if (this.password != null) {
            return new PasswordAuthentication(this.user, this.password.toCharArray());
        }
        return new PasswordAuthentication(this.user, null);
    }
}
