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
import com.aerospike.client.async.NioEventLoops;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.reactor.AerospikeReactorClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Aerospike client.
 *
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
@ConditionalOnClass(AerospikeClient.class)
@ConditionalOnProperty("spring.aerospike.hosts")
@EnableConfigurationProperties(AerospikeProperties.class)
public class AerospikeAutoConfiguration {

    @Bean(name = "aerospikeClient", destroyMethod = "close")
    @ConditionalOnMissingBean(IAerospikeClient.class)
    public AerospikeClient aerospikeClient(AerospikeProperties properties,
                                           ClientPolicy aerospikeClientPolicy) {
        Host[] hosts = Host.parseHosts(properties.getHosts(), properties.getDefaultPort());
        return new AerospikeClient(aerospikeClientPolicy, hosts);
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
        whenPresent(properties.getConnectTimeout(), p -> clientPolicy.timeout = (int) p.toMillis());
        whenPresent(properties.getLoginTimeout(), p -> clientPolicy.loginTimeout = (int) p.toMillis());
        whenPresent(properties.getMinConnsPerNode(), p -> clientPolicy.minConnsPerNode = p);
        whenPresent(properties.getMaxConnsPerNode(), p -> clientPolicy.maxConnsPerNode = p);
        whenPresent(properties.getConnPoolsPerNode(), p -> clientPolicy.connPoolsPerNode = p);
        whenPresent(properties.getMaxSocketIdle(), p -> clientPolicy.maxSocketIdle = (int) p.getSeconds());
        whenPresent(properties.getTendInterval(), p -> clientPolicy.tendInterval = (int) p.toMillis());
        whenPresent(properties.getFailIfNotConnected(), p -> clientPolicy.failIfNotConnected = p);

        clientPolicy.readPolicyDefault = setupReadPolicy(properties);
        clientPolicy.writePolicyDefault = setupWritePolicy(properties);
        clientPolicy.batchPolicyDefault = setupBatchPolicy(properties);
        clientPolicy.queryPolicyDefault = setupQueryPolicy(properties);
        aerospikeEventLoops.ifPresent(loops -> clientPolicy.eventLoops = loops);

        return clientPolicy;
    }

    @ConditionalOnClass({AerospikeReactorClient.class, Flux.class})
    public static class AerospikeReactiveAutoConfiguration {

        @Bean(name = "aerospikeReactorClient", destroyMethod = "")
        @ConditionalOnMissingBean
        //disable destroy method, because we do not want AerospikeReactorClient to close AerospikeClient
        public AerospikeReactorClient aerospikeReactorClient(IAerospikeClient aerospikeClient,
                                                             EventLoops eventLoops) {
            return new AerospikeReactorClient(aerospikeClient, eventLoops);
        }

        @Bean(name = "aerospikeEventLoops", destroyMethod = "close")
        @ConditionalOnMissingBean
        public EventLoops aerospikeEventLoops() {
            return new NioEventLoops();
        }
    }

    private WritePolicy setupWritePolicy(AerospikeProperties properties) {
        AerospikeProperties.WritePolicyDefault writePolicyDefault = properties.getWrite();
        WritePolicy policy = new WritePolicy();
        setGeneralPolicyProperties(policy, writePolicyDefault);
        whenPresent(writePolicyDefault.durableDelete, p -> policy.durableDelete = p);
        whenPresent(writePolicyDefault.sendKey, p -> policy.sendKey = p);
        return policy;
    }

    private Policy setupReadPolicy(AerospikeProperties properties) {
        AerospikeProperties.ReadPolicyDefault readPolicyDefault = properties.getRead();
        Policy policy = new Policy();
        setGeneralPolicyProperties(policy, readPolicyDefault);
        return policy;
    }

    private BatchPolicy setupBatchPolicy(AerospikeProperties properties) {
        AerospikeProperties.BatchPolicyDefault batchPolicyDefault = properties.getBatch();
        BatchPolicy policy = new BatchPolicy();
        setGeneralPolicyProperties(policy, batchPolicyDefault);
        whenPresent(batchPolicyDefault.maxConcurrentThreads, p -> policy.maxConcurrentThreads = p);
        whenPresent(batchPolicyDefault.allowInline, p -> policy.allowInline = p);
        whenPresent(batchPolicyDefault.sendSetName, p -> policy.sendSetName = p);
        return policy;
    }

    private QueryPolicy setupQueryPolicy(AerospikeProperties properties) {
        AerospikeProperties.QueryPolicyDefault queryPolicyDefault = properties.getQuery();
        QueryPolicy policy = new QueryPolicy();
        setGeneralPolicyProperties(policy, queryPolicyDefault);
        whenPresent(queryPolicyDefault.maxRecords, p -> policy.maxRecords = p);
        whenPresent(queryPolicyDefault.failOnClusterChange, p -> policy.failOnClusterChange = p);
        whenPresent(queryPolicyDefault.includeBinData, p -> policy.includeBinData = p);
        whenPresent(queryPolicyDefault.maxConcurrentNodes, p -> policy.maxConcurrentNodes = p);
        whenPresent(queryPolicyDefault.recordQueueSize, p -> policy.recordQueueSize = p);
        return policy;
    }

    private void setGeneralPolicyProperties(Policy policy, AerospikeProperties.PolicyDefault policyDefault) {
        whenPresent(policyDefault.socketTimeout, p -> policy.socketTimeout = (int) p.toMillis());
        whenPresent(policyDefault.totalTimeout, p -> policy.totalTimeout = (int) p.toMillis());
        whenPresent(policyDefault.timeoutDelay, p -> policy.timeoutDelay = (int) p.toMillis());
        whenPresent(policyDefault.maxRetries, p -> policy.maxRetries = p);
        whenPresent(policyDefault.sleepBetweenRetries, p -> policy.sleepBetweenRetries = (int) p.toMillis());
    }

    private <T> void whenPresent(T param, Consumer<T> consumer) {
        if (param != null)
            consumer.accept(param);
    }
}
