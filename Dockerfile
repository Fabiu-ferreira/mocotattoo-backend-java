# -----------------------------------------------------------
# Estágio 1: Compilação (Build Stage)
# -----------------------------------------------------------
FROM maven:3.8.4-openjdk-17 AS build

# Define o diretório de trabalho para onde vamos COPIAR o código no container.
WORKDIR /app

# Copia o código-fonte do repositório para o diretório de trabalho no container.
# O ponto ( . ) significa "tudo na raiz do contexto de compilação do Docker" (que é a raiz do seu repositório).
COPY . .

# Altera o diretório de trabalho para a pasta onde está o pom.xml
# Substitua 'mocotattoo-backend-java' pelo nome da pasta que tem o pom.xml.
WORKDIR /app/mocotattoo-backend-java

# Executa a compilação.
RUN mvn clean package -DskipTests

# -----------------------------------------------------------
# Estágio 2: Execução (Runtime Stage)
# -----------------------------------------------------------
FROM tomcat:10.1.47-jdk17

# O WORKDIR e a COPY para o Tomcat devem ser ajustados 
# para pegar o arquivo .war gerado no estágio 'build'.

WORKDIR /usr/local/tomcat/webapps
# Copia o arquivo .war gerado (mocotattoo-backend-java-*.war) para o diretório webapps do Tomcat.
# Você precisará saber o nome exato do arquivo .war gerado.
# Assumindo que o nome do artefato é 'target/mocotattoo-backend-java.war'
COPY --from=build /app/mocotattoo-backend-java/target/*.war /usr/local/tomcat/webapps/ROOT.war
# Definir como ROOT.war garante que o site seja acessado pela raiz.

 CMD ["catalina.sh", "run"]
