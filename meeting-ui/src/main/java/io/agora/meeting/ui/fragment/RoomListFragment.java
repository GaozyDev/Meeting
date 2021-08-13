package io.agora.meeting.ui.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.meeting.core.RtcNetworkMonitor;
import io.agora.meeting.core.annotaion.DeviceNetQuality;
import io.agora.meeting.ui.Constant;
import io.agora.meeting.ui.MeetingActivity;
import io.agora.meeting.ui.MeetingApplication;
import io.agora.meeting.ui.R;
import io.agora.meeting.ui.adapter.RoomRecyclerAdapter;
import io.agora.meeting.ui.base.BaseFragment;
import io.agora.meeting.ui.databinding.FragmentRoomListBinding;
import io.agora.meeting.ui.dialog.LoadingDialog;
import io.agora.meeting.ui.http.BaseCallback;
import io.agora.meeting.ui.http.MeetingService;
import io.agora.meeting.ui.http.body.req.RoomStatusReq;
import io.agora.meeting.ui.http.body.req.VerifyTokenReq;
import io.agora.meeting.ui.http.network.RetrofitManager;
import io.agora.meeting.ui.util.ClipboardUtil;
import io.agora.meeting.ui.util.KeyboardUtil;
import io.agora.meeting.ui.util.SpUtils;
import io.agora.meeting.ui.util.StringUtil;
import io.agora.meeting.ui.util.ToastUtil;
import io.agora.meeting.ui.viewmodel.PreferenceViewModel;
import io.agora.meeting.ui.viewmodel.RoomViewModel;
import io.agora.meeting.ui.widget.TipsPopup;

public class RoomListFragment extends BaseFragment<FragmentRoomListBinding> {
    private PreferenceViewModel preferenceVM;

    private RoomViewModel roomVM;

    private RoomRecyclerAdapter mRoomAdapter;

    private LoadingDialog mEnterLoadingDialog;

