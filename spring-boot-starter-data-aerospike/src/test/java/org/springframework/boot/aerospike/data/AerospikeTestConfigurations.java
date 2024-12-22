package org.springframework.boot.aerospike.data;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.aerospike.data.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeCustomConverters;
import java.util.List;

public class AerospikeTestConfigurations {

    @AutoConfiguration
    @EntityScan("org.springframework.boot.autoconfigure.data.aerospike.city")
    public static class EntityScanConfiguration {

    }

    @AutoConfiguration
    public static class CustomConversionsConfig {

        @Bean
        AerospikeCustomConverters myCustomConversions() {
            return new AerospikeCustomConverters(List.of(new CityToStringConverter()));
        }
    }

    public static class CityToStringConverter implements Converter<City, String> {

        @Override
        public String convert(City value) {
            return value.getName();
        }
    }
}
