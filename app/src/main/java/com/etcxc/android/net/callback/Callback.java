package com.etcxc.android.net.callback;

import com.etcxc.android.R;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * * Created by LiuTao
 *
 * @param <T>
 */
public abstract class Callback<T> {
    protected final String TAG = "Callback";
    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request, int id) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter(int id) {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total, int id) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(Response response, int id) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, int id) throws Exception;

    public abstract void onError(Call call, Exception e, int id);

    public abstract void onResponse(T response, int id) throws JSONException;


    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            if(e.toString().contains("closed")){
                LogUtil.e(TAG,"取消请求");
            }else {
                ToastUtils.showToast(R.string.request_failed);
            }
        }

        @Override
        public void onResponse(Object response, int id) {

        }
    };

}