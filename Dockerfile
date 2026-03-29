# ১. জাভা এবং মেভেন এনভায়রনমেন্ট সেটআপ
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

# ২. রান করার জন্য ছোট জাভা ইমেজ
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
