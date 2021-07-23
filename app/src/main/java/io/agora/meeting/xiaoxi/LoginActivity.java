package io.agora.meeting.xiaoxi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.agora.meeting.R;
import io.agora.meeting.xiaoxi.utils.SpUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mAccountEt;
    private EditText mPwdEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String uid = SpUtils.getString(LoginActivity.this, "uid", "");
        if (!TextUtils.isEmpty(uid)) {
            startActivity(new Intent(this, MeetingListActivity.class));
            finish();
        }

        mAccountEt = findViewById(R.id.login_et_account);
        mPwdEt = findViewById(R.id.login_et_password);
        findViewById(R.id.login_btn).setOnClickListener(v -> {
            new Thread(() -> login("account=" + mAccountEt.getText() + "&pwd=" + mPwdEt.getText())).start();
        });
    }

    void login(String body) {
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
        String URL = "http://192.168.14.44:8080";
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder()
                .url(URL + "/login")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        try {
            try (Response response = client.newCall(request).execute()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String uid = data.getString("uid");
                    SpUtils.putString(LoginActivity.this, "uid", uid);
                    startActivity(new Intent(this, MeetingListActivity.class));
                    finish();
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}