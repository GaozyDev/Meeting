package io.agora.meeting.ui.http.body.req;

import io.agora.meeting.core.annotaion.Keep;

/**
 * Description:
 *
 * @since 3/10/21
 */
@Keep
public final class VerifyTokenReq {

    public String token;

    public VerifyTokenReq(String token) {
        this.token = token;
    }
}
