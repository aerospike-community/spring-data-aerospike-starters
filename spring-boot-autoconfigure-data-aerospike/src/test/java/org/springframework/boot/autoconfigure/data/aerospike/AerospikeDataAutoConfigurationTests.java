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
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AerospikeDataAutoConfiguration}.
 *
 * @author Igor Ermolenko
 */
public class AerospikeDataAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AerospikeDataAutoConfiguration.class, MockConfiguration.class));

    @Test
    public void configurationIsApplied() {
        contextRunner
                .withPropertyValues("spring.data.aerospike.hosts=localhost:3000")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveAerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeDataProperties.class);
                });
    }

    @Test
    public void configurationIsNotAppliedWhenPropertyIsAbsent() {
        contextRunner
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AerospikeDataProperties.class);
                });
    }


    @Configuration
    static class MockConfiguration {
        @Bean
        @Primary
        public AerospikeClient aerospikeClient() {
            return Mockito.mock(AerospikeClient.class);
        }

        @Bean
        @Primary
        public AerospikeTemplate aerospikeTemplate() {
            return Mockito.mock(AerospikeTemplate.class);
        }
    }

}
