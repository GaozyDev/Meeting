//package io.agora.meeting.xiaoxi;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import io.agora.rtc.IRtcEngineEventHandler;
//import io.agora.rtc.RtcEngine;
//import io.agora.rtc.video.VideoCanvas;
//import io.agora.rtc.video.VideoEncoderConfiguration;
//
//public class MeetingActivity extends AppCompatActivity {
//
//    private static final String TAG = com.xiaoxi.meeting.MeetingActivity.class.getSimpleName();
//
//    private static final int PERMISSION_REQ_ID = 22;
//
//    private static final String[] REQUESTED_PERMISSIONS = {
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA
//    };
//
//    private RtcEngine mRtcEngine;
//    private boolean mCallEnd;
//    private boolean mMuted;
//
//    private FrameLayout mLocalContainer;
//    private RelativeLayout mRemoteContainer;
//    private VideoCanvas mLocalVideo;
//    private VideoCanvas mRemoteVideo;
//
//    private ImageView mCallBtn;
//    private ImageView mMuteBtn;
//    private ImageView mSwitchCameraBtn;
//
//    private String token;
//    private String channel;
//
//    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
//
//        @Override
//        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
//                }
//            });
//        }
//
//        @Override
//        public void onUserJoined(final int uid, int elapsed) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
//                    setupRemoteVideo(uid);
//                }
//            });
//        }
//
//        @Override
//        public void onUserOffline(final int uid, int reason) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "User offline, uid: " + (uid & 0xFFFFFFFFL));
//                    onRemoteUserLeft(uid);
//                }
//            });
//        }
//    };
//
//    private void setupRemoteVideo(int uid) {
//        ViewGroup parent = mRemoteContainer;
//        if (parent.indexOfChild(mLocalVideo.view) > -1) {
//            parent = mLocalContainer;
//        }
//
//        if (mRemoteVideo != null) {
//            return;
//        }
//
//        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
//        view.setZOrderMediaOverlay(parent == mLocalContainer);
//        parent.addView(view);
//        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
//        mRtcEngine.setupRemoteVideo(mRemoteVideo);
//    }
//
//    private void onRemoteUserLeft(int uid) {
//        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
//            removeFromParent(mRemoteVideo);
//            mRemoteVideo = null;
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_meeting);
//        String s = getIntent().getStringExtra("json");
//        try {
//            JSONObject jsonObject = new JSONObject(s);
//            token = jsonObject.getString("token");
//            channel = jsonObject.getString("channel");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        initUI();
//
//        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
//                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
//            initEngineAndJoinChannel();
//        }
//    }
//
//    private void initUI() {
//        mLocalContainer = findViewById(R.id.local_video_view_container);
//        mRemoteContainer = findViewById(R.id.remote_video_view_container);
//
//        mCallBtn = findViewById(R.id.btn_call);
//        mMuteBtn = findViewById(R.id.btn_mute);
//        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);
//    }
//
//    private boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, permission) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQ_ID) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
//                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
//                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
//                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
//                        "/" + Manifest.permission.CAMERA);
//                finish();
//                return;
//            }
//
//            initEngineAndJoinChannel();
//        }
//    }
//
//    private void showLongToast(final String msg) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void initEngineAndJoinChannel() {
//        initializeEngine();
//        setupVideoConfig();
//        setupLocalVideo();
//        joinChannel();
//    }
//
//    private void initializeEngine() {
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), "63d77718e40c4e0db1c267023d56a5f0", mRtcEventHandler);
//            mRtcEngine.muteLocalAudioStream(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
//        }
//    }
//
//    private void setupVideoConfig() {
//        mRtcEngine.enableVideo();
//
//        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
//                VideoEncoderConfiguration.VD_640x360,
//                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
//                VideoEncoderConfiguration.STANDARD_BITRATE,
//                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
//    }
//
//    private void setupLocalVideo() {
//        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
//        view.setZOrderMediaOverlay(true);
//        mLocalContainer.addView(view);
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
//        mRtcEngine.setupLocalVideo(mLocalVideo);
//    }
//
//    private void joinChannel() {
//        mRtcEngine.joinChannel(token, channel, "Extra Optional Data", 0);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (!mCallEnd) {
//            leaveChannel();
//        }
//        RtcEngine.destroy();
//    }
//
//    private void leaveChannel() {
//        mRtcEngine.leaveChannel();
//    }
//
//    public void onLocalAudioMuteClicked(View view) {
//        mMuted = !mMuted;
//        mRtcEngine.muteLocalAudioStream(mMuted);
//        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
//        mMuteBtn.setImageResource(res);
//    }
//
//    public void onSwitchCameraClicked(View view) {
//        mRtcEngine.switchCamera();
//    }
//
//    public void onCallClicked(View view) {
//        if (mCallEnd) {
//            startCall();
//            mCallEnd = false;
//            mCallBtn.setImageResource(R.drawable.btn_endcall);
//        } else {
//            endCall();
//            mCallEnd = true;
//            mCallBtn.setImageResource(R.drawable.btn_startcall);
//        }
//
//        showButtons(!mCallEnd);
//    }
//
//    private void startCall() {
//        setupLocalVideo();
//        joinChannel();
//    }
//
//    private void endCall() {
//        removeFromParent(mLocalVideo);
//        mLocalVideo = null;
//        removeFromParent(mRemoteVideo);
//        mRemoteVideo = null;
//        leaveChannel();
//    }
//
//    private void showButtons(boolean show) {
//        int visibility = show ? View.VISIBLE : View.GONE;
//        mMuteBtn.setVisibility(visibility);
//        mSwitchCameraBtn.setVisibility(visibility);
//    }
//
//    private ViewGroup removeFromParent(VideoCanvas canvas) {
//        if (canvas != null) {
//            ViewParent parent = canvas.view.getParent();
//            if (parent != null) {
//                ViewGroup group = (ViewGroup) parent;
//                group.removeView(canvas.view);
//                return group;
//            }
//        }
//        return null;
//    }
//
//    private void switchView(VideoCanvas canvas) {
//        ViewGroup parent = removeFromParent(canvas);
//        if (parent == mLocalContainer) {
//            if (canvas.view instanceof SurfaceView) {
//                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
//            }
//            mRemoteContainer.addView(canvas.view);
//        } else if (parent == mRemoteContainer) {
//            if (canvas.view instanceof SurfaceView) {
//                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
//            }
//            mLocalContainer.addView(canvas.view);
//        }
//    }
//
//    public void onLocalContainerClick(View view) {
//        switchView(mLocalVideo);
//        switchView(mRemoteVideo);
//    }
//}