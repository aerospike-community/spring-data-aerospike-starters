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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AerospikeDataProperties}.
 */
public class AerospikeDataPropertiesTest {

    @Test
    public void getEffectiveClassKey_returnsDefault_whenNeitherSet() {
        AerospikeDataProperties properties = new AerospikeDataProperties();

        assertThat(properties.getEffectiveClassKey()).isEqualTo("@_class");
    }

    @Test
    public void getEffectiveClassKey_returnsClassKey_whenOnlyClassKeySet() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setClassKey("@custom_class");

        assertThat(properties.getEffectiveClassKey()).isEqualTo("@custom_class");
    }

    @Test
    public void getEffectiveClassKey_returnsTypeKey_whenOnlyTypeKeySet() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setTypeKey("@legacy_type");

        assertThat(properties.getEffectiveClassKey()).isEqualTo("@legacy_type");
    }

    @Test
    public void getEffectiveClassKey_returnsTypeKey_whenBothSet() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setClassKey("@new_class");
        properties.setTypeKey("@legacy_type");

        // typeKey takes precedence for backward compatibility
        assertThat(properties.getEffectiveClassKey()).isEqualTo("@legacy_type");
    }

    @Test
    public void getEffectiveClassKey_returnsClassKey_whenTypeKeyIsEmpty() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setClassKey("@custom_class");
        properties.setTypeKey("");

        // Empty string is treated as "not set", falls back to classKey
        assertThat(properties.getEffectiveClassKey()).isEqualTo("@custom_class");
    }

    @Test
    public void getEffectiveClassKey_returnsNull_whenClassKeySetToNull() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setClassKey(null);

        assertThat(properties.getEffectiveClassKey()).isNull();
    }

    @Test
    public void getEffectiveClassKey_returnsEmptyClassKey_whenClassKeySetToEmpty() {
        AerospikeDataProperties properties = new AerospikeDataProperties();
        properties.setClassKey("");

        // Empty classKey is a valid value (disables type storage)
        assertThat(properties.getEffectiveClassKey()).isEmpty();
    }
}
