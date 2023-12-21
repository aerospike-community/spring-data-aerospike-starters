package org.springframework.boot.autoconfigure.data.aerospike;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.convert.AerospikeCustomConversions;
import org.springframework.data.aerospike.convert.AerospikeTypeAliasAccessor;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeExceptionTranslator;
import org.springframework.data.aerospike.core.DefaultAerospikeExceptionTranslator;
import org.springframework.data.aerospike.index.AerospikeIndexResolver;
import org.springframework.data.aerospike.mapping.AerospikeMappingContext;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.query.FilterExpressionsBuilder;
import org.springframework.data.aerospike.query.StatementBuilder;
import org.springframework.data.aerospike.query.cache.IndexesCache;
import org.springframework.data.aerospike.query.cache.IndexesCacheHolder;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;

import java.util.Collections;

@Slf4j
@AutoConfiguration
class AerospikeCommonDataConfiguration {

    @Bean(name = "aerospikeFilterExpressionsBuilder")
    @ConditionalOnMissingBean(name = "aerospikeFilterExpressionsBuilder")
    public FilterExpressionsBuilder aerospikeFilterExpressionsBuilder() {
        return new FilterExpressionsBuilder();
    }

    @Bean(name = "aerospikeStatementBuilder")
    @ConditionalOnMissingBean(name = "aerospikeStatementBuilder")
    public StatementBuilder aerospikeStatementBuilder(IndexesCache indexesCache) {
        return new StatementBuilder(indexesCache);
    }

    @Bean(name = "aerospikeIndexResolver")
    @ConditionalOnMissingBean(name = "aerospikeIndexResolver")
    public AerospikeIndexResolver aerospikeIndexResolver() {
        return new AerospikeIndexResolver();
    }

    @Bean(name = "aerospikeIndexCache")
    @ConditionalOnMissingBean(name = "aerospikeIndexCache")
    public IndexesCacheHolder aerospikeIndexCache() {
        return new IndexesCacheHolder();
    }

    @Bean(name = "mappingAerospikeConverter")
    @ConditionalOnMissingBean(name = "mappingAerospikeConverter")
    public MappingAerospikeConverter mappingAerospikeConverter(AerospikeMappingContext aerospikeMappingContext,
                                                               AerospikeTypeAliasAccessor aerospikeTypeAliasAccessor,
                                                               AerospikeCustomConversions aerospikeCustomConversions,
                                                               AerospikeDataProperties aerospikeDataProperties) {
        return new MappingAerospikeConverter(aerospikeMappingContext, aerospikeCustomConversions,
                aerospikeTypeAliasAccessor, aerospikeDataSettings(aerospikeDataProperties));
    }

    @Bean(name = "aerospikeTypeAliasAccessor")
    @ConditionalOnMissingBean(name = "aerospikeTypeAliasAccessor")
    public AerospikeTypeAliasAccessor aerospikeTypeAliasAccessor(AerospikeDataProperties aerospikeDataProperties) {
        String typeKey = aerospikeDataProperties.getTypeKey();
        return new AerospikeTypeAliasAccessor(typeKey != null && typeKey.equals("") ? null : typeKey);
    }

    @Bean(name = "aerospikeCustomConversions")
    @ConditionalOnMissingBean(name = "aerospikeCustomConversions")
    public AerospikeCustomConversions aerospikeCustomConversions() {
        return new AerospikeCustomConversions(Collections.emptyList());
    }

    @Bean(name = "aerospikeMappingContext")
    @ConditionalOnMissingBean(name = "aerospikeMappingContext")
    public AerospikeMappingContext aerospikeMappingContext(ApplicationContext applicationContext,
                                                           AerospikeCustomConversions aerospikeCustomConversions,
                                                           AerospikeDataProperties aerospikeDataProperties)
            throws Exception {
        AerospikeMappingContext context = new AerospikeMappingContext();
        context.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class, Persistent.class));
        context.setSimpleTypeHolder(aerospikeCustomConversions.getSimpleTypeHolder());
        Class<?> fieldNamingStrategy = aerospikeDataProperties.getFieldNamingStrategy();
        if (fieldNamingStrategy != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(fieldNamingStrategy));
        }
        return context;
    }

    @Bean(name = "aerospikeExceptionTranslator")
    @ConditionalOnMissingBean(name = "aerospikeExceptionTranslator")
    public AerospikeExceptionTranslator aerospikeExceptionTranslator() {
        return new DefaultAerospikeExceptionTranslator();
    }

    private AerospikeDataSettings aerospikeDataSettings(AerospikeDataProperties aerospikeDataProperties) {
        AerospikeDataSettings.AerospikeDataSettingsBuilder builder = AerospikeDataSettings.builder();
        configureDataSettings(builder, aerospikeDataProperties);
        return builder.build();
    }

    private void configureDataSettings(AerospikeDataSettings.AerospikeDataSettingsBuilder builder,
                                         AerospikeDataProperties aerospikeDataProperties) {
        builder.scansEnabled(aerospikeDataProperties.isScansEnabled());
        builder.sendKey(aerospikeDataProperties.isSendKey());
        builder.createIndexesOnStartup(aerospikeDataProperties.isCreateIndexesOnStartup());
    }
}
