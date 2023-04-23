package com.aggregator.aggregator.provider.config;

import com.aggregator.aggregator.core.model.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(classes = ProvidersConfiguration.class)
class ProvidersConfigurationITest {

    @Autowired
    private ProvidersConfiguration classToTest;

    @Test
    void testGetProviders() {
        var actual = classToTest.getProviders();
        var expected = List.of(new Provider("provider1", "https://provider1.com/v1/api/account/validate"),
                               new Provider("provider2", "https://provider2.com/v2/api/account/validate"));
        assertEquals("Expected to have 2 providers", expected, actual);
    }

    @Test
    void testGetProviderServices() {
        var actual = classToTest.providerServices();

        assertTrue("Expected to have 2 provider services", actual.containsKey("provider1"));
        assertTrue("Expected to have 2 provider services", actual.containsKey("provider2"));
    }
}
