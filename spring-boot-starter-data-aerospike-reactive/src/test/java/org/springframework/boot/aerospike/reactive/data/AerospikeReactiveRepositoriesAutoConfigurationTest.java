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

package org.springframework.boot.aerospike.reactive.data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.aerospike.reactive.data.city.City;
import org.springframework.boot.aerospike.reactive.data.city.CityRepository;
import org.springframework.boot.aerospike.reactive.data.city.ReactiveCityRepository;
import org.springframework.boot.aerospike.reactive.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.mapping.context.MappingContext;

/**
 * Tests for {@link AerospikeReactiveRepositoriesAutoConfiguration}.
 *
 * @author Igor Ermolenko
 */
public class AerospikeReactiveRepositoriesAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AerospikeAutoConfiguration.class,
                    AerospikeReactiveDataAutoConfiguration.class, AerospikeReactiveRepositoriesAutoConfiguration.class))
            .withPropertyValues("spring.aerospike.hosts=localhost:3000")
            .withPropertyValues("spring.data.aerospike.namespace=TEST");

    @Test
    public void reactiveRepositoryIsCreated() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedWhenRepositoryInterfaceNotExists() {
        contextRunner
                .withUserConfiguration(NoRepositoryConfiguration.class)
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeImperative() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.repositories.type=imperative")
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeNone() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.repositories.type=none")
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }


    @Configuration
    @TestAutoConfigurationPackage(City.class)
    static class DefaultConfiguration {
    }

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    static class NoRepositoryConfiguration {
    }
}
