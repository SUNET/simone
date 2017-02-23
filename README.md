# SimOne

Base container for SimOne. SimOne is a simple simulator for REST and FEED based services. 

## Build
First build the .jar

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