    private Timer mTimer;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        preferenceVM = provider.get(PreferenceViewModel.class);
        roomVM = provider.get(RoomViewModel.class);
    }

    @Override
    protected FragmentRoomListBinding createBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentRoomListBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init() {
        setupAppBar(binding.toolbar, false);
        binding.toolbar.post(() -> binding.toolbar.setTitle(""));
        binding.btnEnter.setOnClickListener(v -> {
            if (checkInput()) {
                KeyboardUtil.hideInput(requireActivity());
                binding.btnEnter.showLoading();
                enterRoom();
            }
        });

        binding.layoutLimitTip.setOnLongClickListener(v -> {
            showConfigDialog();
            return true;
        });

        binding.aetRoomPwd.setRightIconClickListener(v -> {
            KeyboardUtil.hideInput(requireActivity());
            new TipsPopup(this)
                    .setBackgroundColor(Color.TRANSPARENT)
                    .setPopupGravity(Gravity.BOTTOM)
                    .showPopupWindow(v);
        });

        roomVM.failure.observe(getViewLifecycleOwner(), throwable -> {
            binding.btnEnter.showButtonText();
        });


        initSaveConfigView();
    }

    private void showConfigDialog() {
        View layout = LayoutInflater.from(requireContext()).inflate(R.layout.layout_config, null);
        EditText etDuration = layout.findViewById(R.id.et_meeting_duration);
        EditText etMaxPeople = layout.findViewById(R.id.et_meeting_max_people);
        etDuration.setText(preferenceVM.getMeetingDuration().getValue().toString());
        etMaxPeople.setText(preferenceVM.getMeetingMaxPeople().getValue().toString());
        ((TextView) layout.findViewById(R.id.tv_language)).setText(String.format("%s-%s", Locale.getDefault().getLanguage(), Locale.getDefault().getCountry()).toLowerCase());
        new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setPositiveButton(R.string.cmm_confirm, (dialog, which) -> {
                    preferenceVM.getMeetingDuration().setValue(Integer.valueOf(etDuration.getText().toString()));
                    preferenceVM.getMeetingMaxPeople().setValue(Integer.valueOf(etMaxPeople.getText().toString()));
                })
                .setNegativeButton(R.string.cmm_cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean checkInput() {
        boolean ret = binding.aetRoomName.check();
        if (!ret) return false;
        ret = binding.aetName.check();
        return ret;
    }

    private void enterRoom() {

    }

    private void initSaveConfigView() {
        binding.aetRoomName.setText(roomVM.configInfo.roomName);
        binding.aetRoomPwd.setText(roomVM.configInfo.roomPwd);
        binding.aetName.setText(roomVM.configInfo.userName);
    }

    private void saveConfigInfo() {
        roomVM.configInfo.roomName = binding.aetRoomName.getText();
        roomVM.configInfo.roomPwd = binding.aetRoomPwd.getText();
        roomVM.configInfo.userName = binding.aetName.getText();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity().getWindow().getDecorView().setKeepScreenOn(false);
        binding.setViewModel(preferenceVM);
        roomVM.roomModel.observe(getViewLifecycleOwner(), roomModel -> {
            binding.btnEnter.showButtonText();
            if (!roomModel.hasJoined()) {
                return;
            }
            ((MeetingActivity) requireActivity()).navigateToRoomPage(requireView(), roomModel.roomId, roomModel.roomIndex);
            if (mEnterLoadingDialog != null) {
                mEnterLoadingDialog.dismiss();
            }
        });
        preferenceVM.getCameraFront().observe(getViewLifecycleOwner(), enable -> {
            MeetingApplication.getMeetingEngine().setDefaultCameraFont(enable);
        });
        MeetingApplication.getMeetingEngine().enableNetQualityCheck(new RtcNetworkMonitor.OnNetQualityChangeListener() {
            @Override
            public void onNetQualityChanged(int quality) {
                MenuItem item = binding.toolbar.getMenu().findItem(R.id.menu_signal);
                int drawableId;
                switch (quality) {
                    case DeviceNetQuality.GOOD:
                        drawableId = R.drawable.ic_signal_good;
                        break;
                    case DeviceNetQuality.POOR:
                        drawableId = R.drawable.ic_signal_poor;
                        break;
                    case DeviceNetQuality.BAD:
                        drawableId = R.drawable.ic_signal_bad;
                        break;
                    case DeviceNetQuality.IDLE:
                    default:
                        drawableId = R.drawable.ic_signal_idle;
                        break;
                }
//                item.setIcon(drawableId);
            }

            @Override
            public void onError(Throwable error) {
                ToastUtil.showShort(error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        saveConfigInfo();
        KeyboardUtil.hideInput(requireActivity());
        super.onDestroyView();
        MeetingApplication.getMeetingEngine().disableNetQualityCheck();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_login, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.aetName.postDelayed(this::readMeetingFromClipboard, 500);

        String token = SpUtils.getString(requireContext(), Constant.SP.TOKEN, "");
        binding.roomListRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        mRoomAdapter = new RoomRecyclerAdapter();
        mRoomAdapter.setOnRoomClickListener((room -> {
            mEnterLoadingDialog = new LoadingDialog(requireContext());
            mEnterLoadingDialog.show();
            getRoomEnter(token, room.index);
        }));
        binding.roomListRv.setAdapter(mRoomAdapter);
        verifyToken(token);
    }

    private void verifyToken(String token) {
        MeetingService meetingService = RetrofitManager.instance().getService(Constant.MEETING_URL, MeetingService.class);
        meetingService.verifyToken(new VerifyTokenReq(token))
                .enqueue(new BaseCallback<>(data -> {
                    preferenceVM.getUsername().setValue(data.name);
                    getRoomList(token);
                }, throwable -> {
                    Toast.makeText(requireContext(), "Token校验失败了~", Toast.LENGTH_SHORT).show();
                    SpUtils.delAll(requireContext());
                    ((MeetingActivity) requireActivity()).backToLoginFragment(requireView());
                }));
    }

    private void getRoomList(String token) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                MeetingService meetingService = RetrofitManager.instance().getService(Constant.MEETING_URL, MeetingService.class);
                meetingService.roomList(new VerifyTokenReq(token))
                        .enqueue(new BaseCallback<>(data -> mRoomAdapter.setRoomList(data),
                                throwable -> Toast.makeText(requireContext(), "房间列表获取失败", Toast.LENGTH_SHORT).show()));
            }
        }, 0, 2000);
    }

    private void getRoomEnter(String token, int roomIndex) {
        MeetingService meetingService = RetrofitManager.instance().getService(Constant.MEETING_URL, MeetingService.class);
        meetingService.roomEnter(new RoomStatusReq(token, roomIndex))
                .enqueue(new BaseCallback<>(data -> roomVM.enter(
                        data.id,
                        preferenceVM.getUsername().getValue(),
                        data.pass,
                        binding.swMic.isChecked() && AndPermission.hasPermissions(this, Permission.RECORD_AUDIO),
                        binding.swCamera.isChecked() && AndPermission.hasPermissions(this, Permission.CAMERA),
                        preferenceVM.getMeetingDuration().getValue(),
                        preferenceVM.getMeetingMaxPeople().getValue(),
                        roomIndex
                ),
                        throwable -> Toast.makeText(requireContext(), "房间列表获取失败", Toast.LENGTH_SHORT).show()));
    }

    private void readMeetingFromClipboard() {
        if (getContext() == null || binding == null) {
            return;
        }
        // 用于解析的语言资源
        Resources resource = null;
        String roomInfo = "";
        Locale[] locales = new Locale[]{
                StringUtil.LOCALE_DEFAULT, StringUtil.LOCALE_EN, StringUtil.LOCALE_ZH
        };
        for (Locale locale : locales) {
            resource = StringUtil.getLocalResource(requireContext(), locale);
            if (resource == null) {
                continue;
            }
            roomInfo = ClipboardUtil.readFromClipboard(requireContext(), resource.getString(R.string.invite_meeting_name, ""));
            if (!TextUtils.isEmpty(roomInfo)) {
                break;
            }
        }

        // 不存在会议信息
        if (resource == null || TextUtils.isEmpty(roomInfo)) {
            return;
        }

        String roomName = StringUtil.parseString(resource, R.string.invite_meeting_name, roomInfo);
        String roomPwd = StringUtil.parseString(resource, R.string.invite_meeting_pwd, roomInfo);

        if (TextUtils.isEmpty(binding.aetRoomName.getText())
                && TextUtils.isEmpty(binding.aetRoomPwd.getText())) {
            fillMeetingEt(roomName, roomPwd);
        }
    }

    private void fillMeetingEt(String roomName, String roomPwd) {
        binding.aetRoomName.setText(roomName);
        binding.aetRoomPwd.setText(roomPwd);
    }
}
