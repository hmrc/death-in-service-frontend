
# Death in Service Frontend

Frontend microservice for Death in Service (DIS) which is a feature on manage your pension (MPS) service. Pension Scheme Administrators (PSA) and/or Pension Scheme Practitioners use this service for reporting Death in Service from a registered scheme

## Running the service

1. Make sure you run all the dependant services through the service manager:

   > `sm2 --start DEATH_IN_SERVICE_ALL`

2. Stop the frontend microservice from the service manager and run it locally:

   > `sm2 --stop DEATH_IN_SERVICE_FRONTEND`

   > `sbt run -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes`

The service runs on port `10712` by default.

### Unit tests

> `sbt test`

### Integration tests

> `sbt it/test`

You can also execute the [runtests.sh](runtests.sh) file to run both unit and integration tests and generate coverage report easily.

```bash
/bin/bash ./runtests.sh
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").