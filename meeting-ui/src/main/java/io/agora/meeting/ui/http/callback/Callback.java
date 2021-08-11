package io.agora.meeting.ui.http.callback;

import androidx.annotation.Nullable;

public interface Callback<T> {
    void onSuccess(@Nullable T res);
}
