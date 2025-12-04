# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -Dmaven.test.skip=true dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 复制字体文件(需要提前准备好字体文件)
COPY fonts/*.ttf /usr/share/fonts/chinese/
COPY fonts/*.ttc /usr/share/fonts/chinese/

# 安装中文字体
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    fontconfig \
    fonts-wqy-zenhei \
    fonts-wqy-microhei \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]