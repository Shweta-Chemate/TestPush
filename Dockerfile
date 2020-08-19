FROM 539909726087.dkr.ecr.us-west-2.amazonaws.com/base-hardened-jre:stable

LABEL org.opencontainers.image.title="cxpp-training-enablement" \
      org.opencontainers.image.description="CHANGEME" \
      org.opencontainers.image.url="https://www-github3.cisco.com/vimshank/cxpp-training-enablement.git" \
      org.opencontainers.image.source="https://www-github3.cisco.com/vimshank/cxpp-training-enablement.git" \
      org.opencontainers.image.vendor="Cisco CXE" \
      org.opencontainers.image.revision="$VCS_REF" \
      org.opencontainers.image.created="$BUILD_DATE" \
      org.label-schema.schema-version="1.0" \
      org.label-schema.name="cxpp-training-enablement" \
      org.label-schema.description="CHANGEME" \
      org.label-schema.url="https://www-github3.cisco.com/vimshank/cxpp-training-enablement.git" \
      org.label-schema.vcs-url="https://www-github3.cisco.com/vimshank/cxpp-training-enablement.git" \
      org.label-schema.vendor="Cisco CXE" \
      org.label-schema.vcs-ref="$VCS_REF" \
      org.label-schema.build-date="$BUILD_DATE"

## NOTE: Run ```./add-dockerfile-label.sh``` to generate the LABEL lines here, then delete this comment and push the result into git..

RUN mkdir -p /app
WORKDIR /app

COPY target/cxpp-training-enablement-0.0.1-SNAPSHOT.jar ./cxpp-training-enablement.jar

#AppD environment variables
ENV APPD_CONF=""
ENV APPDYNAMICS_ENABLED=""
ENV JAVA_OPTS="-Xms256m -Xmx900m"

COPY docker-entrypoint.sh /bin/docker-entrypoint.sh
RUN chmod +x /bin/docker-entrypoint.sh
ENTRYPOINT ["/bin/docker-entrypoint.sh", "/app/cxpp-training-enablement.jar"]
