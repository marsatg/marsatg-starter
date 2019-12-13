package org.marsatg.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP客户端工具类（支持HTTPS）
 */
public class HttpClientUtils {


    static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * http的get请求
     *
     * @param url
     */
    public static String get(String url) {
        return get(url, "UTF-8");
    }

    /**
     * http的get请求
     *
     * @param url
     */
    public static String get(String url, String charset) {
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet, charset);
    }

    /**
     * http的get请求，增加异步请求头参数
     *
     * @param url
     */
    public static String ajaxGet(String url) {
        return ajaxGet(url, "UTF-8");
    }

    /**
     * http的get请求，增加异步请求头参数
     *
     * @param url
     */
    public static String ajaxGet(String url, String charset) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        return executeRequest(httpGet, charset);
    }

    /**
     * http的get请求，增加异步请求头参数
     *
     * @param url
     */
    public static String ajaxGetWithToken(String url, String token) {
        return ajaxGetWithToken(url, "UTF-8", token);
    }
    /**
     * http的get请求，增加异步请求头参数
     *
     * @param url
     */
    public static String ajaxGetWithToken(String url, String charset, String token) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        httpGet.setHeader("Authorization", "Bearer " + token);
        return executeRequest(httpGet, charset);
    }

    /**
     * http的post请求，传递map格式参数
     */
    public static String post(String url, Map<String, String> dataMap) {
        return post(url, dataMap, "UTF-8");
    }

    /**
     * http的post请求，传递map格式参数
     */
    public static String post(String url, Map<String, String> dataMap, String charset) {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (dataMap != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
                formEntity.setContentEncoding(charset);
                httpPost.setEntity(formEntity);
            }
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        return executeRequest(httpPost, charset);
    }

    /**
     * http的post请求，增加异步请求头参数，传递map格式参数
     */
    public static String ajaxPost(String url, Map<String, String> dataMap) {
        return ajaxPost(url, dataMap, "UTF-8");
    }

    /**
     * http的post请求，增加异步请求头参数，传递map格式参数
     */
    public static String ajaxPost(String url, Map<String, String> dataMap, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        try {
            if (dataMap != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
                formEntity.setContentEncoding(charset);
                httpPost.setEntity(formEntity);
            }
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        return executeRequest(httpPost, charset);
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     */
    public static String ajaxPostJson(String url, String jsonString) {
        return ajaxPostJson(url, jsonString, "UTF-8");
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     */
    public static String ajaxPostJson(String url, String jsonString, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
//		try {
        StringEntity stringEntity = new StringEntity(jsonString, charset);// 解决中文乱码问题
        stringEntity.setContentEncoding(charset);
        stringEntity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(stringEntity);
//		} catch (UnsupportedEncodingException e) {
//			logger.info(e.getMessage());
//		}
        return executeRequest(httpPost, charset);
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     * 自定义Header
     */
    public static String ajaxPostHeaderJson(String url, String jsonString, String token) {
        return ajaxPostTokenJson(url, jsonString, "UTF-8", token);
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     * 自定义Header
     */
    public static String ajaxPostTokenJson(String url, String jsonString, String charset, String token) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + token);
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
//		try {
        StringEntity stringEntity = new StringEntity(jsonString, charset);// 解决中文乱码问题
        stringEntity.setContentEncoding(charset);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
//		} catch (UnsupportedEncodingException e) {
//			logger.info(e.getMessage());
//		}
        return executeRequest(httpPost, charset);
    }

    /**
     * 执行一个http请求，传递HttpGet或HttpPost参数
     */
    public static String executeRequest(HttpUriRequest httpRequest) {
        return executeRequest(httpRequest, "UTF-8");
    }

    /**
     * 执行一个http请求，传递HttpGet或HttpPost参数
     */
    public static String executeRequest(HttpUriRequest httpRequest, String charset) {
        CloseableHttpClient httpclient;
        if ("https".equals(httpRequest.getURI().getScheme())) {
            httpclient = createSSLInsecureClient();
        } else {
            httpclient = HttpClients.createDefault();
        }
        String result = "";
        try {
            try {
                CloseableHttpResponse response = httpclient.execute(httpRequest);
                HttpEntity entity = null;
                try {
                    entity = response.getEntity();
                    result = EntityUtils.toString(entity, charset);
                } finally {
                    EntityUtils.consume(entity);
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
        return result;
    }

    /**
     * 创建 SSL连接
     */
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     * 自定义Header
     */
    public static String ajaxPutHeaderJson(String url, String jsonString, String token) {
        return ajaxPutTokenJson(url, jsonString, "UTF-8", token);
    }

    /**
     * http的post请求，增加异步请求头参数，传递json格式参数
     * 自定义Header
     */
    public static String ajaxPutTokenJson(String url, String jsonString, String charset, String token) {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Authorization", "Bearer " + token);
        httpPut.setHeader("X-Requested-With", "XMLHttpRequest");
        StringEntity stringEntity = new StringEntity(jsonString, charset);// 解决中文乱码问题
        stringEntity.setContentEncoding(charset);
        stringEntity.setContentType("application/json");
        httpPut.setEntity(stringEntity);
        return executeRequest(httpPut, charset);
    }

}
