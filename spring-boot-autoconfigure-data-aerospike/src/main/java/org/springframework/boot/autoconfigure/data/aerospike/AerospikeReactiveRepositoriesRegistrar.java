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

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;
import org.springframework.data.aerospike.repository.config.ReactiveAerospikeRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data Aerospike Reactive Repositories.
 *
 * @author Igor Ermolenko
 */
public class AerospikeReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveAerospikeRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableReactiveAerospikeRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveAerospikeRepositoryConfigurationExtension();
    }

    @EnableReactiveAerospikeRepositories
    private static class EnableReactiveAerospikeRepositoriesConfiguration {
    }
}
