![SimOne Logo](/images/logo.png)

Base container for SimOne. SimOne is a simple simulator for REST and FEED based services.

This projects builds a jar that constitutes core simulator functionality and is meant to be included in a war that is built by the final simulator. The projects also provides a Docker image that is meant to be used as a base for the final simulator Docker image.

See [SimOne-Example](https://github.com/SUNET/simone-example) for a simple demonstration of how to build a simulator.

## Overview

![SimOne Overview](/images/overview.png)

Atom feed API
: Publish the atom feed.

Example REST API
: The REST API to simulatate, implmented by the simone-example in this case.

Admin REST API
: API to control the SimOne simulator, see [Admin API](#Admin API) for more information.

Extension API
: API that notifies the SimOne simulator about actions initiated from the admin API, or create feed entries.

```Java
void publish(AtomEntry entry);
```

## Docker view

![SimOne Overview](/images/docker.png)

Base
: The base image, currently alpine-openjdk

SimOne
: The image created by this project: Wildfly, Derby etc. See [Dockerfile](simone.docker/docker/Dockerfile)

SomOne-Example
: Deploys the SomOne-Example war file to the wildfly.

## Build

Build the SimOne core jar and Docker image.

```bash
mvn package
```
Release the SimOne jar and Docker image

```bash
mvn release:prepare release:perform
```
## Documentation

### Admin API

API to control the simulator, for example empty the database, answer all REST requests with a specific HTTP status, delay responses etc. The administrator API is documented in Swagger. Start the [simone-example](https://github.com/SUNET/simone-example) Docker container and point your Browser to <http://localhost:8080/sim/doc>

## Environment variables
`SIMONE_BASE_URI`
:  The base URI of SimOne, used to reference the SimOne server in the Atom Feed. Default value is `http://localhost:8080`    

## Files

### Logfiles

Logfiles are stored under `/opt/jboss/wildfly/standalone/log`

### Database

All database files are stored under `/var/simone/db`

### Dropin

`dropin` is a special directory that is monitored by SimOne. When a new file is discovered extensions are notified and may handle the file in any way they want. The dropin directory location is `/var/simone/dropin`

## Debug

### Remote debug the application

You can remote debug the application in the running container by hooking up jdb to port 8787. Note that you also must expose the port in the docker run command.

```bash
jdb -attach 8787
```

### Inspect the Feed Database

Feed information is stored in a Apache Derby database. Use driver `org.apache.derby.jdbc.ClientDriver` the jar (derbyclient.jar) is included in the standard Java JRE installation. Note that you also must expose port `1527` in the docker run command.

URL
: jdbc:derby://localhost:1527/feed

Username
: sa

Password
: admin

# Known problems

* It is not possible to load SimOne by selecting a file in Swagger.

# Todo

* Currently uses a private Docker registry (https://hub.docker.com/r/attiand/simone/).
