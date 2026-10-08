# STUDY.md —— Hui AI Agent 从 0 到 1 学习指南

> **这份文档写给谁？** 写给每一位想学会「Spring AI 应用开发」的小白：只要你 会一点 Java 基础语法、知道 Spring Boot 是什么（不知道也行，第 0 章会补），就能跟着本文档把本项目吃透。
>
> **怎么用这份文档？** 本项目的 git 提交历史本身就是一条完整的学习路径——先会调大模型，再加记忆、加知识库、加工具，最后手写一个 Manus 智能体并上线。本文档按提交顺序把项目拆成 **11 个阶段**，每个阶段告诉你：*要学什么 → 读哪些代码 → 代码里发生了什么 → 动手做什么 → 有哪些坑*。
>
> 建议配合 git 命令对照源码：
> ```bash
> git log --oneline --reverse        # 按时间顺序查看所有提交
> git show <commit号>                # 查看某次提交改了什么（最好的教材！）
> git checkout <commit号>            # 把代码切换到那个阶段，跑起来看效果
> ```

---

## 学习路线总览

| 阶段 | 主题 | 对应提交 | 你将学会 |
| --- | --- | --- | --- |
| 0 | 前置知识 | - | Spring AI 是什么、四个核心概念 |
| 1 | 项目骨架 | `83d1208` | Spring Boot 工程结构、配置文件分层 |
| 2 | 调用大模型的 5 种方式 | `47e38f6` `1dbdd0b` | SDK / HTTP / Spring AI / LangChain4j / Ollama |
| 3 | ChatClient 与 Advisor | `5259d50` | 链式调用、系统提示词、Advisor 责任链 |
| 4 | 自定义 Advisor | `513c3e4` `bcab20f` | 日志、Re-Read 增强、权限校验、违禁词 |
| 5 | 对话记忆 | `1913ab5` `b92588a` | ChatMemory 接口 + 文件/MySQL 三种实现 |
| 6 | RAG 检索增强 | `3c8d159`→`77a34fc` | 文档加载、向量化、切分、元数据、查询重写、检索配置 |
| 7 | 工具调用（Tool Call） | `abe317c`→`8fcf40d` | @Tool 注解、7 个工具、工具注册 |
| 8 | 手写 Manus 智能体 | `bd434b1` | BaseAgent→ReActAgent→ToolCallAgent→HuiManus |
| 9 | 接口服务化 + 流式输出 | `1419c4d` `c377b50` | Flux、SSE 两种写法、跨域 |
| 10 | Vue 3 前端 | `55c89f0` | EventSource 消费 SSE、聊天室组件 |
| 11 | 部署上线 | `7fa8fdf` `8a317ff` | Docker 多阶段构建、Serverless、环境变量注入密钥 |

---

## 阶段 0：前置知识（15 分钟）

### 0.1 这个项目在做什么？

一句话：**用 Java 接入大模型（通义千问），做出两个 AI 应用**——

1. **AI 恋爱大师**：垂直领域聊天机器人（有人设、有记忆、有专属知识库）；
2. **HuiManus 超级智能体**：能自己「思考 → 调工具 → 观察 → 再思考」循环干活的 Agent，类似开源项目 OpenManus 的 Java 版教学实现。

### 0.2 Spring AI 的 4 个核心概念（贯穿全项目，必须先懂）

| 概念 | 一句话理解 | 项目中的例子 |
| --- | --- | --- |
| **ChatModel** | 最底层的「大模型客户端」，一问一答 | `dashscopeChatModel`（灵积模型） |
| **ChatClient** | ChatModel 的「链式封装」，可以挂提示词、记忆、拦截器 | `LoveApp` 构造器里 build 出来的那个 |
| **Advisor**（顾问/拦截器） | 挂在请求链上的「过滤器」，能在发给大模型前后做手脚（加记忆、记日志、拦截违禁词） | `MessageChatMemoryAdvisor`、`MyLoggerAdvisor` |
| **Tool / ToolCallback** | 告诉大模型「你还可以调用这些方法」，模型自己决定何时调用 | `WebSearchTool` 等 7 个工具 |

