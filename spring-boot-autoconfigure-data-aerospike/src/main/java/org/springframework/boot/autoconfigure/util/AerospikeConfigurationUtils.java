/*
 * Copyright 2025 the original author or authors.
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

package org.springframework.boot.autoconfigure.util;

import com.aerospike.client.Host;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeDataProperties;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class AerospikeConfigurationUtils {

    public static List<Host> getClientHosts(AerospikeProperties properties) {
        if (properties.getHosts() != null) {
            Host[] hosts = Host.parseHosts(properties.getHosts(), properties.getDefaultPort());
            return Arrays.stream(hosts).toList();
        }
        return null;
    }

    public static ClientPolicy getClientPolicyConfig(ClientPolicy clientPolicy, AerospikeProperties properties) {
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

        clientPolicy.infoPolicyDefault = setupInfoPolicy(properties);
        clientPolicy.readPolicyDefault = setupReadPolicy(properties);
        clientPolicy.writePolicyDefault = setupWritePolicy(properties);
        clientPolicy.batchPolicyDefault = setupBatchPolicy(properties);
        clientPolicy.queryPolicyDefault = setupQueryPolicy(properties);
        clientPolicy.batchWritePolicyDefault = setupBatchWritePolicy(properties);
        clientPolicy.batchDeletePolicyDefault = setupBatchDeletePolicy(properties);
        clientPolicy.batchUDFPolicyDefault = setupBatchUDFPolicy(properties);
        return clientPolicy;
    }

    private static InfoPolicy setupInfoPolicy(AerospikeProperties properties) {
        AerospikeProperties.InfoPolicyDefault infoPolicyDefault = properties.getInfo();
        InfoPolicy infoPolicy = new InfoPolicy();
        if (infoPolicyDefault.timeout != null) infoPolicy.timeout = (int) infoPolicyDefault.timeout.toMillis();
        return infoPolicy;
    }

    private static WritePolicy setupWritePolicy(AerospikeProperties properties) {
        AerospikeProperties.WritePolicyDefault writePolicyDefault = properties.getWrite();
        WritePolicy policy = new WritePolicy();
        setGeneralPolicyProperties(policy, writePolicyDefault);
        whenPresent(writePolicyDefault.durableDelete, p -> policy.durableDelete = p);
        return policy;
    }

    private static Policy setupReadPolicy(AerospikeProperties properties) {
        AerospikeProperties.ReadPolicyDefault readPolicyDefault = properties.getRead();
        Policy policy = new Policy();
        setGeneralPolicyProperties(policy, readPolicyDefault);
        return policy;
    }

    private static BatchPolicy setupBatchPolicy(AerospikeProperties properties) {
        AerospikeProperties.BatchPolicyDefault batchPolicyDefault = properties.getBatch();
        BatchPolicy policy = new BatchPolicy();
        setGeneralPolicyProperties(policy, batchPolicyDefault);
        whenPresent(batchPolicyDefault.maxConcurrentThreads, p -> policy.maxConcurrentThreads = p);
        whenPresent(batchPolicyDefault.allowInline, p -> policy.allowInline = p);
        whenPresent(batchPolicyDefault.sendSetName, p -> policy.sendSetName = p);
        return policy;
    }

    private static BatchWritePolicy setupBatchWritePolicy(AerospikeProperties properties) {
        AerospikeProperties.BatchWritePolicyDefault batchWritePolicyDefault = properties.getBatchWrite();
        BatchWritePolicy policy = new BatchWritePolicy();
        whenPresent(batchWritePolicyDefault.durableDelete, p -> policy.durableDelete = p);
        return policy;
    }

    private static BatchDeletePolicy setupBatchDeletePolicy(AerospikeProperties properties) {
        AerospikeProperties.BatchDeletePolicyDefault batchDeletePolicyDefault = properties.getBatchDelete();
        BatchDeletePolicy policy = new BatchDeletePolicy();
        whenPresent(batchDeletePolicyDefault.durableDelete, p -> policy.durableDelete = p);
        return policy;
    }

    private static BatchUDFPolicy setupBatchUDFPolicy(AerospikeProperties properties) {
        AerospikeProperties.BatchUDFPolicyDefault batchUDFPolicyDefault = properties.getBatchUdf();
        BatchUDFPolicy policy = new BatchUDFPolicy();
        whenPresent(batchUDFPolicyDefault.durableDelete, p -> policy.durableDelete = p);
        return policy;
    }

    private static QueryPolicy setupQueryPolicy(AerospikeProperties properties) {
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

    public static EventLoops setupEventLoops(AerospikeProperties.EventLoopsProperties eventLoopsProperties) {
        EventPolicy eventPolicy = new EventPolicy();
        whenPresent(eventLoopsProperties.maxCommandsInProcess, p -> eventPolicy.maxCommandsInProcess = p);
        whenPresent(eventLoopsProperties.maxCommandsInQueue, p -> eventPolicy.maxCommandsInQueue = p);
        whenPresent(eventLoopsProperties.queueInitialCapacity, p -> eventPolicy.queueInitialCapacity = p);
        whenPresent(eventLoopsProperties.minTimeout, p -> eventPolicy.minTimeout = p);
        whenPresent(eventLoopsProperties.ticksPerWheel, p -> eventPolicy.ticksPerWheel = p);
        whenPresent(eventLoopsProperties.commandsPerEventLoop, p -> eventPolicy.commandsPerEventLoop = p);
        return new NettyEventLoops(eventPolicy, chooseLoopGroup(eventLoopsProperties));
    }

    private static EventLoopGroup chooseLoopGroup(AerospikeProperties.EventLoopsProperties eventLoopsProperties) {
        int threadsNumber = Math.max(eventLoopsProperties.getThreads(), 0);

        if (!StringUtils.hasText(eventLoopsProperties.groupType)) {
            log.info("Proceeding with standard EventLoops group type 'NioEventLoopGroup'");
            return new NioEventLoopGroup(threadsNumber);
        }

        return switch (eventLoopsProperties.groupType.toLowerCase()) {
            case "epolleventloopgroup" -> new EpollEventLoopGroup(threadsNumber);
            case "kqueueeventloopgroup" -> new KQueueEventLoopGroup(threadsNumber);
            case "nioeventloopgroup" -> new NioEventLoopGroup(threadsNumber);
            default -> {
                log.warn("Unexpected EventLoops group type '{}', proceeding with 'NioEventLoopGroup' instead",
                        eventLoopsProperties.groupType);
                yield new NioEventLoopGroup(threadsNumber);
            }
        };
    }

    private static void setGeneralPolicyProperties(Policy policy, AerospikeProperties.PolicyDefault policyDefault) {
        whenPresent(policyDefault.socketTimeout, p -> policy.socketTimeout = (int) p.toMillis());
        whenPresent(policyDefault.totalTimeout, p -> policy.totalTimeout = (int) p.toMillis());
        whenPresent(policyDefault.timeoutDelay, p -> policy.timeoutDelay = (int) p.toMillis());
        whenPresent(policyDefault.maxRetries, p -> policy.maxRetries = p);
        whenPresent(policyDefault.sleepBetweenRetries, p -> policy.sleepBetweenRetries = (int) p.toMillis());
        whenPresent(policyDefault.sendKey, p -> policy.sendKey = p);
    }

    private static <T> void whenPresent(T param, Consumer<T> consumer) {
        if (param != null)
            consumer.accept(param);
    }

    public static String getNamespace(AerospikeDataProperties dataProperties) {
        if (dataProperties.getNamespace() != null) {
            return dataProperties.getNamespace();
        }
        return null;
    }

    public static void getDataSettings(AerospikeDataProperties dataProperties,
                                       AerospikeDataSettings aerospikeDataSettings) {
        whenPresent(dataProperties.isScansEnabled(), aerospikeDataSettings::setScansEnabled);
        whenPresent(dataProperties.isCreateIndexesOnStartup(), aerospikeDataSettings::setCreateIndexesOnStartup);
        whenPresent(dataProperties.getIndexCacheRefreshSeconds(), aerospikeDataSettings::setIndexCacheRefreshSeconds);
        whenPresent(dataProperties.getServerVersionRefreshSeconds(), aerospikeDataSettings::setServerVersionRefreshSeconds);
        whenPresent(dataProperties.getQueryMaxRecords(), aerospikeDataSettings::setQueryMaxRecords);
        whenPresent(dataProperties.getBatchWriteSize(), aerospikeDataSettings::setBatchWriteSize);
        whenPresent(dataProperties.isKeepOriginalKeyTypes(), aerospikeDataSettings::setKeepOriginalKeyTypes);
    }
}
