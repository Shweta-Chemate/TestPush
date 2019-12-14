FROM containers.cisco.com/cx-platforms/base-hardened-jre:stable

RUN mkdir -p /app
WORKDIR /app

COPY target/cxpp-training-enablement-0.0.1-SNAPSHOT.jar ./cxpp-training-enablement.jar

#AppD environment variables
ENV APPD_CONF=""
ENV APPDYNAMICS_ENABLED=""

COPY docker-entrypoint.sh /bin/docker-entrypoint.sh
RUN chmod +x /bin/docker-entrypoint.sh
ENTRYPOINT ["/bin/docker-entrypoint.sh", "/app/cxpp-training-enablement.jar"]
