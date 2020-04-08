![SimOne Logo](/images/logo.png)

Base container for SimOne. SimOne is a simple simulator for REST and FEED based services.

This projects builds a jar that constitutes core simulator functionality and is meant to be included in a Java EE 8 server that is built by the final simulator.

See [SimOne-Example](https://github.com/SUNET/simone-example) for a starting point to build a simulator.

## Overview

![SimOne Overview](/images/overview.png)

Atom feed API
: Publish the atom feed.

Example REST API
: The REST API to simulate, implemented by the SimOne-example in this case.

Admin REST API
: API to control the SimOne simulator, see [Admin API](#admin-api) for more information.

Extension API
: API that notifies the SimOne simulator about actions initiated from the admin API, or create feed entries.

```Java
void se.uhr.simone.extension.api.feed.FeedPublisher#publish(AtomEntry entry);
```

## Requirements

* Java 11

* Java Microprofile 3.3 compatible server

* Datasource, The application is required to produce a CDI bean of type `javax.sql.DataSource` with qualifier `@FeedDS` to be used by the feed server. The Datasource must be initilized with flyway migration located on the classpath.

* A CDI event `se.uhr.simone.extension.api.SimoneStartupEvent` must be fired when the server is started and ready.

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

API to control the simulator, for example empty the database, answer all REST requests with a specific HTTP status, delay responses etc. The administrator API is documented in Swagger. Start the [simone-example](https://github.com/SUNET/simone-example) Docker container and point your Browser to <http://localhost:8080/openapi>

## Configuration

Simone uses MicroProfile Config to inject the configuration in the application:

`simone.base.uri`
:  The base URI of SimOne, used to reference the SimOne server in the Atom Feed. Default value is `http://localhost:8080`

`simone.dropin`
: is a special directory that is monitored by SimOne. When a new file is discovered extensions are notified and may handle the file in any way they want. The default value is `dropin` in cwd.
