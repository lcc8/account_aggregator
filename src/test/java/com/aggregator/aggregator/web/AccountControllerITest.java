package com.aggregator.aggregator.web;

import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.core.model.ProviderValidate;
import com.aggregator.aggregator.core.service.ValidateService;
import com.aggregator.aggregator.web.converter.ConversionException;
import com.aggregator.aggregator.web.converter.ProviderValidateToValidateResponseConverter;
import com.aggregator.aggregator.web.model.ValidateResponse;
import com.aggregator.aggregator.web.model.ValidateResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerITest {
    private static final String VALIDATE_URL = "/v1/api/account/validate";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ValidateService validateServiceMock;
    @MockBean
    private ProviderValidateToValidateResponseConverter providerValidateToValidateResponseConverterMock;

    @ParameterizedTest
    @ValueSource(strings = {"{[\"accountNumber\": \"12345678\"]}",
                            "[{\"accountNumber\": \"12345678\"}]",
                            "{accountNumber: \"12345678\"]}",
                            "{\"accountNumber\": null]}",
                            "{\"accountNumber\": \"\"]}",
                            "{\"account\": \"12345678\"}",
                            "{}",
                            "",
                            "{\"accountNumber\": \"12345678\", \"providors\":[\"provider1\", \"provider2\"]}",
                            "{\"accountNumber\": \"12345678\", \"providers\":[\"provider1\", {}]}"})
    void shouldReturnErrorWhenInvalidRequestBodyGiven(String requestBody) throws Exception {
        sendValidateRequest(requestBody)
            .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"accountNumber\": \"12345678\"}",
                            "{\"accountNumber\": \"12345678\", \"providers\":[\"provider1\", \"provider2\"]}",
                            "{\"accountNumber\": \"12345678\", \"providers\":[]}",
    })
    void shouldReturnResponse(String requestBody) throws Exception {
        var serviceResponse = List.of(new ProviderValidate("provider1", true));
        var validateResponse = new ValidateResponse(List.of(new ValidateResult("provider1", true)));

        when(validateServiceMock.validate(anyString(), nullable(List.class))).thenReturn(serviceResponse);
        when(providerValidateToValidateResponseConverterMock.convert(serviceResponse)).thenReturn(validateResponse);

        sendValidateRequest(requestBody)
            .andExpect(status().isOk())
            .andExpect(mvcResult -> assertEquals("Should have the same output", mvcResult.getResponse().getContentAsString(),
                                                 objectMapper.writeValueAsString(validateResponse)));

    }

    @Test
    void shouldReturnErrorWhenValidateServiceReturnError() throws Exception {
        var requestBody = "{\"accountNumber\": \"12345678\"}";

        doThrow(new ValidateException()).when(validateServiceMock).validate(anyString(), nullable(List.class));

        sendValidateRequest(requestBody)
            .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturnErrorWhenConverterReturnError() throws Exception {
        var requestBody = "{\"accountNumber\": \"12345678\"}";

        doThrow(new ConversionException()).when(providerValidateToValidateResponseConverterMock).convert(nullable(List.class));

        sendValidateRequest(requestBody)
            .andExpect(status().is5xxServerError());
    }

    private ResultActions sendValidateRequest(String requestBody) throws Exception {
        return this.mockMvc.perform(
            post(VALIDATE_URL)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
    }
}
