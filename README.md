![SimOne Logo](/images/logo.png)

Base container for SimOne. SimOne is a simple simulator for REST and FEED based services.

This projects builds a jar that constitutes core simulator functionality and is meant to be included in a Java EE 8 server that is built by the final simulator.

See [SimOne-Example](https://github.com/SUNET/simone-example) for a starting point to build a simulator.

## Overview

To create a simulator instance:

```Java
@Produces
@ApplicationScoped
public SimOne create() {
    var feedRepository = new DerbyFeedRepository(dataSource);

    URI feedBaseUri = UriBuilder.fromUri(properties.baseURI).segment("feed").build();

    return SimOne.builder()
        .withName("restbucks")
        .withFeedBaseURI(feedBaseUri)
        .withFeedRepository(feedRepository)
        .withClearDatabaseFunction(() -> orderRepository.clear())
        .build();
}
```

To publish a event on the feed.

```Java
AtomEntry entry = AtomEntry.builder()
    .withAtomEntryId(uid.toString())
    .withSubmittedNow()
    .withContent(Content.builder().withValue(content).withContentType(MediaType.APPLICATION_XML).build())
    .withCategory(AtomCategory.builder().withTerm(Term.of("myterm")).withLabel(Label.of("mylabel")).build())
    .build();

simOne.publish(entry);
```

## Requirements

* Java 17

* Java Microprofile 6.0 compatible server (Tested on Quarkus 3)

* Datasource, by default uses a Apache Derby data source. The Datasource must be initialized with the Flyway migration located on the classpath.

## Build

```bash
mvn package
```
Release the SimOne jar and Docker image

```bash
mvn release:prepare release:perform
```
## Documentation

### Admin API

API to control the simulator, for example empty the database, answer all REST requests with a specific HTTP status, delay responses etc. The administrator API is documented in OpenApi. Start the [simone-example](https://github.com/SUNET/simone-example) Docker container and access the OpenApi description on <http://localhost:8080/q/openapi>

