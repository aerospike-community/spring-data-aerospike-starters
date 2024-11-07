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
import com.aerospike.client.IAerospikeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import org.springframework.data.aerospike.repository.support.AerospikeRepositoryFactoryBean;

import java.util.Arrays;
import java.util.Collection;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Aerospike Repositories.
 *
 * @author Igor Ermolenko
 */
@AutoConfiguration
@ConditionalOnClass({IAerospikeClient.class, ReactiveAerospikeRepository.class})
@ConditionalOnRepositoryType(store = "aerospike", type = RepositoryType.IMPERATIVE)
@ConditionalOnMissingBean(AerospikeRepositoryFactoryBean.class)
@Import(AerospikeRepositoriesRegistrar.class)
public class AerospikeRepositoriesAutoConfiguration {

}
