# Hui AI Agent 🤖

一个基于 **Spring Boot 3 + Spring AI + 阿里云灵积（DashScope）** 从 0 到 1 打造的 AI 智能体全栈项目，包含两个完整的 AI 应用：

1. **AI 恋爱大师（LoveApp）**——一个带系统人设、多轮记忆、知识库检索（RAG）的垂直领域对话应用；
2. **HuiManus 超级智能体**——不依赖任何 Agent 框架、手写 ReAct（思考-行动-观察）循环、可自主调用 7 种工具完成复杂任务的通用智能体。

项目还包含 **Vue 3 前端**（SSE 流式聊天界面）、**Docker / Serverless 部署配置**，以及大量可用于教学的示例代码（大模型调用、对话记忆、RAG、工具调用）。

> 📚 **想系统学习本项目？** 学习资料都在 [`study/`](./study/) 目录：
> - [`STUDY.md`](./study/STUDY.md)——按 git 提交历史编排的 0→1 教学手册：11 个阶段，每阶段 = 前置准备 → 依赖（含官方/Maven 地址）→ 配置 → 逐行注释的实战代码 → 动手实验 → 踩坑 → 照抄附录；每章末尾还有 **⏪ git checkout 命令**，可一键切回该阶段的原始代码对照学习。
> - [`RESUME.md`](./study/RESUME.md)——简历写法 + 14 条按公式写好的项目条目；
> - [`INTERVIEW.md`](./study/INTERVIEW.md)——面试官视角 30 题自检（逐条对应简历）。
>
> 🎬 配套 **12 支讲解视频**（每阶段一支，幻灯片 + 中文旁白）见 [`video/`](./video/README.md)。

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
| 多轮对话记忆 | **内存 / 文件（Kryo 序列化）/ MySQL** 三种实现，一行切换（默认 MySQL） | 构造器中切换 `ChatMemory` |
| RAG 知识库问答 | 加载 `resources/document/` 下 3 份 Markdown 知识文档，向量化后检索增强回答 | `doChatWithRag` |
| 查询重写 | 检索前先把口语化问题改写成更适合向量检索的独立问题（中文模板防"英译偏航"） | `doChatWithRagQueryWriter` |
| 内容安全 | 自定义 Advisor 责任链：权限（封禁名单）校验、违禁词拦截 | `AuthorizedAdvisor` / `ForbiddenWordsAdvisor` |

### 2. HuiManus 超级智能体

自主规划、循环执行"思考 → 行动 → 观察"直到任务完成的通用智能体。**没有引入任何 Agent 框架**，用四层继承手写了整个 ReAct 循环：

```
BaseAgent（执行循环 run()、状态机 AgentState、SSE 流式输出 runStream()）
   └── ReActAgent（把"一步"定义成 think() + act() 的模板）
          └── ToolCallAgent（真实现：思考=让大模型选工具，行动=执行工具并记账）
                 └── HuiManus（最终智能体：人设提示词、注册全部工具、20 步封顶）
```

两个关键机制：

- `withProxyToolCalls(true)`：关掉框架的自动工具执行，由 `act()` 自己调 `ToolCallingManager` 执行、自己维护消息列表——**工具结果每圈写回历史，下一圈思考才看得见**（这是"连续做事"的命脉）；
- **双保险防死循环**：`TerminateTool` 结束信号（模型主动喊停）+ `maxSteps` 步数硬上限。

内置 7 种工具（`com.hui.huiaiagent.tools`，注册于 `ToolRegistration`）：

| 工具 | 能力 |
| --- | --- |
| `FileOperationTool` | 读写本地文件（`tmp/file/` 目录） |
| `WebSearchTool` | 调用 SearchAPI（百度引擎）联网搜索，返回前 5 条结果 |
| `WebScrapingTool` | 用 Jsoup 抓取指定网页正文 |
| `ResourceDownloadTool` | 下载网络资源（如图片）到本地 `tmp/download/` |
| `TerminalOperationTool` | 执行终端命令（当前写死 `cmd.exe`，仅 Windows；演示用，生产慎开） |
| `PDFGenerationTool` | 用 iText 将文本转成 PDF（内置中文字体，防乱码） |
| `TerminateTool` | 任务完成时终止智能体循环 |

