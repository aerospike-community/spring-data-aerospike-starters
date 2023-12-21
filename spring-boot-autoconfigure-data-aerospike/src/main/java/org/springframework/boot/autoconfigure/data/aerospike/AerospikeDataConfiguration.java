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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeExceptionTranslator;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.index.AerospikeIndexResolver;
import org.springframework.data.aerospike.index.AerospikePersistenceEntityIndexCreator;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.aerospike.query.FilterExpressionsBuilder;
import org.springframework.data.aerospike.query.QueryEngine;
import org.springframework.data.aerospike.query.StatementBuilder;
import org.springframework.data.aerospike.query.cache.IndexInfoParser;
import org.springframework.data.aerospike.query.cache.IndexRefresher;
import org.springframework.data.aerospike.query.cache.IndexesCacheUpdater;
import org.springframework.data.aerospike.query.cache.InternalIndexOperations;
import org.springframework.data.aerospike.server.version.ServerVersionSupport;

/**
 * Configure Spring Data's Aerospike support.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@Slf4j
@AutoConfiguration
class AerospikeDataConfiguration {

    @Bean(name = "aerospikeTemplate")
    @ConditionalOnMissingBean(name = "aerospikeTemplate")
    public AerospikeTemplate aerospikeTemplate(IAerospikeClient aerospikeClient,
                                               AerospikeDataProperties aerospikeDataProperties,
                                               MappingAerospikeConverter mappingAerospikeConverter,
                                               AerospikeMappingContext aerospikeMappingContext,
                                               AerospikeExceptionTranslator aerospikeExceptionTranslator,
                                               QueryEngine queryEngine, IndexRefresher indexRefresher,
                                               ServerVersionSupport serverVersionSupport) {
        return new AerospikeTemplate(aerospikeClient, aerospikeDataProperties.getNamespace(), mappingAerospikeConverter,
                aerospikeMappingContext, aerospikeExceptionTranslator, queryEngine, indexRefresher, serverVersionSupport);
    }

    @Bean(name = "aerospikeQueryEngine")
    @ConditionalOnMissingBean(name = "aerospikeQueryEngine")
    public QueryEngine aerospikeQueryEngine(IAerospikeClient aerospikeClient,
                                            AerospikeDataProperties aerospikeDataProperties,
                                            FilterExpressionsBuilder filterExpressionsBuilder,
                                            StatementBuilder statementBuilder) {
        QueryEngine queryEngine = new QueryEngine(aerospikeClient, statementBuilder, filterExpressionsBuilder);
        queryEngine.setScansEnabled(aerospikeDataProperties.isScansEnabled());
        queryEngine.setQueryMaxRecords(aerospikeDataProperties.getQueryMaxRecords());
        return queryEngine;
    }

    @Bean(name = "aerospikeIndexRefresher")
    @ConditionalOnMissingBean(name = "aerospikeIndexRefresher")
    public IndexRefresher indexRefresher(IAerospikeClient aerospikeClient, IndexesCacheUpdater indexesCacheUpdater,
                                         ServerVersionSupport serverVersionSupport,
                                         AerospikeDataProperties aerospikeDataProperties) {
        IndexRefresher refresher = new IndexRefresher(aerospikeClient, aerospikeClient.getInfoPolicyDefault(),
                new InternalIndexOperations(new IndexInfoParser()), indexesCacheUpdater, serverVersionSupport);
        refresher.refreshIndexes();
        int refreshFrequency = aerospikeDataSettings(aerospikeDataProperties).getIndexCacheRefreshSeconds();
        processCacheRefreshFrequency(refreshFrequency, refresher);
        return refresher;
    }

    private void processCacheRefreshFrequency(int indexCacheRefreshSeconds, IndexRefresher indexRefresher) {
        if (indexCacheRefreshSeconds <= 0) {
            log.info("Periodic index cache refreshing is not scheduled, interval ({}) is <= 0",
                    indexCacheRefreshSeconds);
        } else {
            indexRefresher.scheduleRefreshIndexes(indexCacheRefreshSeconds);
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = "aerospikePersistenceEntityIndexCreator")
    public AerospikePersistenceEntityIndexCreator aerospikePersistenceEntityIndexCreator(
            AerospikeDataProperties aerospikeDataProperties,
            @Lazy ObjectProvider<AerospikeTemplate> template,
            ObjectProvider<AerospikeMappingContext> aerospikeMappingContext,
            AerospikeIndexResolver aerospikeIndexResolver) {
        return new AerospikePersistenceEntityIndexCreator(aerospikeMappingContext,
                aerospikeDataProperties.isCreateIndexesOnStartup(), aerospikeIndexResolver, template);
    }

    private AerospikeDataSettings aerospikeDataSettings(AerospikeDataProperties aerospikeDataProperties) {
        AerospikeDataSettings.AerospikeDataSettingsBuilder builder = AerospikeDataSettings.builder();
        configureDataSettings(builder, aerospikeDataProperties);
        return builder.build();
    }

    private void configureDataSettings(AerospikeDataSettings.AerospikeDataSettingsBuilder builder,
                                         AerospikeDataProperties aerospikeDataProperties) {
        builder.scansEnabled(aerospikeDataProperties.isScansEnabled());
        builder.sendKey(aerospikeDataProperties.isSendKey());
        builder.createIndexesOnStartup(aerospikeDataProperties.isCreateIndexesOnStartup());
    }
}
