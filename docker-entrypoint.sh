#!/bin/sh

#Appd configuration
if [ ! -z "$APPDYNAMICS_ENABLED" ]; then
APPD_CONF="-javaagent:/AppServerAgent/javaagent.jar -Dappdynamics.agent.uniqueHostId=$(sed -rn '1s#.*/##; 1s/(.{12}).*/\1/p' /proc/self/cgroup) ${APPDYNAMICS_JVM_PARAMETER}"
fi

PROXY_SETTING="-Dhttp.proxyHost=${HTTP_PROXY} -Dhttp.proxyPort=${HTTP_PORT} -Dhttps.proxyHost=${HTTPS_PROXY} -Dhttps.proxyPort=${HTTPS_PORT} -Dhttp.nonProxyHosts=${NON_PROXY}"
#running spring boot app
java ${PROXY_SETTING} ${APPD_CONF} -Xms50m -Xmx350m -XX:+UseG1GC -jar $1
