package com.aggregator.aggregator.core.service;

import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.core.model.ProviderValidate;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface ValidateService {
    List<ProviderValidate> validate(String accountNumber, List<String> providers) throws TimeoutException, ValidateException;
}
