package org.springframework.boot.client.reactive.autoconfigure;

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

import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.reactor.AerospikeReactorClient;
import com.aerospike.client.reactor.IAerospikeReactorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.IsClientOnly;
import org.springframework.boot.client.autoconfigure.AerospikeClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import reactor.core.publisher.Flux;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Aerospike Reactor client.
 * Loaded only for client-only modules or when no namespace property is given.
 *
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
@Conditional(IsClientOnly.class)
@ConditionalOnClass({IAerospikeClient.class, IAerospikeReactorClient.class, Flux.class})
@AutoConfigureAfter({AerospikeClientConfiguration.class})
@Slf4j
public class AerospikeReactorClientConfiguration {

    @Bean(name = "aerospikeReactorClient", destroyMethod = "")
    @ConditionalOnMissingBean
    // disable destroy method, because we do not want AerospikeReactorClient to close AerospikeClient
    public IAerospikeReactorClient aerospikeReactorClient(IAerospikeClient aerospikeClient) {
        log.info("Initializing Aerospike Reactor Java client");
        return new AerospikeReactorClient(aerospikeClient);
    }
}