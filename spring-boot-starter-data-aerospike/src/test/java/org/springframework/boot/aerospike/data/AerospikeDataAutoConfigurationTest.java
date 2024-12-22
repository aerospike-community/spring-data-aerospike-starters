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

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.reactor.AerospikeReactorClient;
import com.aerospike.client.reactor.IAerospikeReactorClient;
import com.aerospike.client.reactor.retry.AerospikeReactorRetryClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.aerospike.data.city.City;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.aerospike.AerospikeAutoConfiguration;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.boot.autoconfigure.data.aerospike.AerospikeDataProperties;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.aerospike.data.TestUtils.getField;

/**
 * Tests for {@link AerospikeDataAutoConfiguration}.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
public class AerospikeDataAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withClassLoader(new FilteredClassLoader(IAerospikeReactorClient.class))
            .withConfiguration(AutoConfigurations.of(
                    AerospikeAutoConfiguration.class, AerospikeDataAutoConfiguration.class));

    @Test
    public void aerospikeTemplateAndClientAreNotSetupWhenNoHostsPropertyGiven() {
        assertThatThrownBy(() -> contextRunner.run(context -> context.getBean(AerospikeTemplate.class)))
                .cause()
                .hasMessageContaining("Required property 'spring.aerospike.hosts' is missing");
    }

    @Test
    public void entityScanShouldSetInitialEntitySet() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withUserConfiguration(AerospikeTestConfigurations.EntityScanConfiguration.class)
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
                .run(context -> {
                    AerospikeTypeAliasAccessor aliasAccessor = context.getBean(AerospikeTypeAliasAccessor.class);
                    String classKey = getField(aliasAccessor, "classKey");

                    assertThat(classKey).isEqualTo(AerospikeConverter.CLASS_KEY_DEFAULT);
                });
    }

    @Test
    public void typeKeyCanBeCustomized() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withPropertyValues("spring.data.aerospike.class-key=++amazing++")
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
                .withPropertyValues("spring.data.aerospike.class-key=")
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
                .withUserConfiguration(AerospikeTestConfigurations.CustomConversionsConfig.class)
                .run(context -> {
                    MappingAerospikeConverter converter = context.getBean(MappingAerospikeConverter.class);
                    assertThat(converter.getConversionService().canConvert(City.class, String.class)).isTrue();
                });
    }

    @Test
    public void sendKeyPropertyIsRead() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withPropertyValues("spring.aerospike.write.sendKey=false")
                .run(context -> {
                    IAerospikeClient client = context.getBean(IAerospikeClient.class);
                    assertThat(client.getWritePolicyDefault().sendKey).isFalse();
                });

        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withPropertyValues("spring.aerospike.write.sendKey=true")
                .run(context -> {
                    IAerospikeClient client = context.getBean(IAerospikeClient.class);
                    assertThat(client.getWritePolicyDefault().sendKey).isTrue();
                });
    }

    @Test
    public void configurationIsApplied() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .run(context -> {
                    // In Spring Data Aerospike only the relevant flow beans are loaded (sync in this case)
                    assertThat(context).doesNotHaveBean(ReactiveAerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeDataProperties.class);
                    assertThat(context).hasSingleBean(AerospikeProperties.class);
                    assertThat(context).hasSingleBean(AerospikeMappingContext.class);
                });
    }

    @Test
    public void dataConfigurationIsAppliedWithDifferentClients() {
        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withClassLoader(AerospikeReactorClient.class.getClassLoader())
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveAerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeProperties.class);
                    assertThat(context).hasSingleBean(AerospikeMappingContext.class);
                });

        contextRunner
                .withPropertyValues("spring.aerospike.hosts=localhost:3000")
                .withPropertyValues("spring.data.aerospike.namespace=TEST")
                .withClassLoader(AerospikeReactorRetryClient.class.getClassLoader())
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ReactiveAerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeTemplate.class);
                    assertThat(context).hasSingleBean(AerospikeProperties.class);
                    assertThat(context).hasSingleBean(AerospikeMappingContext.class);
                });
    }
}
