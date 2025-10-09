# Etapa de build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de execução
FROM tomcat:10.1.47-jdk17
WORKDIR /usr/local/tomcat/webapps
COPY --from=build /app/target/seu-app.war ./ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
