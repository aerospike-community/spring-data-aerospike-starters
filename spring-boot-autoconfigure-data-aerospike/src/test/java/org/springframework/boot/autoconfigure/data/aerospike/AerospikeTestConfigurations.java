package org.springframework.boot.autoconfigure.data.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeCustomConversions;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.mockito.Mockito.mock;

public class AerospikeTestConfigurations {

    @Configuration(proxyBeanMethods = false)
    public static class AerospikeClientMockConfiguration {

        @Bean
        public AerospikeClient aerospikeClientMock() {
            AerospikeClient client = mock(AerospikeClient.class);

            Cluster cluster = mock(Cluster.class);
            Field clusterField = ReflectionUtils.findField(AerospikeClient.class, "cluster");
            clusterField.setAccessible(true);
            ReflectionUtils.setField(clusterField, client, cluster);

            Field writePolicyDefaultField = ReflectionUtils.findField(AerospikeClient.class, "writePolicyDefault");
            writePolicyDefaultField.setAccessible(true);
            ReflectionUtils.setField(writePolicyDefaultField, client, new WritePolicy());

            Field nodesField = ReflectionUtils.findField(Cluster.class, "nodes");
            nodesField.setAccessible(true);
            ReflectionUtils.setField(nodesField, cluster, new Node[]{});
            return client;
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
