# Gradle 빌드를 위한 베이스 이미지
FROM gradle:7.5-jdk17 AS builder

# 소스 복사 및 작업 디렉토리 설정
WORKDIR /app
COPY . /app

# Gradle 빌드 실행
RUN gradle build -x test

# 실행을 위한 새로운 베이스 이미지
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/chatEAT-0.0.1-SNAPSHOT.jar app.jar

# application.yaml 파일 복사
COPY --from=builder /app/src/main/resources/application.yaml /app/resources/

# 환경 변수 설정
ENV MYSQL_URL=jdbc:mysql://chateat-mysql.cvesmgcuk54g.ap-northeast-2.rds.amazonaws.com:3306/chateat_mysql
ENV MYSQL_USERNAME=admin
ENV MYSQL_PASSWORD=HzwaT15iJu9EVdxewDrC

# MongoDB 환경 변수
ENV MONGODB_URI=mongodb://chateat:chateatmongodb13@10.0.2.43:27017/chateat

# Redis 환경 변수
ENV REDIS_HOST=chateat-redis-cluster.mv0dvc.ng.0001.apn2.cache.amazonaws.com
ENV REDIS_PORT=6379

# JWT, AES, OAuth2 관련 환경 변수
ENV JWT_SECRET_KEY=4P@l94*avE9N!%Am29yE35!^jUnQ#E7Z
ENV AES_SECRET_KEY=1234567890123456
ENV KAKAO_ID=039c511d6a6956cfa4c502a30b810566
ENV KAKAO_SECRET=cbkERmi9eAil451w8th7BrjUeox70iQw
ENV KAKAO_REDIRECT_URI=http://ec2-52-79-76-137.ap-northeast-2.compute.amazonaws.com:8080/login/oauth2/code/kakao
ENV FRONT_END_SERVER=http://chateat-front.s3-website.ap-northeast-2.amazonaws.com/
ENV API_URL=http://10.0.2.171:8000/process/

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]

