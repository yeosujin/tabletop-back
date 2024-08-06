# Maven 설치
RUN apt-get update && apt-get install -y maven

# 실행 권한 부여
RUN chmod +x ./mvnw

# 애플리케이션 컴파일 (테스트 실행 제외)
RUN ./mvnw clean package -DskipTests

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod
ENV LOGGING_LEVEL_ROOT=INFO

# 포트 노출
EXPOSE 8080

# 헬스 체크 설정
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
CMD ["java", "-jar", "target/tabletop-back-0.0.1-SNAPSHOT.jar"]

