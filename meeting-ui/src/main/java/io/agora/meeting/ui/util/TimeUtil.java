package io.agora.meeting.ui.util;

import java.util.Formatter;
import java.util.Locale;

import io.agora.meeting.core.utils.TimeSyncUtil;

public class TimeUtil {
    public static String stringForTimeHMS(long timeMS, String formatStrHMS) {
        long timeS = timeMS / 1000;
        long seconds = timeS % 60;
        long minutes = timeS / 60 % 60;
        long hours = timeS / 3600;
        return new Formatter(new StringBuffer(), Locale.getDefault())
                .format(formatStrHMS, hours, minutes, seconds).toString();
    }

    public static long getSyncCurrentTimeMillis(){
        return TimeSyncUtil.getSyncCurrentTimeMillis();
    }

}
