# Usando imagem base do OpenJDK
FROM openjdk:17
# Define o diretório de trabalho dentro do container
WORKDIR /app
# Copia todos os arquivos do projeto para dentro do container
COPY . .
# Comando para compilar seu projeto Java (ajuste conforme seu projeto)
RUN javac Main.java
# Comando para rodar seu programa
CMD ["java", "Main"]
