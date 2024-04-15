# 기본 이미지로 OpenJDK 11 사용
FROM adoptopenjdk/openjdk11

# 작업 디렉토리 설정

WORKDIR /app
COPY /build/libs/preorder-0.0.1-SNAPSHOT.jar /app/app.jar
COPY /scouter/agent.java /app/scouter/agent.java

EXPOSE 8084

# 호스트의 JAR 파일을 컨테이너의 /app 경로로 복사
COPY ./build/libs/preorder-0.0.1-SNAPSHOT.jar /app/application.jar

# 컨테이너가 실행될 때 실행될 명령어 설정
CMD ["java","-javaagent:scouter/agent.java/scouter.agent.jar" ,"-Dspring.profiles.active=dev", "-jar", "application.jar"]