package io.agora.meeting.ui.http;

import java.util.List;

import io.agora.meeting.ui.http.body.ResponseBody;
import io.agora.meeting.ui.http.body.req.LoginReq;
import io.agora.meeting.ui.http.body.req.RoomEnterReq;
import io.agora.meeting.ui.http.body.req.VerifyTokenReq;
import io.agora.meeting.ui.http.body.resp.RoomEnterResp;
import io.agora.meeting.ui.http.body.resp.RoomListResp;
import io.agora.meeting.ui.http.body.resp.VerifyTokenResp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface MeetingService {

    @POST("/api/sso/accountLogin")
    Call<ResponseBody<String>> accountLogin(
            @Body LoginReq body
    );

    @POST("/user/verifyToken")
    Call<ResponseBody<VerifyTokenResp>> verifyToken(
            @Body VerifyTokenReq body
    );

    @PUT("/room/list")
    Call<ResponseBody<List<RoomListResp>>> roomList(
            @Body VerifyTokenReq body
    );

    @PUT("/room/enter")
    Call<ResponseBody<RoomEnterResp>> roomEnter(
            @Body RoomEnterReq body
    );
}
