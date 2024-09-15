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

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.data.aerospike.city.CityRepository;
import org.springframework.boot.autoconfigure.data.aerospike.city.ReactiveCityRepository;
import org.springframework.boot.autoconfigure.data.aerospike.empty.EmptyDataPackage;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AerospikeReactiveRepositoriesAutoConfiguration}.
 *
 * @author Igor Ermolenko
 */
public class AerospikeReactiveRepositoriesAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
//            .withConfiguration(AutoConfigurations.of(AerospikeReactiveRepositoriesAutoConfiguration.class, MockConfiguration.class));
            .withConfiguration(AutoConfigurations.of(AerospikeReactiveRepositoriesAutoConfiguration.class));

    @Test
    public void reactiveRepositoryIsCreated() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.aerospike.hosts=localhost:3000", // TODO: determining port of embedded image
                        "spring.data.aerospike.namespace=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedWhenRepositoryInterfaceNotExists() {
        contextRunner
                .withUserConfiguration(NoRepositoryConfiguration.class)
                .withPropertyValues("spring.aerospike.hosts=localhost:3000", // TODO: determining port of embedded image
                        "spring.data.aerospike.namespace=test")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeImperative() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
                .withPropertyValues("spring.data.aerospike.repositories.type=imperative")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveCityRepository.class);
                    assertThat(context).doesNotHaveBean(CityRepository.class);
                });
    }

    @Test
    public void repositoryIsNotCreatedForTypeNone() {
        contextRunner
                .withUserConfiguration(DefaultConfiguration.class)
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
}
