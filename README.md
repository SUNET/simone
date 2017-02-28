# SimOne

Base container for SimOne. SimOne is a simple simulator for REST and FEED based services. See [SimOne-Example](https://github.com/SUNET/simone-example) for a simple demonstration of how to build a simulator.


## Build
First build the SimOne core jar.

```bash
mvn install
```
Then build the SimOne base Docker container.

```bash
mvn --projects simone.docker package docker:build 
```

## Push the Docker container
```bash
mvn --projects simone.docker docker:push
```
## Documentation

### Admin API

The administrator API is documented in Swagger. Start the [simone-example](https://github.com/SUNET/simone-example) Docker container and point a Browser to <http://localhost:8080/sim/doc>

## Environment variables
`SIMONE_BASE_URI`
:  The base URI of SimOne used to reference the SimOne server in the Atom Feed. Default value is `http://localhost:8080`    

## Directories

### Logfiles

All logfiles are stored under `/opt/jboss/wildfly/standalone/log`

### Database

All database files are stored under `/var/simone/db`

### Dropin

dropin is a special directory that is monitored by SimOne. When a new file is discovered extensions are notified and may handle the file in any way they want. The dropin directory is located at `/var/simone/dropin`

## Debug

### Remote debug the application

You can remote debug the application in the running container by hooking up jdb to port 8787. Note that you also must expose the port in the docker run command.

```bash
jdb -attach 8787
```

### Inspect the Feed Database

The Feed dagabase is stored in Apache Derby.  Use driver `org.apache.derby.jdbc.ClientDriver` the jar (derbyclient.jar) is included in the standard Java JRE. Nnote that you also must expose port `1527` in the docker run command.

URL
: jdbc:derby://localhost:1527/feed

Username
: sa

Password
: admin

# Known Problems

* It is not possible to load SimOne by selecting a file in Swagger.

# Todo

* Currently uses Maven snapshot versions. 

* Currently use private Docker registry.
