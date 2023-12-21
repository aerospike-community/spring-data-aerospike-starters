package org.springframework.boot.autoconfigure.data.aerospike;

import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.WritePolicy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.aerospike.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeCustomConversions;
import org.springframework.data.aerospike.query.cache.ReactorIndexRefresher;
import org.springframework.data.aerospike.server.version.ServerVersionSupport;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AerospikeTestConfigurations {

    @AutoConfiguration
    public static class AerospikeClientMockConfiguration {

        @Bean
        public IAerospikeClient aerospikeClientMock() {
            IAerospikeClient client = mock(IAerospikeClient.class);
            when(client.getNodes()).thenReturn(new Node[]{});
            when(client.getWritePolicyDefault()).thenReturn(new WritePolicy());
            return client;
        }

    }

    @AutoConfiguration
    public static class AerospikeServerVersionSupportMockConfiguration {

        @Bean
        public ServerVersionSupport aerospikeServerVersionSupportMock() {
            ServerVersionSupport serverVersionSupport = mock(ServerVersionSupport.class);
            when(serverVersionSupport.getServerVersion()).thenReturn("5.0.0.0");
            return serverVersionSupport;
        }

    }

    @AutoConfiguration
    public static class MockReactiveIndexRefresher {

        @Bean
        public ReactorIndexRefresher reactiveAerospikeIndexRefresher() {
            return mock(ReactorIndexRefresher.class);
        }
    }

    @AutoConfiguration
    @EntityScan("org.springframework.boot.autoconfigure.data.aerospike.city")
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
