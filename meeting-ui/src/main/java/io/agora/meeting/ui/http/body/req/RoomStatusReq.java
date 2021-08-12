package io.agora.meeting.ui.http.body.req;

import io.agora.meeting.core.annotaion.Keep;

/**
 * Description:
 *
 * @since 3/10/21
 */
@Keep
public final class RoomStatusReq {

    public String token;
    public int index;

    public RoomStatusReq(String token, int index) {
        this.token = token;
        this.index = index;
    }
}
