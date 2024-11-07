package org.springframework.boot.autoconfigure.utils;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class NoNamespaceProperty implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String namespace = context.getEnvironment().getProperty("spring.data.aerospike.namespace");
        return namespace == null || namespace.isEmpty();
    }
}