再看两个进阶概念（阶段 6、8 用到）：

- **RAG**（Retrieval-Augmented Generation，检索增强生成）：先从知识库里**检索**相关资料，塞进提示词再让模型回答，解决「模型不知道你私域知识」的问题。
- **ReAct**（Reason + Act）：智能体的运行模式——思考一步、行动一步、看结果、再思考，循环直到任务完成。

### 0.3 环境准备

- JDK 21、Maven 3.9（或用项目自带 `mvnw`）、Node.js 18+、MySQL；
- 一个阿里云灵积（DashScope）API Key（[百炼控制台](https://bailian.console.aliyun.com/)免费申请）；
- 按 [README.md「快速开始」](./README.md#快速开始) 完成 `application-local.yml` 配置和建表，保证项目能跑起来再开始学习。

---

## 阶段 1：项目骨架（提交 `83d1208`）

> Initial commit: Spring Boot project skeleton

**学什么**：一个标准的 Spring Boot 工程长什么样。

**读什么**：

- `pom.xml`——父工程 `spring-boot-starter-parent 3.4.4`，Java 21；
- `src/main/java/com/hui/huiaiagent/HuiAiAgentApplication.java`——启动类，注意后面阶段加上的 `exclude = PgVectorStoreAutoConfiguration.class`（先知道有这回事，阶段 6 解释）；
- `src/main/resources/application.yml`——**端口 8123、context-path `/api`**（这两个值后面前端、代理全都要对上）。

**动手**：`mvnw.cmd spring-boot:run` 启动，访问 `http://localhost:8123/api/health` 能通，阶段 1 就毕业了。

---

## 阶段 2：调用大模型的 5 种方式（提交 `47e38f6`、`1dbdd0b`）

> 自动调用大模型的方式，包含SDK，HTTP，spring AI，langchain4j / 增加ollama的调用方式

**为什么这么学**：这是全项目的地基。作者刻意用 5 个 Demo 类演示同一件事（让大模型说句话）的 5 种实现，让你直观对比「原始 → 高级」的封装演化。

**读什么**（都在 `demo/invoke/` 包下，全是教学代码，取消 `@Component` 注释即可在启动时执行）：

| 文件 | 方式 | 关键点 |
| --- | --- | --- |
| `HttpAiInvoke.java` | 裸 HTTP 调 REST 接口 | 最原始：自己拼 URL、header、JSON，自己解析响应。没有封装，但能看清「调用大模型本质就是发 HTTP 请求」 |
| `SdkAiInvoke.java` | 灵积官方 SDK（`dashscope-sdk-java`） | SDK 帮你封装了 HTTP，代码量骤减 |
| `SpringAiAiInvoke.java` | **Spring AI**（本项目主力） | 注入 `ChatModel`，一行 `chatModel.call(new Prompt("..."))` 完事 |
| `LangChainAiInvoke.java` | LangChain4j | 另一个主流 Java AI 框架，作对比了解 |
| `OllamaAiInvoke.java` | 本地 Ollama 模型 | 换个 `ChatModel` 实现（本地免费模型），**体会：框架统一了不同模型的调用方式** |
| `TestApiKey.java` | 密钥连通性测试 | 拿到 Key 后先跑它验证 |

**必须理解的点**：

1. `application.yml` 里的 `spring.ai.dashscope.api-key` + 依赖 `spring-ai-alibaba-starter`，让 Spring 自动装配出了 `dashscopeChatModel` 这个 Bean，后面整个项目注入的都是它；
2. 5 种方式没有优劣之分，**Spring AI 的价值在于和 Spring 生态（依赖注入、拦截器、Web）无缝衔接**——这正是后面所有阶段的选择。

**动手**：把 `HttpAiInvoke` 和 `SpringAiAiInvoke` 的 `@Component` 分别取消注释，启动项目对比控制台输出。

**坑**：`dashscope-sdk-java` 必须用 `2.19.1`（`pom.xml` 里锁死了）。升到 2.2x 会和 `langchain4j-community-dashscope 1.0.0-beta2` 冲突报 `NoSuchMethodError`。

---

## 阶段 3：ChatClient 与 Advisor——恋爱大师诞生（提交 `5259d50`）

> 增加使用 chatclient 和 advisors 的方式来调用灵积模型

**学什么**：`ChatModel` 是「裸模型」，而 `ChatClient` 才是开发应用的正确姿势。

**读什么**：`app/LoveApp.java`（本项目的第一个正式应用，后面阶段都在它身上叠加功能）

```java
chatClient = ChatClient.builder(dashscopeChatModel)
        .defaultSystem(SYSTEM_PROMPT)          // 系统提示词：扮演恋爱专家
        .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory)  // 记忆拦截器
        )
        .build();
```

**必须理解的点**：

1. **系统提示词**（SYSTEM_PROMPT）决定了 AI 的「人设」。读一下 `LoveApp` 里那段提示词，学习它是怎么写的：身份 + 开场白 + 按状态（单身/恋爱/已婚）分维度提问 + 引导用户详述；
2. **Advisor 是责任链**：请求发出前/后都会经过它们，顺序敏感。默认装的记忆 Advisor 会在每次请求时把历史消息塞进上下文；
3. `doChatWithReport` 演示了**结构化输出**：`.call().entity(LoveReport.class)` 让 AI 返回 JSON 并自动转成 Java record——这是 AI 应用对接业务系统的关键能力。

**动手**：跑测试类 `app/LoveAppTest.java` 的 `testChat()` 和 `doChatWithReport()`，观察控制台日志里 Advisor 注入的内容。

---

## 阶段 4：自定义 Advisor（提交 `513c3e4`、`bcab20f`）

> 1.增加自定义日志拦截器 2.增加re2 拦截器 / 自定义Advisor：权限校验、违禁词校验 advisor

**学什么**：Spring AI 的 Advisor 是可以自己写的——这是理解「AI 应用工程化」的第一课（鉴权、风控、日志都要靠它）。

**读什么**（`advisor/` 包，4 个类难度递进）：

| 类 | 干什么 | 学习点 |
| --- | --- | --- |
| `MyLoggerAdvisor` | 打印请求/响应日志 | 实现 `CallAdvisor` 接口最简单的例子：`adviseRequest` 里看请求、`next.call()` 放行、返回值里看响应 |
| `ReReadingAdvisor` | 「重读」增强：在用户问题后面拼一句 `让我们逐步思考...`（Re-Read/CoT 技巧） | 学会**修改发给模型的内容**：`before()` 阶段改写用户消息 |
| `AuthorizedAdvisor` | 权限校验：从 Advisor 参数里取 `user_id`，命中封禁名单直接拒绝 | 学会**短路返回**：不调 `next.call()`，直接返回拒绝话术 |
| `ForbiddenWordsAdvisor` | 违禁词检测：命中词直接拦截 | 生产必备的内容安全 demo |

**必须理解的点**：这 4 个类共同展示了 Advisor 的三种玩法——**观察（日志）、增强（改写请求）、拦截（拒绝请求）**。真实项目的 AI 网关基本就是这三件事。

**动手**：在 `LoveApp` 构造器里把 `AuthorizedAdvisor` 的注释打开，传一个封禁名单 `new AuthorizedAdvisor(Set.of("badGuy"))`，再用 `.advisors(spec -> spec.param(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "badGuy"))` 模拟被封禁用户发消息，观察被拦截的效果。

---

## 阶段 5：对话记忆——三种实现（提交 `1913ab5`、`b92588a`）

> 增加记忆文件持久化代码 / 加入数据库持久化对话到MySQL中

**学什么**：大模型本身是「金鱼记忆」，多轮对话全靠应用层把历史存下来再喂回去。Spring AI 把这件事抽象成了 **`ChatMemory` 接口**，本阶段实现了它的 3 个实现类。

**读什么**（`chatmemory/` 包 + `sql/chat_memory.sql`）：

```
ChatMemory 接口（Spring AI 提供）
 ├── InMemoryChatMemory      （框架自带，存内存，重启就没）
 ├── FileBasedChatMemory     （手写：一个会话一个文件，Kryo 序列化）
 └── MysqlChatMemory         （手写：一条消息一行记录，JdbcTemplate 直插 SQL）
```

**必须理解的点**：

1. 看 `MysqlChatMemory` 的建表 `sql/chat_memory.sql`：`conversation_id`（对应业务里的 `chatId`）+ `role`（USER/ASSISTANT）+ `content`，**主键自增 id 天然记录消息顺序**——这就是多轮对话数据模型的全部；
2. 记忆是怎么被用起来的？回到 `LoveApp`：`MessageChatMemoryAdvisor(chatMemory)` 在**每次请求前按 chatId 查历史、请求后把新消息写回去**，你写实现类就行，编织过程框架包办；
3. **策略模式 + 一行切换**：`LoveApp` 构造器里三选一，注释切换：
   ```java
   ChatMemory chatMemory = new InMemoryChatMemory();     // 当前启用：内存
   // ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
   // ChatMemory chatMemory = mysqlChatMemory;
   ```
4. `FileBasedChatMemory` 顺带学了 **Kryo**（高性能 Java 序列化库）的用法：注册策略、Input/Output 流。

**动手**：切换到 `MysqlChatMemory`，用同一个 `chatId` 连续问两句话（第二句依赖第一句的上下文），然后 `SELECT * FROM chat_memory` 看落库的数据。

---

## 阶段 6：RAG 检索增强——8 个提交的知识库进化史（提交 `3c8d159` → `77a34fc`）

> 添加 RAG + 本地知识库 → … → 添加"上下文查询增强器"代码

这是项目里提交最多的阶段，RAG 的每个环节都被拆成了独立提交，**强烈建议逐个 `git show` 对比学习**。

### 6.1 RAG 三步曲（先背下来）

```
① 离线写入：文档 → 切分 → Embedding 向量化 → 存入向量库
② 在线检索：用户问题 → 向量化 → 相似度搜索 → 取回 TopK 相关片段
③ 增强回答：把片段拼进提示词 → 大模型结合资料作答
```

### 6.2 按提交顺序读代码

| 提交 | 文件 | 学什么 |
| --- | --- | --- |
| `3c8d159` 本地知识库 | `rag/LoveAppDocumentLoader.java`、`rag/LoveAppVectorStoreConfig.java`、`resources/document/*.md` | **① 的完整落地**：用 `MarkdownDocumentReader` 读 3 份知识文档（单身/恋爱/已婚各一份），向量化存进 `SimpleVectorStore`（内存向量库）。再用 `QuestionAnswerAdvisor(vectorStore)` 一行开启检索增强 |
| `4fba4e9` 云端知识库 | `rag/LoveAppRagCloudAdvisorConfig.java` | 另一条路：不自己管文档，直接接阿里云百炼的知识库服务（`DocumentRetriever` 换成云端实现） |
| `e8f05e5` PgVector | `rag/PgVectorVectorStoreConfig.java` | 把向量库从内存换成 **PostgreSQL + pgvector**（生产可用）。注意启动类 `exclude = PgVectorStoreAutoConfiguration.class`——关掉自动配置改为手动定义 Bean。**坑**：DashScope Embedding 接口单次最多 25 条文本，入库必须分批（代码里有分批写法） |
| `79c72e1` 测试 | `PgVectorVectorStoreConfigTest` | 学会对向量库做单元测试（similaritySearch 验证检索效果） |
| `83b1e41` 切分器 | `rag/MyTokenTextSplitter.java` | 自定义 Token 切分策略，控制每段文档的长度（太长检索不准、太短语义破碎） |
| `7b46e09` 元数据 | `LoveAppDocumentLoader`（改） | 给每份文档打 `status=单身/恋爱/已婚` **元数据**，为后面按状态过滤检索做铺垫 |
| `7a1c1f8` 关键词 | `rag/MyKeywordEnricher.java` | 用 AI 给文档自动生成关键词元数据，提升召回率（ETL 思想：写入时多干活，检索时少费劲） |
| `733add7` 查询扩展 | `demo/rag/MultiQueryExpanderDemo.java` | **查询扩展**：把一个问题改写成多个不同角度的问题分别检索，合并结果（MultiQuery） |
| `9d3ebbe` 查询重写 | `rag/QueryRewriter.java` | **查询重写**：把口语化、带闲聊的问题改写成独立完整的检索问题。**坑**：Spring AI 默认重写提示词是英文的（会把中文问题翻译成英文），这里换成中文模板；模板必须保留 `{query}`、`{target}` 占位符，否则 builder 校验直接报错 |
| `3c485a8` 检索配置 | `rag/LoveAppRagCustomAdvisorFactory.java` | 高级检索配置：`filterExpression`（只搜 `status=已婚` 的文档）+ `similarityThreshold`（相似度阈值 0.5）+ `topK(3)` |
| `77a34fc` 上下文增强 | `rag/LoveAppContextualQueryAugmenterFactory.java` | 兜底体验：**检索结果为空时**不瞎答，用自定义模板回「我只能回答恋爱相关的问题哦」 |

### 6.3 一图总结本阶段的类

```
                    ┌─ 写入侧（离线） ─────────────────────────┐
 LoveAppDocumentLoader ─→ MyTokenTextSplitter ─→ MyKeywordEnricher
 （读md+打元数据）        （切分）               （AI补关键词）
          │
          ▼
 LoveAppVectorStoreConfig（SimpleVectorStore 内存版，默认启用）
 PgVectorVectorStoreConfig（PgVector 生产版，默认注释关闭）
          │
                    └─ 检索侧（在线） ─────────────────────────┘
 QueryRewriter（重写）→ LoveAppRagCustomAdvisorFactory（过滤+阈值+TopK）
          │              → LoveAppContextualQueryAugmenterFactory（空结果兜底）
          ▼
 LoveApp.doChatWithRag*() 方法挂上 QuestionAnswerAdvisor 即生效
```

**动手**：跑 `LoveAppTest.doChatWithRag()`，问一个只有知识库里才有答案的问题；再对比 `doChatWithRagQueryWriter()`，观察日志里重写前后的问题差别。

---

## 阶段 7：工具调用——给 AI 装上手脚（提交 `abe317c` → `8fcf40d`）

> 添加"文件调用"工具 / 联网搜索 / 网页抓取 / 终端命令 / 资源下载 / PDF生成器 / 工具注册类

**学什么**：Tool Call（Function Calling）。前面 AI 只会「说」，这一阶段让 AI 会「做」——你只要把工具方法用 `@Tool` 注解标出来，框架会生成工具描述告诉大模型，**大模型自己决定何时调用、传什么参数**。

**读什么**（`tools/` 包）：

| 类 | 一句话 | 值得学的点 |
| --- | --- | --- |
| `FileOperationTool` | 读写本地文件 | 最简单的 @Tool 例子：`@Tool(description=...)` + `@ToolParam`；**description 写得越清楚，模型调用越准** |
| `WebSearchTool` | 调 SearchAPI 搜百度 | 构造器传入 API Key（工具类不走 Spring 注解注入，由注册类手动 new） |
| `WebScrapingTool` | Jsoup 抓网页正文 | 学 Jsoup 三行代码取网页文本 |
| `TerminalOperationTool` | 执行终端命令 | 判断操作系统选 cmd/bash（`System.getProperty("os.name")`） |
| `ResourceDownloadTool` | 下载网络资源 | Hutool `HttpUtil.downloadFile` |
| `PDFGenerationTool` | 文本转 PDF | iText + **中文字体**（`font-asian` 依赖，不加中文全是方块） |
| `TerminateTool` | 终止智能体 | 阶段 8 的伏笔：Agent 循环的「刹车」 |
| `ToolRegistration` | 注册中心 | `@Bean ToolCallback[] allTools()`：`ToolCallbacks.from(...)` 把 7 个工具统一转成 `ToolCallback` 数组注入容器，注释里写了工厂/注册/适配器模式的说明 |

**必须理解的点**：

1. 工具调用的完整链路：用户问题 → 模型返回「我要调 `searchWeb`，参数是 xxx」→ 框架反射执行方法 → 结果回传模型 → 模型组织成自然语言答复。**AI 全程只出「调用意图」，执行永远在你的 Java 进程里**；
2. 使用侧只要一行：`LoveApp.doChatWithTools()` 里的 `.tools(allTools)`。

**动手**：跑 `LoveAppTest.doChatWithTools()`，输入「帮我搜一下今天杭州的天气」，观察日志里模型发起的工具调用和参数。

---

## 阶段 8：手写 Manus 智能体（提交 `bd434b1`）⭐ 全项目最核心

> 添加 manus 智能体

**学什么**：把前面所有能力（提示词 + 记忆 + 工具）组装成一个**自主循环**的智能体。这是本项目的高光部分——**不依赖任何 Agent 框架，用 4 个类手写出一个迷你 Manus**。

**读什么**（`agent/` 包，**必须按继承链自上而下读**）：

### 第 1 层：`BaseAgent.java`——执行循环 + 状态机

- `AgentState` 枚举（`IDLE/RUNNING/FINISHED/ERROR`）管理生命周期；
- `run()` 方法是心脏：`for (i < maxSteps && state != FINISHED) { step(); }` —— **「步数上限 + 完成标记」双保险防止 AI 无限循环烧钱**；
- `runStream()`：用 `SseEmitter` + `CompletableFuture.runAsync` 把每一步结果实时推送前端（异步线程不阻塞 Web 线程）；
- `step()` 是抽象方法——「一步」干什么，交给子类定义。

### 第 2 层：`ReActAgent.java`——定义「思考 + 行动」

```java
public String step() {
    boolean shouldAct = think();      // 思考：让大模型决定下一步
    if (!shouldAct) return "思考完成";
    return act();                     // 行动：执行思考产生的动作
}
```

这就是 **ReAct 模式**：每一「步」= 一次 think + 一次 act。

### 第 3 层：`ToolCallAgent.java`——最难的一个类，慢慢啃

- **think()**：把「系统提示词 + 完整消息历史 + nextStepPrompt（下一步引导词）」发给模型，并 `.tools(availableTools)` 附上全部工具。模型要么给文本（不调工具），要么给 `ToolCall` 列表（要调工具）；
- **act()**：用 `ToolCallingManager.executeToolCalls()` 真正执行工具，**把工具结果追加进消息列表**（这就是「观察 Observation」）；
- 两个关键设计：
  1. `withProxyToolCalls(true)`——**关掉 Spring AI 内置的工具自动执行**，改为手动接管。为什么？因为 Agent 要自己维护跨多轮的消息上下文；
  2. 执行结果里检测是否调用了 `doTerminate`（TerminateTool）——调了就把状态置 `FINISHED`，循环正常结束。

### 第 4 层：`HuiManus.java`——最终智能体

只是组装：设定名字、系统提示词（「你是全能助手 YuManus...」）、下一步引导提示词（「主动选择工具、复杂任务分步解决、完成后调用 terminate」）、`maxSteps=20`、绑定 ChatClient 和全部工具。

**用一张图记住它**：

```
runStream("帮我做一份上海约会攻略并生成PDF")
   │
   ▼
┌─ think ──────────────────────────────────┐
│ 消息历史+引导词 → 千问模型 → "我要调      │
│ WebSearchTool，参数：上海 约会 攻略"      │
└──────────────┬───────────────────────────┘
               ▼
┌─ act ────────────────────────────────────┐
│ 执行搜索 → 结果追加进消息历史（观察）     │
│ → 未调用 terminate，回到 think            │
└──────────────┬───────────────────────────┘
               ▼ （若干轮后）
┌─ think ──────────────────────────────────┐
│ "资料齐了，调 FileOperationTool 写文件，  │
│  再调 PDFGenerationTool，最后 terminate"  │
└──────────────┬───────────────────────────┘
               ▼
   doTerminate → 状态 FINISHED → 循环结束
   （每个 Step 都通过 SSE 实时推给了前端）
```

**动手**：跑 `agent/huiManusTest.java`；然后给 `tools/` 包加一个你自己的工具（比如「获取当前时间」），在 `ToolRegistration` 注册，看 Manus 会不会自己用它。

---

## 阶段 9：接口服务化 + 流式输出（提交 `1419c4d`、`c377b50`）

> 添加流式的聊天方法以及AI_Chat 接口服务化 / 添加 manus 接口服务以及补充跨域问题

**学什么**：AI 能力变成 HTTP 接口；以及 AI 应用的标配——**流式输出**（用户等不了 30 秒再看一大段字，要一个字一个字往外蹦）。

**读什么**：`controller/AiController.java`、`config/CorsConfig.java`

**必须理解的点**：

1. **两种流式技术方案**（控制器里都有，专门供你对比）：
   - **WebFlux `Flux<String>`**：响应式流，`produces = MediaType.TEXT_EVENT_STREAM_VALUE`，Spring 原生 SSE；
   - **Servlet `SseEmitter`**：传统 MVC 下的 SSE 手动档——`love_app/chat/sse/emitter` 用 `Flux.subscribe(...)` 把响应式流转投给 Emitter；`/ai/manus/chat` 则是 `BaseAgent.runStream()` 直接返回 Emitter。**体会：两种方案殊途同归，按项目技术栈选**；
2. `/ai/manus/chat` 里每次请求 `new HuiManus(...)`：因为 Agent 是**有状态**的（消息列表、状态机），单例会串会话——这是个重要的设计细节；
3. 跨域：`CorsConfig` 里 `allowCredentials(true)` 时必须用 `allowedOriginPatterns("*")`，直接 `allowedMethods` + `allowedOrigins("*")` 会冲突报错。

**动手**：
```bash
curl -N "http://localhost:8123/api/ai/manus/chat?message=把你好二字写入hello.txt文件"
```
观察 SSE 逐段输出的 Step 结果。

---

## 阶段 10：Vue 3 前端（提交 `55c89f0`）

> 添加"前端代码"

**学什么**：前端怎么消费后端的 SSE 流。前端小白重点读 4 个文件：

```
hui-ai-agent-frontend/src/
├── utils/sse.js          # ★ 核心：基于 fetch + ReadableStream 手写 SSE 解析
│                         #   （不用 EventSource：它不感知流的正常结束、
│                         #    不好支持"停止生成"等主动控制）
├── api/ai.js             # 两个 SSE 聊天接口的封装 + 开发/生产环境地址切换
├── components/ChatRoom.vue  # 通用聊天室组件（消息列表+输入框+打字机渲染）
└── views/                 # HomeView / LoveChatView / ManusChatView
```

**必须理解的点**：

1. `api/ai.js` 里开发环境**直连** `http://localhost:8123/api`（跨域请求），生产环境用相对路径 `/api`（同域部署）——这正是后端要写 `CorsConfig` 的原因；`vite.config.js` 里的 `/api` 代理（不 rewrite，因为后端本身有 context-path）是备用方案；
2. `utils/sse.js` 的解析套路值得背下来：`fetch` 拿到 `response.body.getReader()` → 按 `\n\n` 切分 SSE 事件 → 最后一段可能不完整，留在 buffer 等下一帧——**通用流式解析范式**；
3. 流式渲染：每收到一个 chunk 就追加到当前消息的 buffer，触发 Vue 响应式更新——「打字机效果」没有任何魔法；
4. `utils/markdown.js`：AI 输出的 Markdown 渲染成 HTML。

**动手**：`npm run dev` 后同时打开 `/love` 和 `/manus`，对比普通聊天（纯文本流）和智能体（按 Step 推送）的差异。

---

## 阶段 11：部署上线（提交 `7fa8fdf`、`8a317ff`）

> 添加后端 "serveless" 上线的配置 / 添加前端 "serveless" 上线的配置

**学什么**：AI 应用上生产的两件大事——**镜像瘦身**和**密钥安全**。

**读什么**：根目录 `Dockerfile`、`src/main/resources/application-prod.yml`、`hui-ai-agent-frontend/Dockerfile` + `nginx.conf`、`.dockerignore`

**必须理解的点**：

1. **多阶段构建**：阶段一 `maven:3.9-amazoncorretto-21` 打包，阶段二只拷贝 jar 到纯 JRE 镜像——镜像从 1GB+ 瘦到几百 MB，Serverless 冷启动更快；
2. **三层配置隔离**（这是全项目的密钥管理哲学，值得抄走）：
   - `application.yml`——非敏感配置，进 git；
   - `application-local.yml`——本地密钥，`.gitignore` + `.dockerignore` 双重忽略，**永不进仓库和镜像**；
   - `application-prod.yml`——生产非敏感配置进 git；**生产密钥一律部署平台环境变量注入**（`SPRING_AI_DASHSCOPE_API_KEY` 等，Spring Boot 环境变量优先级高于 jar 内所有 yml）；
3. `-XX:MaxRAMPercentage=75.0`：让 JVM 堆按容器内存配额自适应，适配 Serverless 内存限制；
4. 前端构建成静态文件由 Nginx 托管，`/api` 反代到后端——生产环境不再需要 Vite。

---

## 学完之后：检验清单 & 进阶方向

### 自测清单（全部能答出来 = 毕业 ✅）

1. ChatModel 和 ChatClient 的区别是什么？为什么要用 Advisor？
2. 手写一个「敏感词 + 权限」双拦截的 Advisor 要怎么做？
3. 对话记忆的三种实现怎么切换？`chatId` 在其中扮演什么角色？
4. RAG 的写入和检索各经历哪些步骤？查询重写解决什么问题？
5. `@Tool` 注解的方法是怎么被大模型「看到」并调用的？
6. ToolCallAgent 为什么要 `withProxyToolCalls(true)`？
7. Manus 的循环什么时候结束？哪两道保险防止死循环？
8. `Flux<String>` 和 `SseEmitter` 两种流式方案各自的适用场景？
9. 生产环境的 API Key 应该放在哪？为什么？

### 进阶方向

- **给 Manus 加记忆**：把 `messageList` 接到 `MysqlChatMemory`，让智能体跨会话记住任务；
- **Agent 并行**：参考 OpenManus 给 think 返回的多个工具调用加并行执行；
- **换模型**：本项目已演示 Ollama 接入，试试把 Manus 跑在本地开源模型上对比效果；
- **加评测**：给 RAG 检索质量写自动化评测（召回率/相关性），而不是靠肉眼；
- **可观测**：把 `MyLoggerAdvisor` 升级为调用耗时、Token 消耗统计。

---

> 📖 运行环境搭建、密钥申请、接口清单等实操信息见 [README.md](./README.md)。
> 学习建议：**每读完一个阶段，先 `git checkout` 到对应提交跑一遍，再回到最新代码看它后来长成了什么样**——「看着代码长大」是读开源项目最快的方式。
