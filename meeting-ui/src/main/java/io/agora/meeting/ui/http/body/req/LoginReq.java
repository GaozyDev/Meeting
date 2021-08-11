package io.agora.meeting.ui.http.body.req;

import io.agora.meeting.core.annotaion.Keep;
import io.agora.meeting.ui.Constant;

@Keep
public final class LoginReq {
    public String project = Constant.PROJECT_ID;
    public String username;
    public String password;

    public LoginReq(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
