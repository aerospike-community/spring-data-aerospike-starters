package org.springframework.boot.autoconfigure.utils;

import com.aerospike.client.Host;
import com.aerospike.client.policy.BatchDeletePolicy;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.BatchUDFPolicy;
import com.aerospike.client.policy.BatchWritePolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeDataProperties;
import org.springframework.data.aerospike.config.AerospikeDataSettings;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class AerospikeConfigurationUtils {

    public static Collection<Host> getClientHosts(AerospikeProperties properties) {
        if (properties.getHosts() != null) {
            Host[] hosts = Host.parseHosts(properties.getHosts(), properties.getDefaultPort());
            return Arrays.stream(hosts).toList();
        } else {
            return null;
        }
    }

    public static String getNamespace(AerospikeDataProperties dataProperties) {
        if (dataProperties.getNamespace() != null) {
            return dataProperties.getNamespace();
        } else {
            return null;
        }
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

        clientPolicy.readPolicyDefault = setupReadPolicy(properties);
        clientPolicy.writePolicyDefault = setupWritePolicy(properties);
        clientPolicy.batchPolicyDefault = setupBatchPolicy(properties);
        clientPolicy.queryPolicyDefault = setupQueryPolicy(properties);
        clientPolicy.batchWritePolicyDefault = setupBatchWritePolicy(properties);
        clientPolicy.batchDeletePolicyDefault = setupBatchDeletePolicy(properties);
        clientPolicy.batchUDFPolicyDefault = setupBatchUDFPolicy(properties);
//        aerospikeEventLoops.ifPresent(loops -> clientPolicy.eventLoops = loops); // TODO

//        clientPolicy.user = "tester";
//        clientPolicy.password = "psw";

        return clientPolicy;
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
