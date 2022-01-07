FROM openjdk:14.0.2

# TODO: incorporate any good bits from the official quarkus Dockerfiles
COPY build/quarkus-app/lib/ /deployments/lib/
COPY build/quarkus-app/*.jar /deployments/
COPY build/quarkus-app/app/ /deployments/app/
COPY build/quarkus-app/quarkus/ /deployments/quarkus/

COPY run.sh /

ENV JAVA_OPTIONS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAR_FILE="/deployments/quarkus-run.jar"

ENTRYPOINT ["bash", "/run.sh"]
