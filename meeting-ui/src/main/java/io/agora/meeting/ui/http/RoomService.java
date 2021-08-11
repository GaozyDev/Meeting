package io.agora.meeting.ui.http;

import io.agora.meeting.ui.http.body.ResponseBody;
import io.agora.meeting.ui.http.body.req.LoginReq;
import io.agora.meeting.ui.http.body.req.RoomUpdateReq;
import io.agora.meeting.ui.http.body.resp.LoginResp;
import io.agora.meeting.ui.http.body.resp.NullResp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Description:
 * 房间接口
 *
 *
 * @since 2/3/21
 */
public interface RoomService {

    @POST("/scenario/meeting/apps/{appId}/v2/rooms/{roomId}/join")
    Call<ResponseBody<LoginResp>> join(
            @Path("appId") String appId,
            @Path("roomId") String roomId,
            @Body LoginReq body
            );

    @POST("/scenario/meeting/apps/{appId}/v2/rooms/{roomId}/users/{userId}/leave")
    Call<ResponseBody<NullResp>> leave(
            @Path("appId") String appId,
            @Path("roomId") String roomId,
            @Path("userId") String userId
    );


    @PUT("/scenario/meeting/apps/{appId}/v2/rooms/{roomId}")
    Call<ResponseBody<NullResp>> updateRoomInfo(
            @Path("appId") String appId,
            @Path("roomId") String roomId,
            @Body RoomUpdateReq body
    );

    @POST("/scenario/meeting/apps/{appId}/v2/rooms/{roomId}/users/{userId}/endRoom")
    Call<ResponseBody<NullResp>> close(
            @Path("appId") String appId,
            @Path("roomId") String roomId,
            @Path("userId") String userId
    );


}
