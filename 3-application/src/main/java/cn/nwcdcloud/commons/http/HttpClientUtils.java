package cn.nwcdcloud.commons.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;


/**
 * Title: HttpClient工具类<br>
 * Description: <br>
 * Copyright: Copyright (c) 2016<br>
 * 
 * @author
 * @version 1.0
 * @date 2016-3-2
 */
public class HttpClientUtils {

    private static final Log log = LogFactory.getLog(HttpClientUtils.class);

    private static final int CONNECT_TIMEOUT = 5000;

    private static final int SOKET_TIMEOUT = 100000;

    private static PoolingHttpClientConnectionManager cm;
    static {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
            cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // 总的最大连接数
            int defaultMaxTotal = 40;
            cm.setMaxTotal(defaultMaxTotal);
            // 每个路由的连接数
            int defaultMaxPerRoute = 20;
            cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
        } catch (Exception e) {
            log.error("创建SSL连接失败", e);
        }
    }

    public static PoolingHttpClientConnectionManager getConnectionManager() {
        return cm;
    }

    /**
     * 
     * @return
     */
    public static HttpClient getHttpClient() {
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 20 * 1000; // tomcat默认keepAliveTimeout为20s
            }
        };
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(cm);
        httpClientBuilder.setDefaultRequestConfig(getRequestConfig());
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
        httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        // 以下注释代码可能引起内存泄露
        // Runtime.getRuntime().addShutdownHook(new Thread() {
        // @Override
        // public void run() {
        // try {
        // httpClient.close();
        // } catch (IOException e) {
        // log.error("关闭JVM释放HttpClient失败", e);
        // }
        // }
        // });
        return httpClient;
    }

    /**
     * 设置参数
     * 
     * @return
     */
    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(SOKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
    }
}
