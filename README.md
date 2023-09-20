# Observability leakage
This sample application starts up a Spring Boot application, with a REST and GraphQL controller. 
The controller access a transactional repository, which uses an H2 database.

When starting a transaction from GraphQL, this results in an error like:
Observation <> to which we're restoring is not the same as the one set as this scope's parent observation <>. Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks.

This error causes all our tests to fail.

## Libraries used
* Java 17+
* Spring Boot 3.1.3 (>3.1.1)
* Spring GraphQl
* Micrometer Observation
* Datasource Micrometer (net.ttddyy.observation:datasource-micrometer)

## How to reproduce.
Start the Spring Boot application, with Java assertions enabled:
```
./gradlew bootRun
```

### Works:
```
curl -X POST http://localhost:8081/persons/ \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Moe","lastName":"Szyslak"}'
```

### Fails:
```
curl -X POST http://localhost:8081/graphql \
  -H 'content-type: application/json' \
  -d '{"query":"mutation {\n  addPerson(person:{firstName:\"Moe\", lastName:\"Szyslak\"}) {\n    id\n    firstName\n    lastName\n  }\n}"}'
```

## Some more info
The GraphQL does a `ObservationThreadLocalAccessor#restore` which causes this failure. Below is a "trace" from `SimpleObservationRegistry#setCurrentObservationScope`. of what happens, the numbers are the object ids (from JVM), for  
The numbers are the current's object id, the number in square brackets is the previous observation scope. The text is the (approximate) cause of the scope change.
```
14554 [null] ServerHttpObservationFilter
    14971 [14554] ContextDataFetcherDecorator#get
        [transaction interceptor]
        19439 [14971] DataSourceObservationListener#handleGetConnectionBefore
            19476 [19439] @Observed PersonServiceImpl#add
                [transaction interceptor]
                19600 [19476] beforeQuery
            19476 [19439] afterQuery
        19439 [14971] /@Observed PersonServiceImpl#add
        [restore]
    
14554 [null] /ContextDataFetcherDecorator#get
```

What you can see here is that the restore is called by (or cause by) the `ContextDataFetcherDecorator#get` method. The expected scope/value to restore to would be 14554, however, 19439 is currently active, which has 
the 14971 as parent. So the check in `ObservationThreadLocalAccessor#restore` fails. The transaction interceptor obtains a datasource, which is also observed. This observation is not closed when the GraphQl handling takes over again.

This has something to do with transactions and/or GraphQl. The REST equivalent of the addPerson works.
