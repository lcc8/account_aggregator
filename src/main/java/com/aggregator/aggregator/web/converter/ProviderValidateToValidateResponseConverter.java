package com.aggregator.aggregator.web.converter;

import com.aggregator.aggregator.core.model.ProviderValidate;
import com.aggregator.aggregator.web.model.ValidateResponse;
import com.aggregator.aggregator.web.model.ValidateResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProviderValidateToValidateResponseConverter {
    public ValidateResponse convert(List<ProviderValidate> providerValidates) {
        if(providerValidates == null){
            throw new ConversionException();
        }

        var validateResult = providerValidates.stream().map(
            providerValidate -> new ValidateResult(providerValidate.provider(), providerValidate.isValid())).collect(Collectors.toList());

        return new ValidateResponse(validateResult);
    }
}
