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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Spring Data Aerospike.
 *
 * @author Igor Ermolenko
 * @author Anastasiia Smirnova
 */
@ConfigurationProperties(prefix = "spring.data.aerospike")
public class AerospikeDataProperties {

    /**
     * Aerospike namespace to store data in.
     */
    private String namespace;

    /**
     * Bin name that will be used for storing entity's type.
     * <p>
     *
     * @see org.springframework.data.aerospike.convert.AerospikeTypeAliasAccessor
     */
    private String typeKey = "@_class";

    /**
     * Gives ability to disable queries that will run scan on Aerospike server.
     */
    private boolean scansEnabled = false;

    /**
     * Specifies fully qualified name of the FieldNamingStrategy for the entities.
     */
    private Class<?> fieldNamingStrategy;

    /**
     * Specifies whether to create secondary indexes for @Indexed annotated fields on application startup.
     */
    private boolean createIndexesOnStartup = true;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public boolean isScansEnabled() {
        return scansEnabled;
    }

    public void setScansEnabled(boolean scansEnabled) {
        this.scansEnabled = scansEnabled;
    }

    public Class<?> getFieldNamingStrategy() {
        return fieldNamingStrategy;
    }

    public void setFieldNamingStrategy(Class<?> fieldNamingStrategy) {
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    public boolean isCreateIndexesOnStartup() {
        return createIndexesOnStartup;
    }

    public void setCreateIndexesOnStartup(boolean createIndexesOnStartup) {
        this.createIndexesOnStartup = createIndexesOnStartup;
    }
}
