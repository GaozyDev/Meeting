package io.agora.meeting.xiaoxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.agora.meeting.MainActivity;
import io.agora.meeting.R;
import io.agora.meeting.xiaoxi.utils.SpUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MeetingListActivity extends AppCompatActivity {

    private BaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        findViewById(R.id.main_create).setOnClickListener(v -> {
            new Thread(this::create).start();
        });
        RecyclerView recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter();
        recyclerView.setAdapter(mAdapter);
        new Thread(this::getMeetingList).start();
    }

    private void create() {
        startActivity(new Intent(MeetingListActivity.this, MainActivity.class));
    }

    private void getMeetingList() {
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
        String URL = "http://quick.nat300.top";
        String uid = SpUtils.getString(MeetingListActivity.this, "uid", "");
        RequestBody requestBody = RequestBody.create(mediaType, "uid=" + uid);
        Request request = new Request.Builder()
                .url(URL + "/getMeetingList")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        try {
            try (Response response = client.newCall(request).execute()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                Log.e("gaozy", jsonObject.toString());
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setJsonArray(jsonArray);
                        }
                    });
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder> {

        private JSONArray jsonArray = new JSONArray();

        public void setJsonArray(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View root = inflater.inflate(R.layout.item_meeting_room, parent, false);
            return new BaseViewHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            try {
                holder.bind(jsonArray.getJSONObject(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class BaseViewHolder extends RecyclerView.ViewHolder {

            private final TextView textView;

            public BaseViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }

            void bind(JSONObject jsonObject) {
                textView.setText(jsonObject.toString());
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(MeetingListActivity.this, MainActivity.class);
                    intent.putExtra("json", jsonObject.toString());
                    MeetingListActivity.this.startActivity(intent);
                });
            }
        }
    }
}