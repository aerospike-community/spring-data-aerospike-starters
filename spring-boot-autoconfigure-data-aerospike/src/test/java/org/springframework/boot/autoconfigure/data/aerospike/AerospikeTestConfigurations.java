package org.springframework.boot.autoconfigure.data.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.reactor.AerospikeReactorClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeCustomConversions;
import org.springframework.data.aerospike.query.cache.IndexInfoParser;
import org.springframework.data.aerospike.query.cache.IndexesCacheUpdater;
import org.springframework.data.aerospike.query.cache.InternalIndexOperations;
import org.springframework.data.aerospike.query.cache.ReactorIndexRefresher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;

public class AerospikeTestConfigurations {

    @Configuration(proxyBeanMethods = false)
    public static class AerospikeClientMockConfiguration {

        @Bean
        public AerospikeClient aerospikeClientMock() {
            Cluster cluster = mock(Cluster.class);
            setField(cluster, "nodes", new Node[]{});
            setField(cluster, "nodeIndex", new AtomicInteger());

            AerospikeClient client = mock(AerospikeClient.class);
            setField(client, "cluster", cluster);
            setField(client, "writePolicyDefault", new WritePolicy());

            return client;
        }

        private void setField(Object object, String fieldName, Object fieldValue) {
            Field field = ReflectionUtils.findField(object.getClass(), fieldName);
            field.setAccessible(true);
            ReflectionUtils.setField(field, object, fieldValue);
        }

    }

    @Configuration(proxyBeanMethods = false)
    public static class MockReactiveIndexRefresher {

        @Bean
        public ReactorIndexRefresher reactiveAerospikeIndexRefresher() {
            return mock(ReactorIndexRefresher.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EntityScan("org.springframework.boot.autoconfigure.data.aerospike.city")
    public static class EntityScanConfiguration {

    }

    @Configuration(proxyBeanMethods = false)
    public static class CustomConversionsConfig {

        @Bean(name = "aerospikeCustomConversions")
        AerospikeCustomConversions myCustomConversions() {
            return new AerospikeCustomConversions(Arrays.asList(new CityToStringConverter()));
        }

    }

    public static class CityToStringConverter implements Converter<City, String> {

        @Override
        public String convert(City value) {
            return value.getName();
        }

    }
}
