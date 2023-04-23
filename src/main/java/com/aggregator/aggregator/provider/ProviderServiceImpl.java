package com.aggregator.aggregator.provider;

public class ProviderServiceImpl implements ProviderService{
    private String name;
    private String url;

    public ProviderServiceImpl(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public ProviderResponse validateAccount(String accountNumber) {
        // this method should make a request via http client to a provider with request account number as input
        // then it will return the provider response containing the provider name and the validation result
        // for this exercise, it will just return a dummy value

        // dummy implementation to simulate provider1 responses slow to timeout account
        if(accountNumber.equals("timeout") && name.equals("provider1")){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        // dummy implementation to simulate providers taking 1.5s to response
        if(accountNumber.equals("timeout2")){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        return new ProviderResponse(name.hashCode() % 2 == 0);
    }
}
