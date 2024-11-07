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

package org.springframework.boot.autoconfigure.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.reactor.AerospikeReactorClient;
import com.aerospike.client.reactor.IAerospikeReactorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.utils.NoNamespaceProperty;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import reactor.core.publisher.Flux;

import java.util.Optional;

import static org.springframework.boot.autoconfigure.utils.AerospikeConfigurationUtils.getClientPolicyConfig;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Aerospike client.
 * Loaded only when no namespace property is given.
 *
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
@Conditional(NoNamespaceProperty.class)
@AutoConfigureAfter({AerospikeAutoConfiguration.class})
@Slf4j
public class AerospikeClientConfiguration {

    @Bean(name = "aerospikeClient", destroyMethod = "close")
    @ConditionalOnMissingBean(IAerospikeClient.class)
    public IAerospikeClient aerospikeClient(AerospikeProperties properties,
                                            ClientPolicy aerospikeClientPolicy) {
        log.info("Initializing only Aerospike Java client due to no namespace property given");
        Host[] hosts = Host.parseHosts(properties.getHosts(), properties.getDefaultPort());
        return new AerospikeClient(aerospikeClientPolicy, hosts);
    }


    @ConditionalOnClass({IAerospikeReactorClient.class, Flux.class})
    public static class AerospikeReactiveAutoConfiguration {

        @Bean(name = "aerospikeReactorClient", destroyMethod = "")
        @ConditionalOnMissingBean
        // disable destroy method, because we do not want AerospikeReactorClient to close AerospikeClient
        public IAerospikeReactorClient aerospikeReactorClient(IAerospikeClient aerospikeClient) {
            return new AerospikeReactorClient(aerospikeClient);
        }
    }

    @ConditionalOnMissingBean
    public ClientPolicy aerospikeClientPolicy(AerospikeProperties properties) {
        return getClientPolicyConfig(new ClientPolicy(), properties);
    }
}
