package com.ljunggren.common.http;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContexts;

import com.ljunggren.common.http.ntlm.JCIFSNTLMSchemeFactory;

public class HttpBase {

	protected static final String USER_AGENT = "Mozilla/5.0";
    protected static final String PEER_CERTIFICATES = "PEER_CERTIFICATES";
    
    protected static Response request(Request request, HttpRequestBase restMethod, HttpClientBuilder clientBuilder) 
    		throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException {
		restMethod.setHeader("User-Agent", USER_AGENT);
		Builder requestConfigBuilder = RequestConfig.custom().setRedirectsEnabled(request.isRedirect());

		CookieStore cookiejar = new BasicCookieStore();
		addCookies(cookiejar, request.getCookies());
		clientBuilder.setDefaultCookieStore(cookiejar);
		
    	if (request.getHeaders() != null) {
    		restMethod.setHeaders(request.getHeaders().stream().toArray(Header[]::new));
    	}
		if (request.getNtlmProxy() != null) {
			setNtlmProxy(request, clientBuilder, requestConfigBuilder);
		}
		if (request.getConnectionTimeout() != null) {
			requestConfigBuilder.setConnectTimeout(request.getConnectionTimeout());
		}
		if (request.getSockectTimeout() != null) {
			requestConfigBuilder.setSocketTimeout(request.getSockectTimeout());
		}
		if (request.getAuthorization() != null) {
			setAuthorization(request, clientBuilder, restMethod);
		}
		if (request.isRedirect()) {
			clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		}
		if (request.isAcceptSelfSignedCertificates()) {
			setSelfSignedCertificate(clientBuilder);
		}
		
		restMethod.setConfig(requestConfigBuilder.build());
		HttpClient client = clientBuilder.build();
        HttpContext context = new BasicHttpContext();
		LocalTime startTime = LocalTime.now();

		HttpResponse response = client.execute(restMethod, context);

		long elapsedTime = MILLIS.between(startTime, LocalTime.now());
		List<Cookie> cookies = cookiejar.getCookies();
        Certificate[] peerCertificates = (Certificate[]) context.getAttribute(PEER_CERTIFICATES);
        Header[] headers = response.getAllHeaders();
		int responseCode = response.getStatusLine().getStatusCode();
		String result = getResult(response);
		
		return Response.builder()
				.responseCode(responseCode)
				.page(result)
				.cookies(cookies)
				.responseTime(elapsedTime)
				.certificates(peerCertificates == null ? new ArrayList<Certificate>() : Arrays.asList(peerCertificates))
				.headers(headers == null ? new ArrayList<Header>() : Arrays.asList(headers))
				.build();
    }

	protected static void addCookies(CookieStore cookiejar, List<Cookie> cookies) {
		for (Cookie cookie: cookies) {
			cookiejar.addCookie(cookie);
		}
	}
	
	protected static void setNtlmProxy(Request request, HttpClientBuilder clientBuilder, Builder requestConfigBuilder) {
		Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
		        .register(AuthSchemes.NTLM, new JCIFSNTLMSchemeFactory())
		        .register(AuthSchemes.BASIC, new BasicSchemeFactory())
		        .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
		        .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
		        .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
		        .build();
		clientBuilder.setDefaultAuthSchemeRegistry(authSchemeRegistry)
			.setProxy(new HttpHost(request.getNtlmProxy().getIp(), request.getNtlmProxy().getPort()))
			.setProxyAuthenticationStrategy(ProxyAuthenticationStrategy.INSTANCE);
		requestConfigBuilder.setAuthenticationEnabled(true)
			.setProxyPreferredAuthSchemes(Arrays.asList("NTLM"));
	}

	protected static void setSelfSignedCertificate(HttpClientBuilder clientBuilder) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
		SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(
				sslContext, new String[] {"SSLv2Hello", "SSLv3", "TLSv1","TLSv1.1", "TLSv1.2" }, 
				null, NoopHostnameVerifier.INSTANCE);
		clientBuilder.setSSLSocketFactory(connectionFactory);
	}
	
	protected static void setAuthorization(Request request, HttpClientBuilder clientBuilder, HttpRequestBase httpRequest) {
		Authorization authorization = request.getAuthorization();
		CredentialsProvider credsProvider;
		switch (authorization.getAuthType()) {
		case HEADER_AUTH:
			String base64String = Base64.getEncoder()
				.encodeToString((authorization.getUsername() + ":" + authorization.getPassword()).getBytes());
			httpRequest.setHeader("Authorization", "Basic " + base64String);
			break;
		case BASIC_AUTH:
			Credentials credentials = new UsernamePasswordCredentials(authorization.getUsername(), authorization.getPassword());
			credsProvider = new BasicCredentialsProvider();
	        credsProvider.setCredentials(AuthScope.ANY, credentials);
	        clientBuilder.setDefaultCredentialsProvider(credsProvider);
	        break;
		case NTLM_AUTH:
			credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(
		            new AuthScope(AuthScope.ANY),
		            new NTCredentials(authorization.getUsername(), 
		            		authorization.getPassword(), 
		            		request.getUrl(),
		            		authorization.getDomain()));
		    clientBuilder.setDefaultCredentialsProvider(credsProvider);
			break;
		default:
		}
	}
	
	protected static String getResult(HttpResponse response) throws UnsupportedOperationException, IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuilder result = new StringBuilder();
		String line = new String();
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	protected static void addCertificateBuilder(Request request, HttpClientBuilder clientBuilder) {
		if (request.getUrl().startsWith("https://")) { 
	        HttpResponseInterceptor certificateInterceptor = (httpResponse, context) -> {
	            ManagedHttpClientConnection routedConnection = (ManagedHttpClientConnection)context.getAttribute(HttpCoreContext.HTTP_CONNECTION);
	            SSLSession sslSession = routedConnection.getSSLSession();
	            if (sslSession != null) {
	                Certificate[] certificates = sslSession.getPeerCertificates();
	                context.setAttribute(PEER_CERTIFICATES, certificates);
	            }
	        };
	        clientBuilder.addInterceptorLast(certificateInterceptor);
		}
	}
	
}
