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
