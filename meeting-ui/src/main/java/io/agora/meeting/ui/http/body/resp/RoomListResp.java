package io.agora.meeting.ui.http.body.resp;

import io.agora.meeting.core.annotaion.Keep;

@Keep
public final class RoomListResp {

    public int index;

    public int users;

    @Override
    public String toString() {
        return "RoomListResp{" +
                "index=" + index +
                ", users=" + users +
                '}';
    }
}
