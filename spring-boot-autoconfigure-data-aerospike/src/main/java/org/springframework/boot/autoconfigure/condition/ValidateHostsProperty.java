/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
