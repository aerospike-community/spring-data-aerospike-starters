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

package org.springframework.boot.aerospike.data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.aerospike.data.city.ReactiveCityRepository;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.aerospike.data.city.City;
import org.springframework.boot.aerospike.data.city.CityRepository;
import org.springframework.boot.aerospike.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport.CONFIG_PREFIX_CONNECTION;
import static org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport.CONFIG_PREFIX_DATA;

/**
 * Tests for {@link AerospikeRepositoriesAutoConfiguration}.
 *
 * @author Igor Ermolenko
 */
public class AerospikeRepositoriesAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AerospikeAutoConfiguration.class,
                    AerospikeDataAutoConfiguration.class, AerospikeRepositoriesAutoConfiguration.class))
            .withPropertyValues(CONFIG_PREFIX_CONNECTION + ".hosts=localhost:3000")
            .withPropertyValues(CONFIG_PREFIX_DATA + ".namespace=TEST");

    @Test
    public void repositoryIsCreated() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).hasSingleBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedWhenRepositoryInterfaceDoesNotExists() {
        contextRunner
                .withUserConfiguration(NoRepositoryConfiguration.class)
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeReactive() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues(CONFIG_PREFIX_DATA + ".repositories.type=reactive")
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    Assertions.assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeNone() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues(CONFIG_PREFIX_DATA + ".repositories.type=none")
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
