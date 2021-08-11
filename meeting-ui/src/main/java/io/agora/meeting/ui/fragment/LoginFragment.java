package io.agora.meeting.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import io.agora.meeting.ui.Constant;
import io.agora.meeting.ui.MeetingActivity;
import io.agora.meeting.ui.R;
import io.agora.meeting.ui.http.BaseCallback;
import io.agora.meeting.ui.http.MeetingService;
import io.agora.meeting.ui.http.body.req.LoginReq;
import io.agora.meeting.ui.http.network.RetrofitManager;
import io.agora.meeting.ui.util.SpUtils;

public class LoginFragment extends Fragment {

    private EditText mAccountEt;
    private EditText mPwdEt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String token = SpUtils.getString(requireContext(), Constant.SP.TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            ((MeetingActivity) requireActivity()).navigateToRoomListPage(requireView());
        }
    }

    private void initView(View view) {
        mAccountEt = view.findViewById(R.id.login_et_account);
        mPwdEt = view.findViewById(R.id.login_et_password);
        String username = SpUtils.getString(requireContext(), Constant.SP.USER_NAME, "");
        String password = SpUtils.getString(requireContext(), Constant.SP.PASSWORD, "");
        mAccountEt.setText(username);
        mPwdEt.setText(password);
        view.findViewById(R.id.login_btn).setOnClickListener(v -> login());
    }

    private void login() {
        String username = mAccountEt.getText().toString();
        String pwd = mPwdEt.getText().toString();
        SpUtils.putString(requireContext(), Constant.SP.USER_NAME, username);
        SpUtils.putString(requireContext(), Constant.SP.PASSWORD, pwd);

        MeetingService meetingService = RetrofitManager.instance().getService(Constant.SSO_URL, MeetingService.class);
        meetingService.accountLogin(new LoginReq(username, pwd))
                .enqueue(new BaseCallback<>(data -> {
                    if (!TextUtils.isEmpty(data)) {
                        SpUtils.putString(requireContext(), Constant.SP.TOKEN, data);
                        ((MeetingActivity) requireActivity()).navigateToRoomListPage(requireView());
                    }
                }, throwable -> Toast.makeText(requireContext(), "噢，登录失败了~", Toast.LENGTH_SHORT).show()));

    }
}