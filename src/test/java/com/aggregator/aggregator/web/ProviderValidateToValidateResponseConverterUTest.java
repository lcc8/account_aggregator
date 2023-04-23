package com.aggregator.aggregator.web;

import com.aggregator.aggregator.core.model.ProviderValidate;
import com.aggregator.aggregator.web.converter.ConversionException;
import com.aggregator.aggregator.web.converter.ProviderValidateToValidateResponseConverter;
import com.aggregator.aggregator.web.model.ValidateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProviderValidateToValidateResponseConverterUTest {

    private ProviderValidateToValidateResponseConverter classToTest;

    @BeforeEach
    public void setup(){
        classToTest = new ProviderValidateToValidateResponseConverter();
    }

    @Test
    void shouldConvert() {
        var providerValidates = List.of(new ProviderValidate("p1", true), new ProviderValidate("p2", false));
        var actual = classToTest.convert(providerValidates);
        var expected = List.of(new ValidateResult("p1", true), new ValidateResult("p2", false));
        assertEquals(expected, actual.result());
    }

    @Test
    void shouldConvertEmpty() {
        List<ProviderValidate> providerValidates = List.of();
        var actual = classToTest.convert(providerValidates);
        var expected = List.of();
        assertEquals(expected, actual.result());
    }

    @Test
    void shouldErrorWhenConvertNull() {
        assertThrows(ConversionException.class, () -> classToTest.convert(null));
    }
}
