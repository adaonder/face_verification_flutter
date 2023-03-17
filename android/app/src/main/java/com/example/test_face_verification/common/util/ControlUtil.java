package com.example.test_face_verification.common.util;

import java.util.List;
import java.util.Set;

public class ControlUtil {
    public static boolean isData(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isData(Set<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isNullData(List<?> list) {
        return list == null || list.size() == 0;
    }
}
