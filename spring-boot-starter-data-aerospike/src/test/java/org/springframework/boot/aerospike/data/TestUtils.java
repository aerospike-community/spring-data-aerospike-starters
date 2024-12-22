package org.springframework.boot.aerospike.data;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class TestUtils {

    public static <T> T getField(Object object, String fieldName) {
        Field typeKeyField = ReflectionUtils.findField(object.getClass(), fieldName);
        typeKeyField.setAccessible(true);
        return (T) ReflectionUtils.getField(typeKeyField, object);
    }
}
