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

import com.aerospike.client.IAerospikeClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.utils.HasNamespaceProperty;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Aerospike support.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
// match only if we do not have reactive client
// we want sync context to be loaded when only sync client is on classpath
@ConditionalOnMissingClass("com.aerospike.client.reactor.IAerospikeReactorClient")
@ConditionalOnClass({IAerospikeClient.class, AerospikeRepository.class})
@Conditional(HasNamespaceProperty.class)
@EnableConfigurationProperties(AerospikeDataProperties.class)
@AutoConfigureAfter({AerospikeAutoConfiguration.class})
@Import(AerospikeDataConfiguration.class)
public class AerospikeDataAutoConfiguration {
}
