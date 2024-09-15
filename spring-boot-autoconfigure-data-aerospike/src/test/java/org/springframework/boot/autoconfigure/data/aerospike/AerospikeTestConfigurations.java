package org.springframework.boot.autoconfigure.data.aerospike;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeCustomConversions;

import java.util.List;

public class AerospikeTestConfigurations {

    @AutoConfiguration()
    @EntityScan("org.springframework.boot.autoconfigure.data.aerospike.city")
    @TestAutoConfigurationPackage(City.class)
    public static class EntityScanConfiguration {

    }

    @AutoConfiguration
    public static class CustomConversionsConfig {

        @Bean(name = "aerospikeCustomConversions")
        AerospikeCustomConversions myCustomConversions() {
            return new AerospikeCustomConversions(List.of(new CityToStringConverter()));
        }
    }

    public static class CityToStringConverter implements Converter<City, String> {

        @Override
        public String convert(City value) {
            return value.getName();
        }
    }
}
