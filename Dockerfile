# --- Stage 1: Build the application ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Копіюємо pom.xml усіх модулів та батьківський pom
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml

# Завантажуємо залежності (щоб закешувати цей шар)
RUN mvn dependency:go-offline -B

# Копіюємо вихідний код
COPY core/src core/src
COPY persistence/src persistence/src
COPY web/src web/src

# Збираємо проєкт, пропускаючи тести (тести запускатимемо в GitHub Actions)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копіюємо лише скомпільований JAR файл веб-модуля з першого етапу
COPY --from=build /app/web/target/web-*.jar app.jar

# Render очікує порт 8080
EXPOSE 8080

# Команда запуску
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]