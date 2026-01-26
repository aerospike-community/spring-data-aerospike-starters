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

package org.springframework.boot.aerospike.reactive.data;

import com.aerospike.client.Host;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.policy.ClientPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeDataProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;

import java.util.Collection;

import static org.springframework.boot.autoconfigure.util.AerospikeConfigurationUtils.*;

/**
 * Configure Spring Data's Aerospike support.
 * Imported only when namespace property is given.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@Slf4j
@Configuration
public class  AerospikeReactiveDataConfiguration extends AbstractReactiveAerospikeDataConfiguration {

    @Autowired
    private AerospikeProperties properties;
    @Autowired
    private AerospikeDataProperties dataProperties;

    @Override
    protected Collection<Host> getHosts() {
        return getClientHosts(properties);
    }

    @Override
    protected String nameSpace() {
        return getNamespace(dataProperties);
    }

    @Override
    public EventLoops eventLoops() {
        return setupEventLoops(properties.getEventLoops());
    }

    @Override
    protected ClientPolicy getClientPolicy() {
        return getClientPolicyConfig(super.getClientPolicy(), properties);
    }

    @Override
    protected void configureDataSettings(AerospikeDataSettings aerospikeDataSettings) {
        getDataSettings(dataProperties, aerospikeDataSettings);
    }
}
