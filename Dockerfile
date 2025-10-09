FROM maven:3.8.4-openjdk-17 AS build

# Define o diretório de trabalho como a raiz do app.
# O pom.xml está aqui.
WORKDIR /app

# Copia todo o conteúdo do repositório (incluindo o pom.xml e o código-fonte) para /app
# O Render faz o checkout do seu repo e o "." pega tudo.
COPY . .

# Executa a compilação.
# Como o pom.xml está em /app, este comando VAI FUNCIONAR AGORA.
RUN mvn clean package -DskipTests 


# -----------------------------------------------------------
# Estágio 2: Execução (Runtime Stage)
# Usa o Tomcat para rodar o arquivo WAR gerado
# -----------------------------------------------------------
FROM tomcat:10.1.47-jdk17

# Define o diretório de webapps do Tomcat
WORKDIR /usr/local/tomcat/webapps

# Copia o arquivo .war gerado no Estágio 1 (build) e renomeia para ROOT.war.
# O arquivo .war está em /app/target/ dentro do estágio "build".
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# O comando para iniciar o Tomcat (o "catalina run")
# A imagem oficial do Tomcat (tomcat:10.1.47-jdk17) já usa o comando catalina.sh run
# por padrão como seu ENTRYPOINT ou CMD, então você não precisa adicioná-lo. 
# Se você quiser ser explícito:
 CMD ["catalina.sh", "run"]
