FROM openjdk:15.0.2-slim as build-project
COPY . ./build-folder
WORKDIR ./build-folder

RUN chmod +x ./mvnw
RUN ./mvnw clean
RUN ./mvnw package -Dmaven.test.skip=true


FROM openjdk:15.0.2-slim
COPY --from=build-project ./build-folder/target/*.jar /app/my-service.jar
# COPY --from=build-project ./build-folder/entrypoint.sh /app/entrypoint.sh

WORKDIR /app

# RUN chmod 777 entrypoint.sh

#ENV ARTIFACT_NAME=my-service.jar
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "my-service.jar"]
# ENTRYPOINT ['./entrypoint.sh']

