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
import com.aerospike.client.reactor.AerospikeReactorClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeTestConfigurations.AerospikeServerVersionSupportMockConfiguration;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeTestConfigurations.EntityScanConfiguration;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.aerospike.convert.AerospikeConverter;
import org.springframework.data.aerospike.convert.AerospikeTypeAliasAccessor;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.util.TypeInformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.autoconfigure.data.aerospike.AerospikeTestConfigurations.AerospikeClientMockConfiguration;
import static org.springframework.boot.autoconfigure.data.aerospike.AerospikeTestConfigurations.CustomConversionsConfig;
import static org.springframework.boot.autoconfigure.data.aerospike.TestUtils.getField;

/**
 * Tests for {@link AerospikeDataAutoConfiguration}.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
public class AerospikeDataAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withClassLoader(new FilteredClassLoader(AerospikeReactorClient.class))
            .withConfiguration(AutoConfigurations.of(
                    AerospikeAutoConfiguration.class, AerospikeDataAutoConfiguration.class));

    @Test
    public void aerospikeTemplateAndClientAreNotSetupWhenNeitherClientNorDataPropertiesConfigured() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(AerospikeClient.class);
            assertThat(context).doesNotHaveBean(AerospikeTemplate.class);
            assertThat(context).doesNotHaveBean(AerospikeDataProperties.class);
            assertThat(context).doesNotHaveBean(AerospikeMappingContext.class);
        });
    }

    @Test
    public void entityScanShouldSetInitialEntitySet() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(EntityScanConfiguration.class, AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .run(context -> {
                    AerospikeMappingContext mappingContext = context.getBean(AerospikeMappingContext.class);
                    assertThat(mappingContext.getManagedTypes()).containsOnly(TypeInformation.of(City.class));
                });
    }

    @Test
    public void classKeyDefault() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .run(context -> {
                    AerospikeTypeAliasAccessor aliasAccessor = context.getBean(AerospikeTypeAliasAccessor.class);
                    String classKey = getField(aliasAccessor, "classKey");

                    assertThat(classKey).isEqualTo(AerospikeConverter.CLASS_KEY);
                });
    }

    @Test
    public void typeKeyCanBeCustomized() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .withPropertyValues("spring.data.aerospike.type-key=++amazing++")
                .run((context) -> {
                    AerospikeTypeAliasAccessor aliasAccessor = context.getBean(AerospikeTypeAliasAccessor.class);
                    String typeKey = getField(aliasAccessor, "classKey");

                    assertThat(typeKey).isEqualTo("++amazing++");
                });
    }

    @Test
    public void typeKeyCanBeNull() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .withPropertyValues("spring.data.aerospike.type-key=")
                .run((context) -> {
                    AerospikeTypeAliasAccessor aliasAccessor = context.getBean(AerospikeTypeAliasAccessor.class);
                    String typeKey = getField(aliasAccessor, "classKey");

                    assertThat(typeKey).isNull();
                });
    }

    @Test
    public void customConversions() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(CustomConversionsConfig.class, AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .run(context -> {
                    MappingAerospikeConverter converter = context.getBean(MappingAerospikeConverter.class);
                    assertThat(converter.getConversionService().canConvert(City.class, String.class)).isTrue();
                });
    }

    @Test
    public void configurationIsApplied() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(AerospikeClientMockConfiguration.class,
                        AerospikeServerVersionSupportMockConfiguration.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveAerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeDataProperties.class);
                    assertThat(context).hasSingleBean(AerospikeProperties.class);
                    assertThat(context).hasSingleBean(AerospikeMappingContext.class);
                });
    }
}
