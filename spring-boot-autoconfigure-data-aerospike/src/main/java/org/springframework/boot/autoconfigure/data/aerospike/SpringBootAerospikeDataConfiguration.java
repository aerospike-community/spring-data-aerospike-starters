/*
 * Copyright 2019 the original author or authors.
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

package org.springframework.boot.autoconfigure.data.aerospike;

import com.aerospike.client.Host;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;

import java.util.Collection;
import java.util.Collections;

/**
 * Configure Spring Data's Aerospike support.
 *
 * @author Igor Ermolenko
 */
@Configuration
@ConditionalOnMissingBean(AbstractAerospikeDataConfiguration.class)
public class SpringBootAerospikeDataConfiguration extends AbstractAerospikeDataConfiguration {

    private final AerospikeDataProperties properties;


    SpringBootAerospikeDataConfiguration(AerospikeDataProperties properties) {
        this.properties = properties;
    }

    @Override
    protected Collection<Host> getHosts() {
        return Host.parseServiceHosts(properties.getHosts());
    }

    @Override
    protected String nameSpace() {
        return properties.getNamespace();
    }
}
