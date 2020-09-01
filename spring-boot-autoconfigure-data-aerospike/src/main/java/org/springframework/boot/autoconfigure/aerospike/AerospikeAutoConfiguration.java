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
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.NioEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.reactor.AerospikeReactorClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Aerospike client.
 *
 * @author Anastasiia Smirnova
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AerospikeClient.class)
@ConditionalOnProperty("spring.aerospike.hosts")
@EnableConfigurationProperties(AerospikeProperties.class)
public class AerospikeAutoConfiguration {

    @Bean(name = "aerospikeClient", destroyMethod = "close")
    @ConditionalOnMissingBean
    public AerospikeClient aerospikeClient(AerospikeProperties properties,
                                           ClientPolicy aerospikeClientPolicy) {
        Collection<Host> hosts = Host.parseServiceHosts(properties.getHosts());
        return new AerospikeClient(aerospikeClientPolicy, hosts.toArray(new Host[hosts.size()]));
    }

    @Bean(name = "aerospikeClientPolicy")
    @ConditionalOnMissingBean
    public ClientPolicy aerospikeClientPolicy(AerospikeProperties properties,
                                              Optional<EventLoops> aerospikeEventLoops) {
        ClientPolicy clientPolicy = new ClientPolicy();
        whenPresent(properties.getUser(), p -> clientPolicy.user = p);
        whenPresent(properties.getPassword(), p -> clientPolicy.password = p);
        whenPresent(properties.getClusterName(), p -> clientPolicy.clusterName = p);
        whenPresent(properties.getAuthMode(), p -> clientPolicy.authMode = p);
        whenPresent(properties.getTimeout(), p -> clientPolicy.timeout = (int) p.toMillis());
        whenPresent(properties.getLoginTimeout(), p -> clientPolicy.loginTimeout = (int) p.toMillis());
        whenPresent(properties.getMaxConnsPerNode(), p -> clientPolicy.maxConnsPerNode = p);
        whenPresent(properties.getConnPoolsPerNode(), p -> clientPolicy.connPoolsPerNode = p);
        whenPresent(properties.getMaxSocketIdle(), p -> clientPolicy.maxSocketIdle = (int) p.getSeconds());
        whenPresent(properties.getTendInterval(), p -> clientPolicy.tendInterval = (int) p.toMillis());
        whenPresent(properties.getFailIfNotConnected(), p -> clientPolicy.failIfNotConnected = p);

        clientPolicy.readPolicyDefault = setupReadPolicy(properties);
        clientPolicy.writePolicyDefault = setupWritePolicy(properties);
        aerospikeEventLoops.ifPresent(loops -> clientPolicy.eventLoops = loops);

        return clientPolicy;
    }

    @ConditionalOnClass({AerospikeReactorClient.class, Flux.class})
    public static class AerospikeReactiveAutoConfiguration {

        @Bean(name = "aerospikeReactorClient", destroyMethod = "")
        @ConditionalOnMissingBean
        //disable destroy method, because we do not want AerospikeReactorClient to close AerospikeClient
        public AerospikeReactorClient aerospikeReactorClient(AerospikeClient aerospikeClient,
                                                             EventLoops eventLoops) {
            return new AerospikeReactorClient(aerospikeClient, eventLoops);
        }

        @Bean(name = "aerospikeEventLoops", destroyMethod = "close")
        @ConditionalOnMissingBean
        public EventLoops aerospikeEventLoops() {
            NioEventLoops eventLoops = new NioEventLoops();
            return eventLoops;
        }
    }

    private WritePolicy setupWritePolicy(AerospikeProperties properties) {
        AerospikeProperties.WritePolicyDefault writePolicyDefault = properties.getWrite();
        WritePolicy policy = new WritePolicy();
        whenPresent(writePolicyDefault.socketTimeout, p -> policy.socketTimeout = (int) p.toMillis());
        whenPresent(writePolicyDefault.totalTimeout, p -> policy.totalTimeout = (int) p.toMillis());
        whenPresent(writePolicyDefault.timeoutDelay, p -> policy.timeoutDelay = (int) p.toMillis());
        whenPresent(writePolicyDefault.maxRetries, p -> policy.maxRetries = p);
        whenPresent(writePolicyDefault.sleepBetweenRetries, p -> policy.sleepBetweenRetries = (int) p.toMillis());
        whenPresent(writePolicyDefault.durableDelete, p -> policy.durableDelete = p);
        return policy;
    }

    private Policy setupReadPolicy(AerospikeProperties properties) {
        AerospikeProperties.ReadPolicyDefault readPolicyDefault = properties.getRead();
        Policy policy = new Policy();
        whenPresent(readPolicyDefault.socketTimeout, p -> policy.socketTimeout = (int) p.toMillis());
        whenPresent(readPolicyDefault.totalTimeout, p -> policy.totalTimeout = (int) p.toMillis());
        whenPresent(readPolicyDefault.timeoutDelay, p -> policy.timeoutDelay = (int) p.toMillis());
        whenPresent(readPolicyDefault.maxRetries, p -> policy.maxRetries = p);
        whenPresent(readPolicyDefault.sleepBetweenRetries, p -> policy.sleepBetweenRetries = (int) p.toMillis());
        return policy;
    }

    private <T> void whenPresent(T param, Consumer<T> consumer) {
        if (param != null)
            consumer.accept(param);
    }
}
