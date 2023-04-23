package com.aggregator.aggregator.web;

import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.core.service.ValidateService;
import com.aggregator.aggregator.web.converter.ProviderValidateToValidateResponseConverter;
import com.aggregator.aggregator.web.model.ValidateRequest;
import com.aggregator.aggregator.web.model.ValidateResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/v1/api/account")
public class AccountController {

    private final ValidateService validationService;
    private final ProviderValidateToValidateResponseConverter converter;

    public AccountController(ValidateService validationService,
                             ProviderValidateToValidateResponseConverter providerValidateToValidateResponseConverter) {
        this.validationService = validationService;
        this.converter = providerValidateToValidateResponseConverter;
    }

    @PostMapping(path = "/validate")
    public ValidateResponse validate(@RequestBody @Valid ValidateRequest validateRequest) throws ValidateException, TimeoutException {
        var serviceResponse = validationService.validate(validateRequest.accountNumber(), validateRequest.providers());
        return converter.convert(serviceResponse);
    }
}