### 3. 接口服务与前端

- 后端将两大应用封装为 HTTP + SSE 接口（`/api/ai/**`），并附 Knife4j 在线接口文档（`/api/doc.html`，可在线调试）；
- Vue 3 前端提供主页、恋爱大师聊天室、Manus 聊天室三个页面，均为 SSE 流式渲染（fetch 手写解析，支持"停止生成"），支持 Markdown 渲染。

---

## 技术选型

### 后端

| 类别 | 技术 | 版本 | 用途 |
| --- | --- | --- | --- |
| 语言/运行时 | Java | 21 | 启用 `--enable-preview` |
| 框架 | Spring Boot | 3.4.4 | Web 应用骨架 |
| AI 框架 | Spring AI（spring-ai-alibaba-starter） | 1.0.0-M6.1 | ChatModel / ChatClient / Advisor / RAG / ToolCall |
| AI 框架 | LangChain4j（community-dashscope） | 1.0.0-beta2 | 演示另一种大模型调用方式（对比选型） |
| 大模型 SDK | dashscope-sdk-java | 2.19.1 | 灵积原生 SDK（版本须与 langchain4j 适配，见 FAQ） |
| 本地模型 | Spring AI Ollama | 1.0.0-M6 | 可选：本地 Ollama 模型调用 |
| 向量数据库 | SimpleVectorStore（内存）/ PgVector | 1.0.0-M6 | RAG 向量存储；PgVector 对接 PostgreSQL（默认关闭） |
| 对话记忆 | MySQL（mysql-connector-j） | 8.0.33 | `chat_memory` 表持久化多轮对话 |
| 序列化 | Kryo | 5.6.2 | 文件版对话记忆的对象序列化 |
| 网页抓取 | Jsoup | 1.19.1 | 网页正文提取 |
| PDF 生成 | iText（itext-core + font-asian） | 9.1.0 | PDF 生成，含中文字体支持 |
| 工具库 | Hutool / Lombok | 5.8.37 / 1.18.36 | HTTP、文件、JSON 工具 / 简化代码 |
| 接口文档 | Knife4j (OpenAPI3, jakarta) | 4.4.0 | 在线接口文档 + 在线调试 |

