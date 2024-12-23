package org.springframework.boot.autoconfigure.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.security.InvalidParameterException;

import static org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport.CONFIG_PREFIX_CONNECTION;

public class ValidateHostsProperty implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String hostsProperty = CONFIG_PREFIX_CONNECTION + ".hosts";
        String hosts = context.getEnvironment().getProperty(hostsProperty);
        boolean hasHosts = hosts != null && !hosts.isEmpty();

        if (!hasHosts) {
            throw new InvalidParameterException("Required property '" + hostsProperty + "' is missing");
        }
        return true;
    }
}
