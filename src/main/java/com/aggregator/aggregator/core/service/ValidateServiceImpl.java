package com.aggregator.aggregator.core.service;

import com.aggregator.aggregator.core.exception.InvalidProviderException;
import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.core.model.ProviderValidate;
import com.aggregator.aggregator.provider.ProviderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class ValidateServiceImpl implements ValidateService {

    private final Map<String, ProviderService> providerServices;

    public ValidateServiceImpl(Map<String, ProviderService> providerServices) {
        this.providerServices = providerServices;
    }

    @Override
    public List<ProviderValidate> validate(String accountNumber,
                                           List<String> providers) throws ValidateException, TimeoutException {

        if (providers == null || providers.isEmpty()) {
            providers = providerServices.keySet().stream().toList();
        }else{
            providers.forEach(provider -> {
                if(!providerServices.containsKey(provider)){
                    throw new InvalidProviderException("Provider " + provider + " does not exist.");
                }
            });
        }

        var executor = Executors.newFixedThreadPool(providers.size());

        var completableFutures = providers.stream()
                                          .map(provider -> getProviderValidate(accountNumber, provider, executor))
                                          .collect(Collectors.toList());

        var allFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

        var allCompletableFuture = allFutures.thenApply(future -> completableFutures.stream()
                                                                                    .map(completableFuture -> completableFuture.join())
                                                                                    .collect(Collectors.toList()));

        try{
            return allCompletableFuture.get(2, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            throw new ValidateException();
        } catch (TimeoutException e){
            throw e;
        }
    }

    private CompletableFuture<ProviderValidate> getProviderValidate(String accountNumber, String provider, Executor executor) {
        // other validations
        return CompletableFuture.supplyAsync(() -> {
            var providerResponse = providerServices.get(provider).validateAccount(accountNumber);
            return new ProviderValidate(provider, providerResponse.isValid());
        }, executor);
    }
}
