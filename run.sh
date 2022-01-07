#!/usr/bin/env bash

set -eu -o pipefail

# adapted from http://blog.christianposta.com/kubernetes/java-remote-debug-for-applications-running-in-kubernetes/

# Set debug options if required
if [[ ${JAVA_DEBUG_ENABLED:-0} -eq 1 ]]; then
    if [[ ${JAVA_DEBUG_SUSPEND:-0} -eq 1 ]]; then
        susp="y"
    else
        susp="n"
    fi
    java_debug_args="-agentlib:jdwp=transport=dt_socket,server=y,suspend=${susp},address=${JAVA_DEBUG_PORT:-5005}"
fi

# https://stackoverflow.com/a/59381730
if [[ ${JAVA_JMX_ENABLED:-0} -eq 1 ]]; then
    java_jmx_args=(
        "-Dcom.sun.management.jmxremote=true"
        "-Dcom.sun.management.jmxremote.port=${JAVA_JMX_PORT:-1099}"
        "-Dcom.sun.management.jmxremote.local.only=false"
        "-Dcom.sun.management.jmxremote.authenticate=false"
        "-Dcom.sun.management.jmxremote.ssl=false"
        "-Dcom.sun.management.jmxremote.rmi.port=${JAVA_JMX_PORT:-1099}"
        "-Djava.rmi.server.hostname=localhost")
fi

# TODO: hard-code a default value once a safe one has been determined
if [[ ${JAVA_HEAP_PCT:-50} -gt 0 ]]; then
    echo "JAVA_HEAP_PCT=${JAVA_HEAP_PCT:-50}"
    java_memory_args=(
        "-XX:MaxRAMPercentage=${JAVA_HEAP_PCT:-50}"
        "-XX:InitialRAMPercentage=${JAVA_HEAP_PCT:-50}")
fi

if [[ ${JAVA_PRINT_FINAL_FLAGS:-0} -eq 1 ]]; then
    java_xx_flags="${java_xx_flags:-} -XX:+PrintFlagsFinal"
fi

if [[ ${JAVA_PRINT_GC:-0} -eq 1 ]]; then
    java_xx_flags="${java_xx_flags:-} -XX:+PrintGCDetails"
fi

exec java \
    ${java_debug_args:-} \
    ${java_jmx_args[@]:-} \
    ${java_memory_args[@]:-} \
    ${java_xx_flags:-} \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+UnlockDiagnosticVMOptions \
    -XX:+DebugNonSafepoints \
    -Djgroups.dns.query=rolling-updates-test-ping \
    -Djava.net.preferIPv4Stack=true \
    -Djava.security.egd=file:/dev/./urandom \
    --illegal-access=permit \
    ${JAVA_OPTIONS:-} \
    "$@" \
    -jar ${JAR_FILE:-/app.jar}

#    -XX:+FlightRecorder \
#    -XX:StartFlightRecording=delay=230s,duration=1m,name=Default,filename=recording.jfr,settings=default \
#    -Xlog:jfr=info \