### 前端（`hui-ai-agent-frontend/`）

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Vue | 3.5 | 渐进式框架（Composition API） |
| Vite | 7 | 构建工具与开发服务器（端口 5173，`/api` 代理 → 后端 8123） |
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
│   │   └── LoveApp.java             # ★ AI 恋爱大师（ChatClient 全家桶用法）
│   ├── agent/                       # ★ 手写 Manus 智能体（ReAct 循环）
│   │   ├── BaseAgent.java           #   执行循环 + 状态机 + SSE 流式输出
│   │   ├── ReActAgent.java          #   step() = think() + act() 模板
│   │   ├── ToolCallAgent.java       #   工具调用落地（核心难点）
│   │   ├── HuiManus.java            #   最终智能体（纯配置层）
│   │   └── model/AgentState.java    #   IDLE/RUNNING/FINISHED/ERROR
│   ├── advisor/                     # 自定义 Advisor（日志/Re2 重读/权限/违禁词）
│   ├── chatmemory/                  # 对话记忆（Kryo 文件版 / MySQL 版）
│   ├── rag/                         # RAG 全家桶（文档加载/向量化/切分/查询重写/检索配置）
│   ├── tools/                       # 7 个工具 + ToolRegistration 注册类
│   ├── demo/                        # 教学示例：4+1 种大模型调用方式、查询扩展器
│   ├── config/CorsConfig.java       # 全局跨域
│   ├── controller/                  # AiController（AI 接口）、Healthcontroller
│   └── constant/FileConstant.java   # 文件保存目录常量（项目根/tmp）
├── src/main/resources
│   ├── application.yml              # 主配置（端口 8123，context-path=/api）
│   ├── application-prod.yml         # 生产配置（非敏感项；密钥走环境变量）
│   ├── application-local.yml        # 本地密钥（.gitignore 忽略，不提交）
│   └── document/                    # RAG 知识文档（单身/恋爱/已婚 3 份 md）
├── src/test/java                    # 测试（见下方「测试」一节清单）
├── sql/chat_memory.sql              # MySQL 对话记忆建表脚本
├── hui-ai-agent-frontend/           # Vue 3 前端（详见其内部 README.md）
├── study/                           # ★ 学习资料
│   ├── STUDY.md                     #   0→1 教学手册（11 阶段 + ⏪ 切代码命令）
│   ├── RESUME.md                    #   简历写法 + 14 条条目
│   └── INTERVIEW.md                 #   面试 30 题自检
├── video/                           # 12 支阶段讲解视频 + 生成脚本（scripts/）
├── Dockerfile                       # 后端多阶段构建（Maven 打包 → JDK21 运行）
└── README.md                        # 本文件
```

> 核心源码均带**逐行中文注释**（Advisor、chatmemory、rag、agent 包），配合 `study/STUDY.md` 的分阶段讲解食用更佳。

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
   └─ defaultAdvisors（责任链，按 getOrder 从小到大执行）：
        ① AuthorizedAdvisor      权限校验（黑名单用户直接拒绝，不调模型）
        ② ForbiddenWordsAdvisor  违禁词拦截（同样到不了模型，零 token 浪费）
        ③ MessageChatMemoryAdvisor 按 chatId 取出最近 10 条历史注入上下文
        ④ MyLoggerAdvisor        打印请求/响应日志
   │
   ▼ （可选增强）
   ├─ QuestionAnswerAdvisor(loveAppVectorStore)：RAG 检索
   │     用户问题 → 向量化 → SimpleVectorStore 相似度检索 TopK
   │     → 命中文档拼进提示词 → 大模型结合知识库回答
   │     （进阶版：LoveAppRagCustomAdvisorFactory——status 标签过滤 + 阈值 + topK + 空结果兜底）
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
   │           携带 7 个工具的说明书；模型返回工具调用指令单 → return true
   │           （withProxyToolCalls=true：禁用框架内置的工具执行，由自己接管）
   │
   ├─ act()：ToolCallingManager.executeToolCalls() 真正执行工具，
   │          返回的 conversationHistory 整包替换消息列表（观察结果入账）
   │          —— 若调用了 TerminateTool 则状态置为 FINISHED，循环结束
   │
   └─ 每步结果通过 SseEmitter 实时推送给前端
```

> 手写循环的逐步追踪、消息列表演化表，详见 `study/STUDY.md` 阶段 8。

### 3. 前端与后端的交互

前端用 `fetch` + `ReadableStream` 手写解析 SSE 流（`src/utils/sse.js`：按 `\n\n` 切事件、buffer 兜半包），逐块接收 AI 输出并渲染 Markdown；`AbortController` 实现"停止生成"。开发环境下前端直连 `http://localhost:8123/api`（见 `src/api/ai.js`），属于跨域请求，因此后端的 `CorsConfig` 是必需的；`vite.config.js` 中另保留了 `/api` 代理作为备用（后端 context-path 为 `/api`，代理保留前缀、不做 rewrite）。

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

