package com.example.criminalintent.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by yubin on 2017/4/26.
 */

public class DateFormatUtil {

    public static final String DATE_FORMAT = "EEE, MM - dd, yyyy";

    public static final String TIME_FORMAT = "hh : mm a";

    /**
     * 格式化日期
     * @return
     */
    public static DateFormat dateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }

    /**
     * 格式化时间
     * @return
     */
    public static DateFormat timeFormat() {
        return new SimpleDateFormat(TIME_FORMAT);
    }

}
