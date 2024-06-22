package com.profITsoft.emailservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.annotation.Nonnull;

/**
 * Configuration for an Elasticsearch client.
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.address}")
    private String esAddress;

    @Override
    @Nonnull
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(esAddress)
                .build();
    }

}
