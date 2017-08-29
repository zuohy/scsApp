package cn.longmaster.doctorlibrary.util.net;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

/**
 * http工具
 */
public class HttpUtil {

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void closeIOStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将Key-value转换成用&号链接的URL查询参数形式。
     *
     * @param parameters
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(key)).append("=").append(URLEncoder.encode(parameters.getString(key)));
        }
        return sb.toString();
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from the
     *               objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Object[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * 解析URL中的查询串转换成key-value
     *
     * @param url
     * @return
     */
    public static Bundle parseUrl(String url) {
        // url = url.replace("rrconnect", "http");
        url = url.replace("#", "?");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    /**
     * 将key1=value1&key2=value2格式的query转换成key-value形式的参数串
     *
     * @param query key1=value1&key2=value2格式的query
     * @return key-value形式的bundle
     */
    @SuppressWarnings("deprecation")
    public static Bundle decodeUrl(String query) {
        Bundle ret = new Bundle();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyAndValues = pair.split("=");
                if (keyAndValues != null && keyAndValues.length == 2) {
                    String key = keyAndValues[0];
                    String value = keyAndValues[1];
                    if (!isEmpty(key) && !isEmpty(value)) {
                        ret.putString(URLDecoder.decode(key), URLDecoder.decode(value));
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 判断字符串是否为空
     *
     * @param query 待检测的字符串
     * @return boolean
     */
    public static boolean isEmpty(String query) {
        boolean ret = false;
        if (query == null || query.trim().equals("")) {
            ret = true;
        }
        return ret;
    }

    /**
     * 连接
     *
     * @param url
     * @param method "POST" or "GET"
     * @param params
     * @return
     * @throws Exception
     */
    public static String openUrl(String url, String method, Bundle params) throws Exception {
        String result = "";
        HttpClient client = getNewHttpClient();
        HttpUriRequest request = null;
        if (method.equals("GET")) {
            url = url + "?" + HttpUtil.encodeUrl(params);
            HttpGet get = new HttpGet(url);
            request = get;
        } else if (method.equals("POST")) {
            HttpPost post = new HttpPost(url);
            request = post;
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            byte[] data = HttpUtil.encodeUrl(params).getBytes("UTF-8");
            ByteArrayEntity formEntity = new ByteArrayEntity(data);
            post.setEntity(formEntity);
        } else if (method.equals("DELETE")) {
            request = new HttpDelete(url);
        }
        HttpResponse response = client.execute(request);
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode != 200) {
            // throw new Exception("连接失败！返回值：" + statusCode);
        }
        result = readHttpResponse(response);
        return result;
    }

    /**
     * 读取HttpResponse数据
     *
     * @param response
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String readHttpResponse(HttpResponse response) {
        String result = "";
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
            inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }

            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            result = new String(content.toByteArray());
            return result;
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
        return result;
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            ConnManagerParams.setTimeout(params, 60 * 1000);
            HttpConnectionParams.setConnectionTimeout(params, 60000);
            HttpConnectionParams.setSoTimeout(params, 60000);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    /**
     * @param reqUrl
     * @param parameters
     * @param fileParamName 传字符串 "pic"
     * @param filename      传字符串 "news_image"
     * @param contentType   传字符串 "image/png"
     * @param in
     * @return
     * @throws Exception
     */
    public static String uploadFile(String reqUrl, Bundle parameters, String fileParamName, String filename, String contentType, InputStream in)
            throws Exception {
        HttpURLConnection urlConn = null;
        try {
            urlConn = sendFormdata(reqUrl, parameters, fileParamName, filename, contentType, in);
            int responseCode = urlConn.getResponseCode();
            InputStream stream;
            if (responseCode != 200) {
                stream = urlConn.getErrorStream();
            } else {
                stream = urlConn.getInputStream();
            }
            String responseContent = read(stream);
            return responseContent.trim();
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    /**
     * @param reqUrl
     * @param parameters
     * @param fileParamName 传字符串 "pic"
     * @param filename      传字符串 "news_image"
     * @param contentType   传字符串 "image/png"
     * @param in            文件输入流
     * @return
     * @throws Exception 上传文件错误
     */
    private static HttpURLConnection sendFormdata(String reqUrl, Bundle parameters, String fileParamName, String filename, String contentType, InputStream in)
            throws Exception {
        HttpURLConnection urlConn = null;
        URL url = new URL(reqUrl);
        urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("POST");
        urlConn.setConnectTimeout(10000);// （单位：毫秒）jdk
        urlConn.setReadTimeout(10000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
        urlConn.setDoOutput(true);

        urlConn.setRequestProperty("connection", "keep-alive");

        String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
        urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        boundary = "--" + boundary;
        StringBuffer params = new StringBuffer();
        if (parameters != null) {
            for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String value = parameters.getString(name);
                params.append(boundary + "\r\n");
                params.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
                params.append(value);
                params.append("\r\n");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(boundary);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + filename + "\"\r\n");
        sb.append("Content-Type: " + contentType + "\r\n\r\n");
        byte[] fileDiv = sb.toString().getBytes();
        byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes();
        byte[] ps = params.toString().getBytes();

        OutputStream os = urlConn.getOutputStream();
        os.write(ps);
        os.write(fileDiv);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        os.write(endData);
        os.flush();
        in.close();
        os.close();
        return urlConn;
    }

}
