# Hui AI Agent 🤖

一个基于 **Spring Boot 3 + Spring AI + 阿里云灵积（DashScope）** 从 0 到 1 打造的 AI 智能体全栈项目，包含两个完整的 AI 应用：

1. **AI 恋爱大师（LoveApp）**——一个带系统人设、多轮记忆、知识库检索（RAG）的垂直领域对话应用；
2. **HuiManus 超级智能体**——一个参考 Manus / OpenManus 架构、手写 ReAct（思考-行动）循环、可自主调用 7 种工具完成复杂任务的通用智能体。

项目还包含 **Vue 3 前端**（SSE 流式聊天界面）、**Docker / Serverless 部署配置**，以及大量可用于教学的示例代码（大模型调用、对话记忆、RAG、工具调用）。

> 📚 想系统学习本项目？请阅读 [STUDY.md](./STUDY.md)——一份按 git 提交历史编排、面向小白的从 0 到 1 教学文档。

---

## 目录

- [核心功能](#核心功能)
- [技术选型](#技术选型)
- [项目结构](#项目结构)
- [业务逻辑](#业务逻辑)
- [快速开始](#快速开始)
- [API 接口](#api-接口)
- [测试](#测试)
- [Docker 与 Serverless 部署](#docker-与-serverless-部署)
- [常见问题 FAQ](#常见问题-faq)

---

## 核心功能

### 1. AI 恋爱大师（LoveApp）

| 能力 | 说明 | 对应方法（`LoveApp`） |
| --- | --- | --- |
| 人设对话 | 通过系统提示词让 AI 扮演恋爱心理专家，按单身/恋爱/已婚三种状态引导提问 | `doChat` |
| 流式输出 | SSE 逐字返回，前端打字机效果 | `doChatByStream` |
| 结构化输出 | 让 AI 直接返回 JSON 并映射为 Java 对象（`LoveReport` record） | `doChatWithReport` |
| 多轮对话记忆 | 支持**内存 / 文件（Kryo 序列化）/ MySQL** 三种可切换的记忆实现 | 构造器中切换 `ChatMemory` |
| RAG 知识库问答 | 加载 `resources/document/` 下 3 份 Markdown 知识文档，向量化后检索增强回答 | `doChatWithRag` |
| 查询重写 | 检索前先把口语化问题改写成更适合向量检索的独立问题 | `doChatWithRagQueryWriter` |
| 内容安全 | 自定义 Advisor 实现违禁词拦截、用户权限（封禁名单）校验 | `ForbiddenWordsAdvisor` / `AuthorizedAdvisor` |

### 2. HuiManus 超级智能体

自主规划、循环执行"思考 → 行动 → 观察"直到任务完成的通用智能体，架构为三层继承：

```
BaseAgent（执行循环、状态机、SSE 流式输出）
   └── ReActAgent（定义 think() / act() 抽象方法）
          └── ToolCallAgent（实现思考=让大模型选工具，行动=执行工具）
                 └── HuiManus（最终智能体：设定提示词、注册全部工具）
```

内置 7 种工具（`com.hui.huiaiagent.tools`）：

| 工具 | 能力 |
| --- | --- |
| `FileOperationTool` | 读写本地文件（`tmp/file/` 目录） |
| `WebSearchTool` | 调用 SearchAPI（百度引擎）联网搜索，返回前 5 条结果 |
| `WebScrapingTool` | 用 Jsoup 抓取指定网页正文 |
| `ResourceDownloadTool` | 下载网络资源（如图片）到本地 `tmp/download/` |
| `TerminalOperationTool` | 执行终端命令（Windows: cmd / 其他: bash） |
| `PDFGenerationTool` | 用 iText 将文本文件转成 PDF（支持中文字体） |
| `TerminateTool` | 任务完成时终止智能体循环 |

### 3. 接口服务与前端

- 后端将两大应用封装为 HTTP + SSE 接口（`/api/ai/**`），并附 Knife4j 接口文档；
- Vue 3 前端提供主页、恋爱大师聊天室、Manus 聊天室三个页面，均为 SSE 流式渲染，支持 Markdown 渲染。

---

## 技术选型

### 后端

| 类别 | 技术 | 版本 | 用途 |
| --- | --- | --- | --- |
| 语言/运行时 | Java | 21 | 启用 `--enable-preview` |
| 框架 | Spring Boot | 3.4.4 | Web 应用骨架 |
| AI 框架 | Spring AI（含 spring-ai-alibaba-starter） | 1.0.0-M6.x | ChatModel / ChatClient / Advisor / RAG / ToolCall |
| AI 框架 | LangChain4j（community-dashscope） | 1.0.0-beta2 | 演示另一种大模型调用方式 |
| 大模型 SDK | dashscope-sdk-java | 2.19.1 | 阿里云灵积原生 SDK（版本须与 langchain4j 适配，见 FAQ） |
| 本地模型 | Spring AI Ollama | 1.0.0-M6 | 可选：本地 Ollama 模型调用 |
| 向量数据库 | SimpleVectorStore（内存）/ PgVector | 1.0.0-M6 | RAG 文档向量存储；PgVector 对接阿里云 RDS PostgreSQL |
| 对话记忆 | MySQL（mysql-connector-j 8.0.33） | - | `chat_memory` 表持久化多轮对话 |
| 序列化 | Kryo | 5.6.2 | 文件版对话记忆的序列化 |
| 网页抓取 | Jsoup | 1.19.1 | 网页正文提取工具 |
| PDF 生成 | iText（itext-core + font-asian） | 9.1.0 | PDF 生成工具，含中文字体支持 |
| 工具库 | Hutool / Lombok | 5.8.37 / 1.18.36 | HTTP、文件、JSON 工具 / 简化代码 |
| 接口文档 | Knife4j (OpenAPI3) | 4.4.0 | 在线接口文档 |

### 前端（`hui-ai-agent-frontend/`）

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Vue | 3.5 | 渐进式框架（Composition API） |
| Vite | 7 | 构建工具与开发服务器（端口 5173，代理 `/api` → 后端 8123） |
| Vue Router | 4 | 三个路由：`/`（主页）、`/love`、`/manus` |
| Axios | 1.7 | HTTP 请求 |

### 大模型服务

- **对话模型 / 向量模型**：阿里云灵积 DashScope（通义千问系列），向量维度 1536；
- **联网搜索**：[SearchAPI](https://www.searchapi.io/)（百度引擎）；
- **可选**：本地 Ollama（如 `granite4.2:3b`，配置见 `application.yml` 注释）。

---

## 项目结构

```
hui-ai-agent
├── src/main/java/com/hui/huiaiagent
│   ├── HuiAiAgentApplication.java   # 启动类（exclude 了 PgVector 自动配置）
│   ├── app/
│   │   └── LoveApp.java             # ★ AI 恋爱大师应用（ChatClient 各种用法）
│   ├── agent/                       # ★ Manus 智能体（ReAct 循环）
│   │   ├── BaseAgent.java           #   执行循环 + 状态机 + SSE 流式输出
│   │   ├── ReActAgent.java          #   思考/行动抽象
│   │   ├── ToolCallAgent.java       #   工具调用落地（核心难点）
│   │   ├── HuiManus.java            #   最终智能体
│   │   └── model/AgentState.java    #   IDLE/RUNNING/FINISHED/ERROR
│   ├── advisor/                     # 自定义 Advisor（日志/重读/权限/违禁词）
│   ├── chatmemory/                  # 对话记忆（文件版 / MySQL 版）
│   ├── rag/                         # RAG 全家桶（文档加载/向量化/切分/查询重写/检索配置）
│   ├── tools/                       # 7 个工具 + ToolRegistration 注册类
│   ├── demo/                        # 教学示例：4+1 种大模型调用方式、查询扩展器
│   ├── config/CorsConfig.java       # 全局跨域
│   ├── controller/                  # AiController（AI 接口）、Healthcontroller
│   └── constant/FileConstant.java   # 文件保存目录常量（项目根/tmp）
├── src/main/resources
│   ├── application.yml              # 主配置（端口 8123，context-path=/api）
│   ├── application-prod.yml         # 生产配置（密钥用环境变量注入）
│   ├── application-local.yml        # 本地密钥（已被 .gitignore 忽略，不提交）
│   └── document/                    # RAG 知识库文档（单身/恋爱/已婚 3 份 md）
├── sql/chat_memory.sql              # MySQL 对话记忆建表脚本
├── hui-ai-agent-frontend/           # Vue 3 前端（详见其内部 README.md）
├── Dockerfile                       # 后端多阶段构建（Maven 打包 → JDK21 运行）
└── STUDY.md                         # ★ 从 0 到 1 教学文档
```

---

## 业务逻辑

### 1. 恋爱大师的一次请求流程

```
用户消息 (message, chatId)
   │
   ▼
AiController (/api/ai/love_app/chat/...)
   │
   ▼
LoveApp.chatClient（构造时装配）
   ├─ defaultSystem：恋爱专家系统提示词
   └─ defaultAdvisors（责任链，按顺序拦截）：
        ① ForbiddenWordsAdvisor   违禁词 → 直接拒绝
        ② MessageChatMemoryAdvisor 按 chatId 取出最近 10 条历史消息注入上下文
        ③ MyLoggerAdvisor          打印请求/响应日志
   │
   ▼ （可选增强）
   ├─ QuestionAnswerAdvisor(loveAppVectorStore)：RAG 检索
   │     用户问题 → 向量化 → SimpleVectorStore 相似度检索 TopK
   │     → 命中文档拼进系统提示词 → 大模型结合知识库回答
   └─ .tools(allTools)：需要时让模型自主调用工具
   │
   ▼
DashScope 大模型 → 同步 String / 流式 Flux<String> / 结构化 LoveReport
```

**RAG 数据流（启动时）**：`LoveAppDocumentLoader` 读取 `classpath:document/*.md`（按文件名前缀打上 `status=单身/恋爱/已婚` 元数据）→（可选：TokenTextSplitter 切分、KeywordEnricher 补关键词）→ `dashscopeEmbeddingModel` 向量化 → 存入 `loveAppVectorStore`。

### 2. HuiManus 执行一次任务

```
用户任务 (message)
   │
   ▼
BaseAgent.runStream()：状态 IDLE→RUNNING，异步线程执行，最多 20 步
   │  每一步 step() = ReActAgent: think() + act()
   │
   ├─ think()：把「系统提示词 + 全部历史消息 + 下一步引导提示」发给大模型，
   │           携带 7 个工具描述；模型要么给出文本思考，要么返回工具调用请求
   │           （withProxyToolCalls=true：禁用框架内置的工具执行，由自己接管）
   │
   ├─ act()：ToolCallingManager.executeToolCalls() 真正执行工具，
   │          工具结果作为 ToolResponseMessage 追加进消息列表（观察结果）
   │          —— 若调用了 TerminateTool 则状态置为 FINISHED，循环结束
   │
   └─ 每步结果通过 SseEmitter 实时推送给前端
```

### 3. 前端与后端的交互

前端用 `fetch` + `ReadableStream` 手写解析 SSE 流（`src/utils/sse.js`），逐块接收 AI 输出并渲染 Markdown。开发环境下前端直连 `http://localhost:8123/api`（见 `src/api/ai.js`），属于跨域请求，因此后端的 `CorsConfig` 是必需的；`vite.config.js` 中另保留了 `/api` 代理作为备用方案（后端 context-path 为 `/api`，代理保留前缀、不做 rewrite）。

---

## 快速开始

### 环境要求

| 软件 | 版本要求 |
| --- | --- |
| JDK | 21+ |
| Maven | 3.9+（或直接用项目自带 `mvnw`） |
| Node.js | 18+（仅前端需要） |
| MySQL | 5.7+ / 8.x（对话记忆持久化；或改用 PostgreSQL） |

### 第 1 步：准备密钥

1. **DashScope API Key**（必须）：在[阿里云百炼控制台](https://bailian.console.aliyun.com/)开通并创建 API Key，用于对话与向量化。
2. **SearchAPI Key**（必须，工具注册时读取；暂时不用联网搜索可先填占位符）：在 [searchapi.io](https://www.searchapi.io/) 注册获取。

### 第 2 步：创建本地密钥配置

在 `src/main/resources/` 下新建 `application-local.yml`（该文件已被 `.gitignore` 忽略，永远不会被提交）：

```yaml
spring:
  ai:
    dashscope:
      api-key: 你的DashScope密钥
  # 对话记忆数据源（二选一，必须配一个：项目启动需要 DataSource）
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/hui_ai_agent?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver

# 联网搜索工具密钥
search-api:
  api-key: 你的SearchAPI密钥
```

> 💡 也可以用 PostgreSQL 数据源（项目作者本地对接的是阿里云 RDS PostgreSQL + pgvector），JDBC URL 形如 `jdbc:postgresql://主机/hui_ai_agent`。

### 第 3 步：初始化数据库

```bash
mysql -u root -p < sql/chat_memory.sql
```

该脚本创建 `hui_ai_agent` 库和 `chat_memory` 表（会话记忆，一行一条消息）。

### 第 4 步：启动后端

```bash
# 在项目根目录（Windows 用 mvnw.cmd，Mac/Linux 用 ./mvnw）
mvnw.cmd spring-boot:run
```

看到类似 `Tomcat started on port 8123` 即启动成功：

- 服务地址：<http://localhost:8123/api>
- 接口文档：<http://localhost:8123/api/doc.html>（Knife4j）或 `/api/swagger-ui.html`
- 健康检查：`GET /api/health`

> ⚠️ 启动时会调用向量化接口把 `resources/document/` 下的知识文档写入内存向量库，因此**必须配置有效的 DashScope Key 且能访问外网**。

### 第 5 步：启动前端

```bash
cd hui-ai-agent-frontend
npm install
npm run dev
```

打开 <http://localhost:5173>，即可体验：

- **AI 恋爱大师**：`/love` 页面
- **超级智能体 Manus**：`/manus` 页面（试着输入："帮我搜索杭州今天的天气，整理成文件并生成 PDF"）

### 第 6 步（可选）：验证接口

```bash
# 同步对话
curl "http://localhost:8123/api/ai/love_app/chat/sync?message=女朋友生气了怎么办&chatId=test-1"

# 流式对话（SSE）
curl -N "http://localhost:8123/api/ai/love_app/chat/sse?message=你好&chatId=test-1"

# Manus 智能体（SSE，会实时推送每个执行步骤）
curl -N "http://localhost:8123/api/ai/manus/chat?message=写一个北京一日游计划保存到文件"
```

---

## API 接口

所有接口挂在 context-path `/api` 下，控制器为 `AiController`（`/api/ai/**`）：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/ai/love_app/chat/sync` | 恋爱大师同步对话，参数 `message`、`chatId` |
| GET | `/api/ai/love_app/chat/sse` | 恋爱大师流式对话（`Flux<String>`，TEXT_EVENT_STREAM） |
| GET | `/api/ai/love_app/chat/sse/emitter` | 同上的 `SseEmitter` 实现（便于对比两种 SSE 写法） |
| GET | `/api/ai/manus/chat` | HuiManus 智能体流式执行，参数 `message`，逐步推送 Step 结果 |
| GET | `/api/health` | 健康检查 |

---

## 测试

后端按功能模块提供了完整的 JUnit 测试，可以直接运行来观察各能力的效果：

```bash
# 运行全部测试
mvnw.cmd test

# 运行单个测试类（示例）
mvnw.cmd test -Dtest=LoveAppTest          # 恋爱大师：对话/结构化输出/RAG/工具
mvnw.cmd test -Dtest=huiManusTest         # Manus 智能体任务执行
mvnw.cmd test -Dtest=QueryRewriterTest    # 查询重写
mvnw.cmd test -Dtest=FileOperationToolTest # 各工具类测试
```

测试类清单：`LoveAppTest`、`huiManusTest`、`QueryRewriterTest`、`MultiQueryExpanderDemoTest`、`PgVectorVectorStoreConfigTest` 以及 `tools/` 下 6 个工具测试。**运行测试同样需要 `application-local.yml` 中的密钥。**

---

## Docker 与 Serverless 部署

### 后端镜像（多阶段构建）

```bash
docker build -t hui-ai-agent .
docker run -d -p 8123:8123 \
  -e SPRING_AI_DASHSCOPE_API_KEY=xxx \
  -e SEARCH_API_API_KEY=xxx \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://宿机IP:3306/hui_ai_agent \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=xxx \
  hui-ai-agent
```

要点（见 `Dockerfile` 注释）：

- 第一阶段 Maven 打包，第二阶段仅保留 JDK21 + jar，镜像更小、Serverless 冷启动更快；
- `application-local.yml` 被 `.dockerignore` 挡在镜像外，**真实密钥全部通过环境变量注入**（优先级高于 jar 内所有 yml）；
- `-XX:MaxRAMPercentage=75.0` 让 JVM 堆按容器内存自适应；
- 默认激活 `prod` profile（`application-prod.yml` 只含非敏感配置）。

### 前端镜像

`hui-ai-agent-frontend/` 内含 `Dockerfile` 和 `nginx.conf`（构建静态文件后由 Nginx 托管并把 `/api` 反代到后端），用法见前端目录内 README。

---

## 常见问题 FAQ

**Q1：启动报 `Could not resolve placeholder 'search-api.api-key'`？**
A：没有创建 `application-local.yml` 或缺少 `search-api.api-key` 配置。该值在 `ToolRegistration` 中通过 `@Value` 注入，缺失会导致启动失败。

**Q2：启动报 `Failed to configure a DataSource`？**
A：项目中的 `MysqlChatMemory` 组件依赖 `JdbcTemplate`，因此必须在配置中提供一个数据源（MySQL 或 PostgreSQL 均可）。

**Q3：报 `NoSuchMethodError` 相关的 dashscope 错误？**
A：`dashscope-sdk-java` 版本必须与 `langchain4j-community-dashscope 1.0.0-beta2` 兼容，项目固定使用 `2.19.1`，升级到 2.2x 会报错（见 `pom.xml` 注释）。

**Q4：向 PgVector 写入文档报 `The input texts limit 25`？**
A：DashScope Embedding API 单次最多处理 25 条文本，需要分批入库（`PgVectorVectorStoreConfig` 中有分批示例代码，默认已注释）。

**Q5：PgVector 默认启用吗？**
A：默认关闭。`PgVectorVectorStoreConfig` 上的 `@Configuration` 被注释（无需 PostgreSQL 也能启动），恋爱大师默认使用内存向量库 `SimpleVectorStore`。需要 PgVector 时：打开注释 + 配置 PostgreSQL 数据源（启动类已 exclude 自动配置，Bean 由该配置类手动定义）。

**Q6：跨域是怎么处理的？**
A：`CorsConfig` 全局放行（`allowCredentials(true)` 必须配合 `allowedOriginPatterns`，不能直接用 `*`）。开发环境下前端是直连 `http://localhost:8123/api` 的跨域请求（见 `src/api/ai.js`），所以这份配置是必需的；生产环境前后端同域部署（Nginx 反代 `/api`）后则不再依赖它。

**Q7：对话记忆存在哪里？**
A：`LoveApp` 构造器中默认使用 `InMemoryChatMemory`（重启即失）。切换到文件持久化（`FileBasedChatMemory`，Kryo 序列化到 `tmp/chat-memory/`）或 MySQL 持久化（`MysqlChatMemory`，表 `chat_memory`）只需在构造器中改一行注释。

---

## 许可与声明

本项目为个人学习/作品集项目，仅供学习交流。`src/main/resources/document/` 下的知识文档为演示用示例内容。