1. **DashScope API Key**（必须）：在[阿里云百炼控制台](https://bailian.console.aliyun.com/)开通并创建，用于对话与向量化；
2. **SearchAPI Key**（工具注册时读取；暂时不用联网搜索可先填占位符）：在 [searchapi.io](https://www.searchapi.io/) 注册获取。

### 第 2 步：创建本地密钥配置

在 `src/main/resources/` 下新建 `application-local.yml`（已被 `.gitignore` 忽略，永远不会被提交）：

```yaml
spring:
  ai:
    dashscope:
      api-key: 你的DashScope密钥
  # 对话记忆数据源（MySQL 或 PostgreSQL，必须配一个：启动需要 DataSource）
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/hui_ai_agent?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver

# 联网搜索工具密钥
search-api:
  api-key: 你的SearchAPI密钥
```

### 第 3 步：初始化数据库

```bash
mysql -u root -p < sql/chat_memory.sql
```

创建 `hui_ai_agent` 库和 `chat_memory` 表（一行一条消息，字段含会话 id / 角色 / 原文，见脚本注释）。

### 第 4 步：启动后端

```bash
# 项目根目录（Windows 用 mvnw.cmd，Mac/Linux 用 ./mvnw）
mvnw.cmd spring-boot:run
```

看到 `Tomcat started on port 8123` 即成功：

- 服务地址：<http://localhost:8123/api>
- 接口文档：<http://localhost:8123/api/doc.html>（Knife4j，可在线调试）
- 健康检查：`GET /api/health`

> ⚠️ 启动时会调用向量化接口把 `resources/document/` 下的知识文档写入内存向量库，因此**必须配置有效的 DashScope Key 且能访问外网**。

### 第 5 步：启动前端

```bash
cd hui-ai-agent-frontend
npm install
npm run dev
```

打开 <http://localhost:5173>：

- **AI 恋爱大师**：`/love` 页面；
- **超级智能体 Manus**：`/manus` 页面（试试："帮我搜索杭州今天的天气，整理成文件并生成 PDF"）。

### 第 6 步（可选）：验证接口

```bash
# 同步对话
curl "http://localhost:8123/api/ai/love_app/chat/sync?message=女朋友生气了怎么办&chatId=test-1"

# 流式对话（SSE）
curl -N "http://localhost:8123/api/ai/love_app/chat/sse?message=你好&chatId=test-1"

# Manus 智能体（SSE，实时推送每个执行步骤）
curl -N "http://localhost:8123/api/ai/manus/chat?message=写一个北京一日游计划保存到文件"
```

---

## API 接口

所有接口挂在 context-path `/api` 下，控制器为 `AiController`（`/api/ai/**`）：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/ai/love_app/chat/sync` | 恋爱大师同步对话，参数 `message`、`chatId` |
| GET | `/api/ai/love_app/chat/sse` | 恋爱大师流式对话（`Flux<String>`，TEXT_EVENT_STREAM） |
| GET | `/api/ai/love_app/chat/sse/emitter` | 同上的 `SseEmitter` 实现（对比两种 SSE 写法） |
| GET | `/api/ai/manus/chat` | HuiManus 智能体流式执行，参数 `message`，逐步推送 Step 结果 |
| GET | `/api/health` | 健康检查 |

---

## 测试

后端按功能模块提供完整的 JUnit 测试，其中两个是**零成本单元测试**（不起 Spring、不调大模型）：

```bash
mvnw.cmd test                                        # 运行全部测试
mvnw.cmd test -Dtest=LoveAppTest                     # 恋爱大师：记忆/结构化输出/RAG/工具
mvnw.cmd test -Dtest=huiManusTest                    # Manus 智能体任务执行（真实跑完任务）
mvnw.cmd test -Dtest=AdvisorTest                     # ⚡ 拦截器单测：不花一分钱（假链子验证拦/放）
mvnw.cmd test -Dtest=ChatMemoryTest                  # ⚡ 三个记忆本单测：不经过大模型
mvnw.cmd test -Dtest=QueryRewriterTest               # 查询重写
mvnw.cmd test -Dtest=FileOperationToolTest           # 工具类测试（tools/ 下共 6 个）
```

测试类清单：`LoveAppTest`、`huiManusTest`、`AdvisorTest`、`ChatMemoryTest`、`QueryRewriterTest`、`MultiQueryExpanderDemoTest`、`PgVectorVectorStoreConfigTest`、`tools/` 下 6 个工具测试。**除两个 ⚡ 零成本单测外，运行测试同样需要 `application-local.yml` 中的密钥**（会真实调用大模型）。

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

- 第一阶段 Maven 打包，第二阶段仅保留 JDK21 + jar——Maven、源码不带进成品，镜像从 1GB+ 瘦到几百 MB，Serverless 冷启动更快；
- `application-local.yml` 被 `.dockerignore` 挡在镜像外，**真实密钥全部通过环境变量注入**（优先级高于 jar 内所有 yml，同一个 jar 插不同钥匙随处能跑）；
- `-XX:MaxRAMPercentage=75.0` 让 JVM 堆按容器内存自适应；
- 默认激活 `prod` profile（`application-prod.yml` 只含非敏感配置）。

### 前端镜像

`hui-ai-agent-frontend/` 内含 `Dockerfile` 和 `nginx.conf`（构建静态文件后由 Nginx 托管并把 `/api` 反代到后端；注意 `proxy_buffering off`——不关它，流式会憋成整屏）。用法见前端目录内 README。

---

## 常见问题 FAQ

**Q1：启动报 `Could not resolve placeholder 'search-api.api-key'`？**
A：没有创建 `application-local.yml` 或缺少 `search-api.api-key` 配置。该值在 `ToolRegistration` 中通过 `@Value` 注入，缺失会导致启动失败（暂时不用搜索也请填占位符）。

**Q2：启动报 `Failed to configure a DataSource`？**
A：`MysqlChatMemory` 组件依赖 `JdbcTemplate`，必须在配置中提供一个数据源（MySQL 或 PostgreSQL 均可）。

**Q3：报 `NoSuchMethodError` 相关的 dashscope 错误？**
A：`dashscope-sdk-java` 必须与 `langchain4j-community-dashscope 1.0.0-beta2` 兼容，项目固定 **2.19.1**，升到 2.2x 会报错（见 `pom.xml` 注释）。

**Q4：向 PgVector 写入文档报 `The input texts limit 25`？**
A：DashScope Embedding API 单次最多处理 25 条文本，需要分批入库（`PgVectorVectorStoreConfig` 中有 batchSize=10 的分批示例，默认注释）。

**Q5：PgVector 默认启用吗？**
A：默认关闭。`PgVectorVectorStoreConfig` 上的 `@Configuration` 被注释（无需 PostgreSQL 也能启动），恋爱大师默认使用内存向量库 `SimpleVectorStore`。需要时：打开注释 + 配置 PostgreSQL 数据源（启动类已 exclude 自动配置，Bean 由该配置类手动定义）。

**Q6：跨域是怎么处理的？**
A：`CorsConfig` 全局放行（`allowCredentials(true)` 必须配合 `allowedOriginPatterns`，不能直接用 `*`）。开发环境前端直连 `http://localhost:8123/api` 属跨域，所以这份配置必需；生产前后端同域部署（Nginx 反代 `/api`）后不再依赖它。

**Q7：对话记忆存在哪里？怎么切换？**
A：`LoveApp` 构造器里一行切换三种实现，**当前默认 `MysqlChatMemory`**（表 `chat_memory`，跨重启）；另有 `InMemoryChatMemory`（内存，重启即失）和 `FileBasedChatMemory`（Kryo 序列化到 `tmp/chat-memory/*.kryo`，同样跨重启）。三者实现同一个 `ChatMemory` 接口（策略模式），切换只需注释/解注释对应行。

**Q8：想把整个项目学一遍，从哪开始？**
A：从 [`study/STUDY.md`](./study/STUDY.md) 阶段 0 开始——按 git 提交历史拆成 11 个阶段，每章末尾有 ⏪ `git checkout` 命令可切回该阶段原始代码对照；学完用 [`RESUME.md`](./study/RESUME.md) 写简历、[`INTERVIEW.md`](./study/INTERVIEW.md) 30 题自检。

---

## 许可与声明

本项目为个人学习/作品集项目，仅供学习交流。`src/main/resources/document/` 下的知识文档为演示用示例内容。
