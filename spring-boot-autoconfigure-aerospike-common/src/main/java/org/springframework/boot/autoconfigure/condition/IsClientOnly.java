package org.springframework.boot.autoconfigure.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport.CONFIG_PREFIX_DATA;

public class IsClientOnly implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String strIsClientOnly = context.getEnvironment().getProperty("starters.aerospike.client-only");
        String namespace = context.getEnvironment().getProperty(CONFIG_PREFIX_DATA + ".namespace");
        boolean isClientOnly = strIsClientOnly != null && strIsClientOnly.equalsIgnoreCase("true");
        boolean hasNamespace = namespace != null && !namespace.isEmpty();
        // if the explicit client-only property is true, no need to check for namespace
        return isClientOnly || !hasNamespace;
    }
}
