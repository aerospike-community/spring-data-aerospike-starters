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

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.data.aerospike.city.CityRepository;
import org.springframework.boot.autoconfigure.data.aerospike.city.ReactiveCityRepository;
import org.springframework.boot.autoconfigure.data.aerospike.empty.EmptyDataPackage;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.mapping.context.MappingContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AerospikeRepositoriesAutoConfiguration}.
 *
 * @author Igor Ermolenko
 */
public class AerospikeRepositoriesAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AerospikeRepositoriesAutoConfiguration.class, MockConfiguration.class));

    @Test
    public void repositoryIsCreated() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.hosts=localhost:3000")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).hasSingleBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedWhenPropertyIsAbsent() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedWhenRepositoryInterfaceDoesNotExists() {
        contextRunner
                .withUserConfiguration(NoRepositoryConfiguration.class)
                .withPropertyValues("spring.data.aerospike.hosts=localhost:3000")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeReactive() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.repositories.type=reactive")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeNone() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.repositories.type=none")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
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

    @Configuration
    static class MockConfiguration {
        @Bean
        @Primary
        public AerospikeTemplate aerospikeTemplate() {
            AerospikeMappingContext context = new AerospikeMappingContext();
            AerospikeTemplate mock = Mockito.mock(AerospikeTemplate.class);
            when(mock.getMappingContext()).thenReturn((MappingContext) context);
            return mock;
        }
    }

}
