package org.springframework.boot.autoconfigure.data.aerospike;

import org.springframework.data.aerospike.convert.AerospikeTypeAliasAccessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class TestUtils {

    public static <T> T getField(AerospikeTypeAliasAccessor aliasAccessor, String fieldName) {
        Field typeKeyField = ReflectionUtils.findField(AerospikeTypeAliasAccessor.class, fieldName);
        typeKeyField.setAccessible(true);
        return (T) ReflectionUtils.getField(typeKeyField, aliasAccessor);
    }
}
