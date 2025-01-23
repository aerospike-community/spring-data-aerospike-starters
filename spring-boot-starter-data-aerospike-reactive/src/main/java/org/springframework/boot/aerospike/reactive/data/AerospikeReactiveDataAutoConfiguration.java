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

import com.aerospike.client.reactor.IAerospikeReactorClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeDataProperties;
import org.springframework.boot.autoconfigure.condition.IsClientOnly;
import org.springframework.boot.autoconfigure.condition.IsNotClientOnly;
import org.springframework.boot.client.reactive.autoconfigure.AerospikeReactorClientConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import reactor.core.publisher.Flux;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Reactive Aerospike support.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
@ConditionalOnClass({IAerospikeReactorClient.class, ReactiveAerospikeRepository.class, Flux.class})
@AutoConfigureAfter(AerospikeAutoConfiguration.class)
public class AerospikeReactiveDataAutoConfiguration {

    @Conditional(IsNotClientOnly.class)
    @EnableConfigurationProperties(AerospikeDataProperties.class)
    @Import(AerospikeReactiveDataConfiguration.class)
    static class ReactiveDataConfigurationImport {
    }

    @Conditional(IsClientOnly.class)
    // Import client configuration if 'namespace' is missing
    @Import(AerospikeReactorClientConfiguration.class)
    static class ReactiveClientConfigurationImport {
    }
}
