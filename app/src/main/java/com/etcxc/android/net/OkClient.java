package com.etcxc.android.net;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.etcxc.MeManager;
import com.etcxc.android.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 对okhttp封装
 * Created by xwpeng on 2017/5/28.
 */
public class OkClient {
    private static final String TAG = "OkClient";
    public static final String HTTP_PREFIX = "http://";
    private static final String HTTPS = "https";
    private static final String MEDIATYPE_TEXT = "text";
    private static final String MEDIATYPE_APPLICATION = "application";

    public static final MediaType JSON
            = MediaType.parse("text/x-json;charset=UTF-8");
    public static final MediaType NORMAL
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String HEADER_SET_COOKIE = "Set-Cookie";


    private static final String EMPTY_BODY = new JSONObject().toString();

    private static final Lock wLock = new ReentrantReadWriteLock().writeLock();


    public static String get(String url, JSONArray array) {
        return get(url, array, null);
    }

    public static String get(String url, JSONObject object) {
        return get(url, object, null);
    }

    public static String get(String url, JSONArray object, Object tag) {
        Object body = get(url, null, object == null ? null : object.toString(), tag, false);
        return body instanceof String ? body.toString() : null;
    }

    public static String get(String url, JSONObject array, Object tag) {
        Object body = get(url, null, array == null ? null : array.toString(), tag, false);
        return body instanceof String ? body.toString() : null;
    }

