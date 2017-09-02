package com.etcxc.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ${liuTao} on 2017/9/2/002.
 */

public class TestPostactivity extends Activity {

    private EditText post_url;
    private EditText post_key;
    private EditText post_value;
    private Button btn;
    private TextView text_show;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_post);
        init();
    }

    private void init() {
        btn = (Button) findViewById(R.id.post_btn);
        post_url = (EditText) findViewById(R.id.post_url);
        post_key = (EditText) findViewById(R.id.key);
        post_value = (EditText) findViewById(R.id.value);
        text_show = (TextView) findViewById(R.id.text_show);
    }

    public void Post(View v) {
        String url = post_url.getText().toString();
        String key = post_key.getText().toString();
        String value = post_value.getText().toString();

        okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient client = httpBuilder
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
    /* 添加两个参数 */
        builder.add(key, value);
        FormBody body = builder.build();

        Request request = new Request
                .Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (ok) {
                            text_show.setText(bodyStr);
                            Toast.makeText(TestPostactivity.this, bodyStr, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TestPostactivity.this, "server error : " + bodyStr, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(TestPostactivity.this, "error : " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
