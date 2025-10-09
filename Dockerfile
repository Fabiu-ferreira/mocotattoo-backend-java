# -----------------------------------------------------------
# Estágio 1: Empacotamento (Build Stage)
# Usaremos uma imagem OpenJDK para criar o WAR manualmente.
# -----------------------------------------------------------
FROM openjdk:17-jdk-slim AS build

# Define o diretório de trabalho como a raiz do app.
WORKDIR /app

# Copia todos os seus arquivos (incluindo WEB-INF, META-INF, etc.)
COPY . .

# Comando para criar o arquivo WAR. 
# Ele comprime todos os arquivos no diretório atual em um arquivo WAR.
# O nome do arquivo WAR deve ser o que o Tomcat espera (ex: app.war ou ROOT.war).
# O nome "ROOT.war" garante que seja acessível na URL raiz (/).
RUN apt-get update && apt-get install -y zip # Instala o zip se não estiver presente
RUN zip -r ROOT.war ./*

# -----------------------------------------------------------
# Estágio 2: Execução (Runtime Stage)
# Usa o Tomcat para rodar o arquivo WAR gerado
# -----------------------------------------------------------
FROM tomcat:10.1.28-jdk17

# Define o diretório de webapps do Tomcat
WORKDIR /usr/local/tomcat/webapps

# Copia o arquivo ROOT.war gerado no Estágio 1 (build) para o diretório de webapps
COPY --from=build /app/ROOT.war .

# O Tomcat já está configurado para iniciar o catalina.sh run.
# Se precisar de portas específicas (padrão 8080):
EXPOSE 8080
