package io.agora.meeting.ui.http;

import java.util.List;

import io.agora.meeting.ui.http.body.ResponseBody;
import io.agora.meeting.ui.http.body.req.LoginReq;
import io.agora.meeting.ui.http.body.req.RoomStatusReq;
import io.agora.meeting.ui.http.body.req.VerifyTokenReq;
import io.agora.meeting.ui.http.body.resp.RoomEnterResp;
import io.agora.meeting.ui.http.body.resp.RoomListResp;
import io.agora.meeting.ui.http.body.resp.VerifyTokenResp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MeetingService {

    @POST("/api/sso/accountLogin")
    Call<ResponseBody<String>> accountLogin(@Body LoginReq body);

    @POST("/user/verifyToken")
    Call<ResponseBody<VerifyTokenResp>> verifyToken(@Body VerifyTokenReq body);

    @POST("/room/list")
    Call<ResponseBody<List<RoomListResp>>> roomList(@Body VerifyTokenReq body);

    @POST("/room/enter")
    Call<ResponseBody<RoomEnterResp>> roomEnter(@Body RoomStatusReq body);

    @POST("/room/close")
    Call<ResponseBody<Object>> closeMeeting(@Body RoomStatusReq roomStatusReq);

    @POST("/room/leave")
    Call<ResponseBody<Object>> exitMeeting(@Body RoomStatusReq roomStatusReq);
}
