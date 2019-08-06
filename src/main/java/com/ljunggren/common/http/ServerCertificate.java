package com.ljunggren.common.http;

import java.io.IOException;
import java.security.cert.Certificate;

import javax.net.ssl.SSLSession;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

public class ServerCertificate {

    private static final String PEER_CERTIFICATES = "PEER_CERTIFICATES";

    public static Certificate[] getCertificates(String url) throws IOException {
        HttpResponseInterceptor certificateInterceptor = (httpResponse, context) -> {
            ManagedHttpClientConnection routedConnection = (ManagedHttpClientConnection)context.getAttribute(HttpCoreContext.HTTP_CONNECTION);
            SSLSession sslSession = routedConnection.getSSLSession();

            if (sslSession != null) {
                Certificate[] certificates = sslSession.getPeerCertificates();
                context.setAttribute(PEER_CERTIFICATES, certificates);
            }
        };

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .addInterceptorLast(certificateInterceptor)
                .build();

        try {
            HttpGet httpget = new HttpGet(url);
            HttpContext context = new BasicHttpContext();
            httpClient.execute(httpget, context);
            Certificate[] peerCertificates = (Certificate[])context.getAttribute(PEER_CERTIFICATES);
            return peerCertificates;
        } 
        finally {
            httpClient.close();
        }
    }
    
}