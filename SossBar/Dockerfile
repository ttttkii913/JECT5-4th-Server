# 1. 빌드 단계 (JDK 사용)
FROM amazoncorretto:17 AS builder
WORKDIR /app

# 빌드 파일 복사
COPY SossBar/gradlew .
COPY SossBar/gradle gradle
COPY SossBar/build.gradle .
COPY SossBar/settings.gradle .
COPY SossBar/src src

# 권한 부여 및 빌드 (bootJar)
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 2. 실행 단계 (가벼운 설정을 위해 JDK 대신 일반 이미지 사용 가능)
FROM amazoncorretto:17
WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]