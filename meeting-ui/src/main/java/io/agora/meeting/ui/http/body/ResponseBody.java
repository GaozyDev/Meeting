package io.agora.meeting.ui.http.body;

import io.agora.meeting.core.annotaion.Keep;
import io.agora.meeting.ui.http.network.BaseResponse;

@Keep
public class ResponseBody<T> extends BaseResponse {
    public T P;

    @Override
    public String toString() {
        return "{" +
                ", \"E\"=" + E +
                ", \"P\"=" + P +
                '}';
    }
}
