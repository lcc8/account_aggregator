# Account Validation Aggregator

The account validation aggregator takes an account number and find out if the account number is valid for one or more providers.

The application is developed in java17 and springboot. 

Request

/v1/api/account/validate

Request body contains a mandatory field ``accountNumber`` and an optional field ``providers``

Request Body example
```
{
    "accountNumber" : "timeout",
    "providers":["provider1", "provider2"]
}
```

Response body contains a list of providers with the account validation result.

Response example
```
{
    "result": [
        {
            "provider": "provider1",
            "isValid": true
        },
        {
            "provider": "provider2",
            "isValid": false
        }
    ]
}
```

## Run in different environment
Each environment configures with different providers and their urls. The configuration is set using spring profiles. 

dev please run
```
mvn spring-boot:run 
```

staging please run
```
mvn spring-boot:run -Dspring-boot.run.profiles=staging
```

production please run
```
mvn spring-boot:run -Dspring-boot.run.profiles=production
```
