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

import com.aerospike.client.AerospikeClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeExceptionTranslator;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.index.AerospikePersistenceEntityIndexCreator;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.aerospike.query.QueryEngine;
import org.springframework.data.aerospike.query.StatementBuilder;
import org.springframework.data.aerospike.query.cache.IndexInfoParser;
import org.springframework.data.aerospike.query.cache.IndexRefresher;
import org.springframework.data.aerospike.query.cache.IndexesCacheUpdater;
import org.springframework.data.aerospike.query.cache.InternalIndexOperations;

/**
 * Configure Spring Data's Aerospike support.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@Configuration(proxyBeanMethods = false)
class AerospikeDataConfiguration {

    @Bean(name = "aerospikeTemplate")
    @ConditionalOnMissingBean(name = "aerospikeTemplate")
    public AerospikeTemplate aerospikeTemplate(AerospikeClient aerospikeClient,
                                               AerospikeDataProperties aerospikeDataProperties,
                                               MappingAerospikeConverter mappingAerospikeConverter,
                                               AerospikeMappingContext aerospikeMappingContext,
                                               AerospikeExceptionTranslator aerospikeExceptionTranslator,
                                               QueryEngine queryEngine, IndexRefresher indexRefresher) {
        return new AerospikeTemplate(aerospikeClient,
                aerospikeDataProperties.getNamespace(),
                mappingAerospikeConverter,
                aerospikeMappingContext,
                aerospikeExceptionTranslator, queryEngine, indexRefresher);
    }

    @Bean(name = "aerospikeQueryEngine")
    @ConditionalOnMissingBean(name = "aerospikeQueryEngine")
    public QueryEngine aerospikeQueryEngine(AerospikeClient aerospikeClient,
                                            AerospikeDataProperties aerospikeDataProperties,
                                            StatementBuilder statementBuilder) {
        QueryEngine queryEngine = new QueryEngine(aerospikeClient, statementBuilder, aerospikeClient.getQueryPolicyDefault());
        queryEngine.setScansEnabled(aerospikeDataProperties.isScansEnabled());
        return queryEngine;
    }

    @Bean(name = "aerospikeIndexRefresher")
    @ConditionalOnMissingBean(name = "aerospikeIndexRefresher")
    public IndexRefresher aerospikeIndexRefresher(AerospikeClient aerospikeClient, IndexesCacheUpdater indexesCacheUpdater) {
        IndexRefresher refresher = new IndexRefresher(aerospikeClient, aerospikeClient.getInfoPolicyDefault(), new InternalIndexOperations(new IndexInfoParser()), indexesCacheUpdater);
        refresher.refreshIndexes();
        return refresher;
    }

    @Bean
    @ConditionalOnMissingBean(name = "aerospikePersistenceEntityIndexCreator")
    public AerospikePersistenceEntityIndexCreator aerospikePersistenceEntityIndexCreator(AerospikeMappingContext aerospikeMappingContext,
                                                                                         @Lazy AerospikeTemplate template) {
        return new AerospikePersistenceEntityIndexCreator(aerospikeMappingContext, template);
    }
}
