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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport.CONFIG_PREFIX_DATA;

/**
 * Configuration properties for Spring Data Aerospike.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@ConfigurationProperties(prefix = CONFIG_PREFIX_DATA)
@Getter
@Setter
public class AerospikeDataProperties {

    /**
     * Aerospike namespace to store data in.
     */
    private String namespace;

    /**
     * Bin name that will be used for storing entity's type. Default value is null
     * <p>
     *
     * @see org.springframework.data.aerospike.convert.AerospikeTypeAliasAccessor
     */
    private String classKey = "@_class";

    /**
     * Gives ability to disable queries that will run scan on Aerospike server.
     */
    private boolean scansEnabled = false;

    /**
     * Specifies fully qualified name of the FieldNamingStrategy for the entities.
     */
    private Class<?> fieldNamingStrategy;

    /**
     * Specifies whether to create secondary indexes for @Indexed annotated fields on application startup.
     */
    private boolean createIndexesOnStartup = true;

    /**
     * Send user defined key in addition to hash digest on both reads and writes
     */
    private boolean sendKey = true;

    /**
     * Automatically refresh indexes cache every <N> seconds
     */
    private int indexCacheRefreshSeconds = 3600;

    /**
     * Automatically refresh cached server version every <N> seconds
     */
    private int serverVersionRefreshSeconds = 0;

    /**
     * Limit amount of results returned by server. Non-positive value means no limit.
     */
    private long queryMaxRecords = 10_000L;

    /**
     * Maximum batch size for batch write operations
     */
    private int batchWriteSize = 100;

    /**
     * Define how @Id fields (primary keys) and Map keys are stored: false - always as String,
     * true - preserve original type if supported
     */
    private boolean keepOriginalKeyTypes = false;

    /**
     * Define how Maps and POJOs are written: true - as sorted maps (TreeMaps, default), false - as unsorted (HashMaps)
     * Writing unsorted maps (false) degrades performance of Map-related operations and does not allow comparing Maps,
     * strongly recommended not to use except during upgrade from older versions of Spring Data Aerospike (if required)
     */
    private boolean writeSortedMaps = true;
}
