package com.example.test_face_verification.common.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;


/*

new TypeToken<List<AssetMaintenance>>() {}.getType()

******* Bunun yerine bu yapıldı ;

TypeToken.getParameterized(listType, targetType).getType()

 */
public class DataUtil {
    public static <T> T getModelStringData(String data, Class<T> object) {
        return new Gson().fromJson(data, (Type) object);
    }
}
