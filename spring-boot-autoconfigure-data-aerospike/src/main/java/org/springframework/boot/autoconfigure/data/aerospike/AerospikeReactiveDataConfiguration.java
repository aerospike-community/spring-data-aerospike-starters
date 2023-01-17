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

import com.aerospike.client.reactor.AerospikeReactorClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeExceptionTranslator;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.index.AerospikeIndexResolver;
import org.springframework.data.aerospike.index.ReactiveAerospikePersistenceEntityIndexCreator;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.aerospike.query.FilterExpressionsBuilder;
import org.springframework.data.aerospike.query.ReactorQueryEngine;
import org.springframework.data.aerospike.query.StatementBuilder;
import org.springframework.data.aerospike.query.cache.IndexInfoParser;
import org.springframework.data.aerospike.query.cache.IndexesCacheUpdater;
import org.springframework.data.aerospike.query.cache.InternalIndexOperations;
import org.springframework.data.aerospike.query.cache.ReactorIndexRefresher;

/**
 * Configure Spring Data's Reactive Aerospike support.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@AutoConfiguration
class AerospikeReactiveDataConfiguration {

    @Bean(name = "reactiveAerospikeTemplate")
    @ConditionalOnMissingBean(name = "reactiveAerospikeTemplate")
    public ReactiveAerospikeTemplate reactiveAerospikeTemplate(MappingAerospikeConverter mappingAerospikeConverter,
                                                               AerospikeDataProperties aerospikeDataProperties,
                                                               AerospikeMappingContext aerospikeMappingContext,
                                                               AerospikeExceptionTranslator aerospikeExceptionTranslator,
                                                               AerospikeReactorClient aerospikeReactorClient,
                                                               ReactorQueryEngine reactorQueryEngine, ReactorIndexRefresher reactorIndexRefresher) {
        return new ReactiveAerospikeTemplate(aerospikeReactorClient, aerospikeDataProperties.getNamespace(), mappingAerospikeConverter, aerospikeMappingContext,
                aerospikeExceptionTranslator, reactorQueryEngine, reactorIndexRefresher);
    }

    @Bean(name = "reactiveAerospikeQueryEngine")
    @ConditionalOnMissingBean(name = "reactiveAerospikeQueryEngine")
    public ReactorQueryEngine reactiveAerospikeQueryEngine(AerospikeReactorClient aerospikeReactorClient,
                                                           AerospikeDataProperties aerospikeDataProperties,
                                                           FilterExpressionsBuilder filterExpressionsBuilder,
                                                           StatementBuilder statementBuilder) {
        ReactorQueryEngine queryEngine = new ReactorQueryEngine(aerospikeReactorClient, statementBuilder, filterExpressionsBuilder, aerospikeReactorClient.getQueryPolicyDefault());
        queryEngine.setScansEnabled(aerospikeDataProperties.isScansEnabled());
        return queryEngine;
    }

    @Bean(name = "reactiveAerospikeIndexRefresher")
    @ConditionalOnMissingBean(name = "reactiveAerospikeIndexRefresher")
    public ReactorIndexRefresher reactiveAerospikeIndexRefresher(AerospikeReactorClient aerospikeReactorClient,
                                                                 IndexesCacheUpdater indexesCacheUpdater) {
        ReactorIndexRefresher refresher = new ReactorIndexRefresher(aerospikeReactorClient, aerospikeReactorClient.getInfoPolicyDefault(),
                new InternalIndexOperations(new IndexInfoParser()), indexesCacheUpdater);
        refresher.refreshIndexes().block();
        return refresher;
    }

    @Bean
    @ConditionalOnMissingBean(name = "reactiveAerospikePersistenceEntityIndexCreator")
    public ReactiveAerospikePersistenceEntityIndexCreator reactiveAerospikePersistenceEntityIndexCreator(
            AerospikeDataProperties aerospikeDataProperties,
            @Lazy ReactiveAerospikeTemplate template,
            ObjectProvider<AerospikeMappingContext> aerospikeMappingContext,
            AerospikeIndexResolver aerospikeIndexResolver) {
        return new ReactiveAerospikePersistenceEntityIndexCreator(aerospikeMappingContext, aerospikeDataProperties.isCreateIndexesOnStartup(),
                aerospikeIndexResolver,
                template);
    }
}
