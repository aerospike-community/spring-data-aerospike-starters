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

import com.aerospike.client.policy.AuthMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Basic configuration properties for Aerospike client.
 * For more details on each option see corresponding field documentation in {@link com.aerospike.client.policy.ClientPolicy}.
 *
 * @author Anastasiia Smirnova
 */
@Data
@ConfigurationProperties(prefix = "spring.aerospike")
public class AerospikeProperties {

    /**
     * Potential hosts to seed the cluster from string format: hostname1:port1,hostname2:port2 ... .
     * <p>
     * See {@link com.aerospike.client.Host#parseServiceHosts} documentation for more details.
     */
    private String hosts;

    /**
     * User authentication to cluster.
     */
    private String user;

    /**
     * Password authentication to cluster.
     */
    public String password;

    /**
     * Expected cluster name.
     */
    private String clusterName;

    /**
     * Authentication mode used when user/password is defined.
     */
    private AuthMode authMode;

    /**
     * Initial host connection timeout.
     */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * Login timeout.
     */
    private Duration loginTimeout;

    /**
     * Maximum number of connections allowed per server node.
     */
    private Integer maxConnsPerNode;

    /**
     * Number of synchronous connection pools used for each node.
     */
    private Integer connPoolsPerNode;

    /**
     * Maximum socket idle.
     */
    private Duration maxSocketIdle;

    /**
     * Interval between cluster tends by maintenance thread.
     */
    private Duration tendInterval;

    /**
     * Throw exception if all seed connections fail on cluster instantiation.
     */
    private Boolean failIfNotConnected = true;

    private ReadPolicyDefault read = new ReadPolicyDefault();

    private WritePolicyDefault write = new WritePolicyDefault();

    /**
     * For more details on each option see corresponding field documentation in {@link com.aerospike.client.policy.Policy}.
     */
    @Data
    public static class ReadPolicyDefault {

        /**
         * Socket idle timeout when processing a database command.
         */
        public Duration socketTimeout;

        /**
         * Total transaction timeout.
         */
        public Duration totalTimeout;

        /**
         * Delay after socket read timeout in an attempt to recover the socket
         * in the background.
         */
        public Duration timeoutDelay;

        /**
         * Maximum number of retries before aborting the current transaction.
         * The initial attempt is not counted as a retry.
         */
        public Integer maxRetries;

        /**
         * Time to sleep between retries.
         */
        public Duration sleepBetweenRetries;

    }

    /**
     * For more details on each option see corresponding field documentation in {@link com.aerospike.client.policy.WritePolicy}.
     */
    @Data
    public static class WritePolicyDefault {

        /**
         * Socket idle timeout when processing a database command.
         */
        public Duration socketTimeout;

        /**
         * Total transaction timeout.
         */
        public Duration totalTimeout;

        /**
         * Delay after socket read timeout in an attempt to recover the socket
         * in the background.
         */
        public Duration timeoutDelay;

        /**
         * Maximum number of retries before aborting the current transaction.
         * NOTE: The initial attempt is not counted as a retry.
         */
        public Integer maxRetries;

        /**
         * Time to sleep between retries.
         */
        public Duration sleepBetweenRetries;

        /**
         * If the transaction results in a record deletion, leave a tombstone for the record.
         */
        public Boolean durableDelete;
    }

}
