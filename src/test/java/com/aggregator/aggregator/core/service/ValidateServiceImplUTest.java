package com.aggregator.aggregator.core.service;

import com.aggregator.aggregator.core.exception.InvalidProviderException;
import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.core.model.ProviderValidate;
import com.aggregator.aggregator.provider.ProviderResponse;
import com.aggregator.aggregator.provider.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidateServiceImplUTest {

    private static final String ACCOUNT_NUMBER = "12345678";

    @Mock
    private ProviderService provider1;
    @Mock
    private ProviderService provider2;
    private Map<String, ProviderService> providerServicesMock;

    private ValidateService classToTest;

    @BeforeEach
    public void setup() {
        providerServicesMock = Map.of("provider1", provider1, "provider2", provider2);
        classToTest = new ValidateServiceImpl(providerServicesMock);
    }

    @Test
    void shouldCallAllProvidersWhenNoneGiven() throws ValidateException, TimeoutException {
        when(provider1.validateAccount(ACCOUNT_NUMBER)).thenReturn(new ProviderResponse(true));
        when(provider2.validateAccount(ACCOUNT_NUMBER)).thenReturn(new ProviderResponse(false));

        var actual = classToTest.validate(ACCOUNT_NUMBER, List.of());

        verify(provider1).validateAccount(ACCOUNT_NUMBER);
        verify(provider2).validateAccount(ACCOUNT_NUMBER);

        var expected = List.of(new ProviderValidate("provider1", true), new ProviderValidate("provider2", false));
        assertTrue("Should call all providers",
                   actual.size() == expected.size() && actual.containsAll(expected) && expected.containsAll(actual));
    }

    @Test
    void shouldCallAllProvidersWhenNoProviderGiven() throws ValidateException, TimeoutException {

        when(provider1.validateAccount(ACCOUNT_NUMBER)).thenReturn(new ProviderResponse(true));
        when(provider2.validateAccount(ACCOUNT_NUMBER)).thenReturn(new ProviderResponse(false));

        var actual = classToTest.validate(ACCOUNT_NUMBER, null);

        verify(provider1).validateAccount(ACCOUNT_NUMBER);
        verify(provider2).validateAccount(ACCOUNT_NUMBER);

        var expected = List.of(new ProviderValidate("provider1", true), new ProviderValidate("provider2", false));
        assertTrue("Should call all providers",
                   actual.size() == expected.size() && actual.containsAll(expected) && expected.containsAll(actual));
    }

    @Test
    void shouldCallGivenProviders() throws ValidateException, TimeoutException {
        when(provider1.validateAccount(ACCOUNT_NUMBER)).thenReturn(new ProviderResponse(true));

        var actual = classToTest.validate(ACCOUNT_NUMBER, List.of("provider1"));

        verify(provider1).validateAccount(ACCOUNT_NUMBER);
        verifyNoInteractions(provider2);

        var expected = List.of(new ProviderValidate("provider1", true));
        assertTrue("Should call all providers",
                   actual.size() == expected.size() && actual.containsAll(expected) && expected.containsAll(actual));
    }

    @Test
    void shouldErrorIfOneOfProviderFails() {
        doThrow(new RuntimeException()).when(provider1).validateAccount(ACCOUNT_NUMBER);

        assertThrows(ValidateException.class, () -> classToTest.validate(ACCOUNT_NUMBER, null));
    }

    @Test
    void shouldErrorProviderDoesNotExist() {
        assertThrows(InvalidProviderException.class, () -> classToTest.validate(ACCOUNT_NUMBER, List.of("UNKNOWN")));
    }

    @Test
    void shouldErrorWhenExecutionLongerThan2Seconds(){
        when(provider1.validateAccount(ACCOUNT_NUMBER)).thenAnswer((Answer<ProviderResponse>) invocationOnMock -> {
            Thread.sleep(2001);
            return new ProviderResponse(true);
        });

        assertThrows(TimeoutException.class, () -> classToTest.validate(ACCOUNT_NUMBER, List.of("provider1")));
    }
}
