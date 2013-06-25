package com.treelev.isimple.httpclient;

import android.content.Context;
import com.treelev.isimple.R;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;

public class HttpClientCert extends DefaultHttpClient  {

        final Context context;

        public HttpClientCert(Context context) {
            this.context = context;
        }

        @Override
        protected ClientConnectionManager createClientConnectionManager() {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", newSslSocketFactory(), 443));
            return new SingleClientConnManager(getParams(), registry);
        }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = context.getResources().openRawResource(R.raw.isimple);
            try {
                //  isimpledrink - password for certificat set user (programmer)
                char[] pass = "isimpledrink".toCharArray();
                trusted.load(in, pass);
            } finally {
                in.close();
            }
//            SSLSocketFactory sf = new SSLSocketFactory(trusted);
//            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            SSLSocketFactory sf =  new SSLSocketFactory(trusted);
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
