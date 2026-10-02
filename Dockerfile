# ============================================================
# 多阶段构建：第一阶段用 Maven 打包，第二阶段只保留 JDK + jar
# 注意：jar 名由 pom.xml 的 artifactId + version 决定，
#       改名/升版本时要同步修改下面的 COPY 路径
# ============================================================

# ---- 构建阶段 ----
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /app

# 只复制必要的源代码和配置文件
# （application-local.yml 被 .dockerignore 挡住，真实密钥不会进镜像）
COPY pom.xml .
COPY src ./src

# 使用 Maven 执行打包
RUN mvn clean package -DskipTests

# ---- 运行阶段：不含 Maven 和源码，镜像更小、Serverless 冷启动更快 ----
FROM amazoncorretto:21
WORKDIR /app

COPY --from=builder /app/target/hui-ai-agent-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用端口（Serverless 平台若有指定端口要求，用环境变量 SERVER_PORT 覆盖）
EXPOSE 8123

# MaxRAMPercentage 让 JVM 堆按容器内存自适应，适合 Serverless 限额场景
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar", "--spring.profiles.active=prod"]
