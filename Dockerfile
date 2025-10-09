# -----------------------------------------------------------
# Estágio 1: Compilação (Build Stage)
# -----------------------------------------------------------
FROM maven:3.8.4-openjdk-17 AS build

# Define o diretório base para o código (pasta "mocotattoo-backend-java" do seu projeto)
# Mantenha o WORKDIR em minúsculas se a pasta for em minúsculas, ou use a capitalização EXATA.
WORKDIR /app/mocotattoo-backend-java 

# Copia apenas o conteúdo da pasta do projeto para o WORKDIR correto.
# Como o Dockerfile está na raiz do repo e o código está em um subdiretório,
# você precisa COPIAR o subdiretório para dentro do WORKDIR.

# Se o Dockerfile está na raiz e a pasta do projeto é 'mocotattoo-backend-java':
COPY ./mocotattoo-backend-java .

# Se a sua pasta do projeto tem outra capitalização (ex: 'MocoTattoo'):
# COPY ./MocoTattoo .

# Executa a compilação.
RUN mvn clean package -DskipTests


# -----------------------------------------------------------
# Estágio 2: Execução (Runtime Stage)
# -----------------------------------------------------------
FROM tomcat:10.1.47-jdk17

# WORKDIR para o Tomcat
WORKDIR /usr/local/tomcat/webapps

# Copia o arquivo .war gerado
COPY --from=build /app/mocotattoo-backend-java/target/*.war /usr/local/tomcat/webapps/ROOT.war

# O comando "catalina run" (CMD)
# A imagem oficial do Tomcat JÁ POSSUI o comando de execução.
# Se quiser ser explícito, adicione:
 CMD ["catalina.sh", "run"]
