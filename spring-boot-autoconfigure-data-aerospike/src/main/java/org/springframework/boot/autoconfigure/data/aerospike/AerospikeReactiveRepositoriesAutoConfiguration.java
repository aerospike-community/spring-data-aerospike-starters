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

import com.aerospike.client.reactor.AerospikeReactorClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import org.springframework.data.aerospike.repository.support.ReactiveAerospikeRepositoryFactoryBean;
import reactor.core.publisher.Flux;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Aerospike Reactive Repositories.
 *
 * @author Igor Ermolenko
 */
@AutoConfiguration
@ConditionalOnClass({AerospikeReactorClient.class, ReactiveAerospikeRepository.class, Flux.class})
@ConditionalOnRepositoryType(store = "aerospike", type = RepositoryType.REACTIVE)
@ConditionalOnMissingBean(ReactiveAerospikeRepositoryFactoryBean.class)
@Import(AerospikeReactiveRepositoriesRegistrar.class)
public class AerospikeReactiveRepositoriesAutoConfiguration {

}
