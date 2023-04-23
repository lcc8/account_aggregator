package com.aggregator.aggregator.provider.config;

import com.aggregator.aggregator.core.model.Provider;
import com.aggregator.aggregator.provider.ProviderService;
import com.aggregator.aggregator.provider.ProviderServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "agg")
public class ProvidersConfiguration {

    private List<Provider> providers;

    @Bean
    public Map<String, ProviderService> providerServices() {
        var providerMap = new HashMap<String, ProviderService>();

        for (Provider provider : providers) {
            var providerService = new ProviderServiceImpl(provider.name(), provider.url());
            providerMap.put(provider.name(), providerService);
        }
        return providerMap;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }
}
