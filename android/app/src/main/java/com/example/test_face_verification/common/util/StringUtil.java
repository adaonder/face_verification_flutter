package com.example.test_face_verification.common.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {


    public static boolean isEmpty(CharSequence text) {
        return StringUtils.isEmpty(text) || StringUtils.isBlank(text);
    }

    public static boolean isNotEmpty(CharSequence text) {
        return !isEmpty(text);
    }

    public static boolean isNotEmptyLink(CharSequence text) {
        return isNotEmpty(text) && isLink(text.toString());
    }

    public static boolean isLink(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
