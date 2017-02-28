FROM casadocker/alpine-openjdk:8

# Set timezone to Sweden
RUN apk --update add tzdata && ln -fs /usr/share/zoneinfo/Europe/Stockholm /etc/localtime

RUN mkdir -p /opt/jboss \
    && mkdir -p /var/simone

RUN adduser -D -h /opt/jboss jboss

RUN chown jboss:0 /var/simone

WORKDIR /opt/jboss
USER jboss

ENV WILDFLY_VERSION 10.1.0.Final
ENV WILDFLY_SHA1 9ee3c0255e2e6007d502223916cefad2a1a5e333
ENV DOWNLOAD_URL=https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz

RUN curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz

RUN ln -s wildfly-$WILDFLY_VERSION wildfly && rm -f wildfly-$WILDFLY_VERSION.tar.gz

# http
EXPOSE 8080
# Jvm remote debug
EXPOSE 8787
# Jboss mangement
EXPOSE 9990
# Derby NW Server
EXPOSE 1527

ENV JBOSS_HOME /opt/jboss/wildfly

# add management user (username: admin, passwd: simone)
RUN /opt/jboss/wildfly/bin/add-user.sh admin simone --silent

RUN mkdir -p /var/simone/db \
    && mkdir -p /var/simone/dropin

COPY derby.jar /tmp/
COPY derbynet.jar /tmp/
COPY jboss-setup.sh /tmp
COPY commands.cli /tmp

WORKDIR /tmp

RUN /bin/bash /tmp/jboss-setup.sh

