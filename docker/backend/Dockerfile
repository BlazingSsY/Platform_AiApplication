FROM maven:3.9-eclipse-temurin-17 AS build

ARG MODULE
WORKDIR /workspace

COPY ${MODULE}/pom.xml ./pom.xml
RUN mvn -B -Dmaven.test.skip=true dependency:go-offline

COPY ${MODULE}/src ./src
RUN mvn -B -Dmaven.test.skip=true clean package \
    && jar_file="$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' | head -n 1)" \
    && test -n "${jar_file}" \
    && cp "${jar_file}" /workspace/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8011

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
