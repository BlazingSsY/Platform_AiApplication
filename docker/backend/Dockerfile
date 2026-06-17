# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-17 AS build

ARG MODULE
WORKDIR /workspace

# pom 不变时这一层走 Docker 层缓存；--mount 让 ~/.m2 在所有模块、多次重建间共享，
# 即使 pom 变了也不必重新下载已有依赖。
COPY ${MODULE}/pom.xml ./pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -Dmaven.test.skip=true dependency:go-offline

COPY ${MODULE}/src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -Dmaven.test.skip=true clean package \
    && jar_file="$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' | head -n 1)" \
    && test -n "${jar_file}" \
    && cp "${jar_file}" /workspace/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8011

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