    private static Request initRequest(String url, Map<String, String> differentHeaders, String requestBody) {
        //url可能需要修正
        url = rightUrl(url);
        Request.Builder builder = new Request.Builder().url(url);
        setCommonHeader(builder);
        if (TextUtils.isEmpty(requestBody) || EMPTY_BODY.equals(requestBody)) {
            builder.get();
        } else {
            builder.post(RequestBody.create(JSON, requestBody));
        }
        if (differentHeaders != null && !differentHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : differentHeaders.entrySet()) {
                String key = entry.getKey();
                if (TextUtils.isEmpty(key)) continue;
                builder.removeHeader(key);
                builder.addHeader(key, entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 最终的请求
     *
     * @param url              请求的url
     * @param differentHeaders 本身有默认的headers，如果有特殊的，则在此传进来，默认传null即可
     * @param requestBody      需要附加的json参数，如果不为空就是post请求，空使用get请求
     * @return {@link String} or {@link InputStream} or NULL
     */
    private static Object getOriginal(String url, Map<String, String> differentHeaders, String requestBody, Object tag) {
        //根据http或https获取不同的OKHttpClient
        OkHttpClient client = rightClient(url);
        Request request = initRequest(url, differentHeaders, requestBody);
        //TODO：记录http请求日志到本地文件，结合拦截器做
        Object result = null;
        try {
            Response response = client.newCall(request).execute();
            final int CODE = response.code();
            if (response.isSuccessful()) {
                // TODO: 2017/5/28 将后端的cookie持久化到本地
           /*     String cookieFrom = response.header(HEADER_SET_COOKIE, null);
                if (cookieFrom != null) {
                    MeManager.setCookieInHeader(getPureCookie(cookieFrom));
                }*/
                if (204 == CODE) {
                    LogUtil.e(TAG, "net return code is 204, return null.");
                    return null;
                }
                ResponseBody body = response.body();
                MediaType t = body.contentType();
                if (t == null) {
                    LogUtil.e(TAG, "MediaType is null, return null.");
                    return null;
                } else {
                    //要区分返回的是text还是Stream，有时请求以为返回是Stream，却返回text，比如请求图片，若session过期，则返回text
                    result = isText(t.type()) ? body.string() : body.byteStream();
                }
            } else if (301 == CODE || 302 == CODE) {
                LogUtil.i(TAG, "redirect " + CODE);
                String redirectUrl = response.header("Location");
                if (!TextUtils.isEmpty(redirectUrl))
                    result = getOriginal(redirectUrl, null, requestBody, tag);//TODO 死循环
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getOriginal", e);
        }
        return result;
    }

    public static Object get(String url, Map<String, String> differentHeaders, String bodyStr, Object tag, boolean afterRelogin) {
        if (TextUtils.isEmpty(url)) {
            LogUtil.e(TAG, "url is null.");
            return null;
        }
        Object result = getOriginal(url, differentHeaders, bodyStr, tag);
        if (result == null) return null;
     /*   if (isInvalidSessionOrCookie(url, result)) {
            wLock.lock();
            result = getOriginal(newUrl(url), differentHeaders, bodyStr, tag);
            if (result == null) {
                return releaseLockAndReturn(wLock, null);//每次返回都要先释放锁，以下皆同
            }
            //锁了之后再判断一次isInvalidSession
            if (isInvalidSessionOrCookie(url, result)) {
                //todo:重登，拿已保存好的用户名和密码
              *//*  String lastUser = PublicSPUtil.getInstance().getLastUser();
                String lastPwd = PublicSPUtil.getInstance().getLastPwd();
                UserLoginResult ulr = new LoginClient().login(new LoginOptions(lastUser, lastPwd, null, null)).userLoginResult;
                if (ulr.isOK()) {
                    Object r = get(newUrl(url), differentHeaders, bodyStr, tag, true);
                    return releaseLockAndReturn(wLock, r);
                }*//*
            }
            return releaseLockAndReturn(wLock, result);
        }*/
        return result;
    }

    private static Object releaseLockAndReturn(Lock lock, Object result) {
        try {
            return result;
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                LogUtil.e(TAG, "wLock.unlock", e);
            }
        }
    }

    /**
     * 判断是否返回session过期
     * todo：排除登录接口
     */
    private static boolean isInvalidSessionOrCookie(String url, @NonNull Object object) {
        return object instanceof String
                && !TextUtils.isEmpty(url)
//                && !url.contains("func=" + NetConfig.FUNC_User_CheckVersionAndLogin)
//                && !url.contains("func=" + NetConfig.FUNC_User_Login)
                && isInvalidSessionOrCookie(object.toString());
    }

    /**
     * @param json
     * @return session过期或Cookie不可用
     */
    public static boolean isInvalidSessionOrCookie(String json) {
        if (TextUtils.isEmpty(json)) return false;
        boolean flag = false;
        try {
            JSONObject object = new JSONObject();
            String code = object.getString("code");
            flag = NetConfig.CODE_FA_INVALID_SESSION.equals(code);
            if (!flag && NetConfig.CODE_FA_SECURITY.equals(code) && "Cookie".equals(object.getString("securityReason"))) {
                JSONArray jsonArray = object.getJSONArray("messages");
                if (jsonArray != null && jsonArray.length() > 0) {
                    flag = "ERROR".equals(jsonArray.getJSONObject(0).getString("severity"));
                }
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, "isInvalidSessionOrCookie", e);
        }
        return flag;
    }

    /**
     * 判断是否是文本<br/>
     */
    private static boolean isText(String type) {
        return MEDIATYPE_TEXT.equals(type) || MEDIATYPE_APPLICATION.equals(type);
    }

    /**
     * 解析Cim=这个cookie
     */
    public static String getPureCookie(String cookie) {
        int index1 = cookie.indexOf("Cim=");
        if (index1 == -1) {
            return cookie;
        }
        int index2 = cookie.indexOf(';', index1);
        return index2 == -1 ? cookie.substring(index1) : cookie.substring(index1, index2);
    }

    /**
     * 替换旧url的sid，返回新的
     *
     * @param oldUrl
     * @return
     */
    private static String newUrl(String oldUrl) {
        if (TextUtils.isEmpty(oldUrl)) {
            return null;
        }
        String word = "&sid=";
        int index = oldUrl.toLowerCase(Locale.getDefault()).indexOf(word);
        if (index == -1) {
            return oldUrl;
        }
        index += word.length();
        int index2 = oldUrl.indexOf('&', index);
        return oldUrl.substring(0, index) + MeManager.getSid() + (index2 == -1 ? "" : oldUrl.substring(index2));
    }

    public static String rightUrl(String url) {
        if (TextUtils.isEmpty(url)) return url;
        try {
            return new URI(url).getScheme() == null ? (HTTP_PREFIX + url) : url;
        } catch (URISyntaxException e) {
            LogUtil.e(TAG, "rightUrl " + url, e);
            return url;
        }
    }

    /**
     * 忽略所有https证书
     */
    private static SSLContext overlockCard() {
        final TrustManager[] trustAllCerts = new TrustManager[]{x509TrustManager};
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
        } catch (Exception e) {
            LogUtil.e(TAG, "overlockCard", e);
        }
        return sslContext;
    }

    public static OkHttpClient rightClient(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
      /*  if (true) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //如果想要详细的信息，把级别改成BODY，但是会造成全接收完传输内容才会回调Response,下载会有明显延时更新效果
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            client = client.newBuilder().addInterceptor(logging).build();
        }*/
        if (httpOrHttps(url)) {
            client = client.newBuilder().sslSocketFactory(overlockCard().getSocketFactory(), x509TrustManager)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();
        }
        return client;
    }

    private static X509TrustManager x509TrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] chain,
                String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] chain,
                String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] x509Certificates = new X509Certificate[0];
            return x509Certificates;
        }
    };

    /**
     * @return true:https false:http
     */
    private static boolean httpOrHttps(String url) {
        try {
            if (HTTPS.equalsIgnoreCase(new URI(url).getScheme())) {
                return true;
            }
        } catch (URISyntaxException e) {
            // FIXME:
//java.net.URISyntaxException: Illegal character in path at index 68: http://192.168.6.58/transaction/transaction/transactionmail/address/  vvvvgggg/area_county/上虞市/veh_plate_code/湘A12345/area_city/绍兴市/receiver/cccccf/mail_tel/17375851912/area_street/汤浦镇/veh_plate_colour/黄底黑字/area_province/浙江省
            LogUtil.e(TAG, "httpOrHttps " + url, e);
        }
        return false;
    }

    public static void setCommonHeader(Request.Builder builder) {
        builder.addHeader("Cache-Control", "max-age=0");
//        builder.addHeader("Accept-Encoding", "gzip, deflate");
        builder.addHeader("X-CM-SERVICE", "Android");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        //TODO：设置默认的cookies
     /*   String cookieTo = MeManager.getCookieInHeader();
        if (cookieTo != null) {
            builder.addHeader("Cookie", cookieTo);
        }*/
    }

}
