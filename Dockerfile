# 1. Gradle 빌드를 위한 베이스 이미지
FROM gradle:7.5-jdk17 AS builder

# 2. 소스 복사 및 작업 디렉토리 설정
WORKDIR /app
COPY . /app

# 3. Gradle 빌드 실행
RUN gradle build -x test

# 4. 실행을 위한 새로운 베이스 이미지
FROM openjdk:17-jdk-slim

# 5. 빌드된 JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/chatEAT-0.0.1-SNAPSHOT.jar app.jar

# 6. 애플리케이션 포트 노출
EXPOSE 8080

# 7. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
