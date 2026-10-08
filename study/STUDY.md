# STUDY.md —— Hui AI Agent 从 0 到 1 学习指南（小白友好版）

> **这份文档写给谁？** 就算你只有五年级的阅读水平、刚学会一点 Java，也能看懂。每个新东西都先用**生活里的比方**讲一遍，再看**项目里的真实代码**（一行一行拆给你看），最后**动手跑一遍**。
>
> **怎么用这份文档？** 本项目的 git 提交历史就是一条学习路径。文档把它拆成了 **11 个阶段**（阶段 0 ~ 阶段 11），每个阶段都是：
>
> 🎯 **打个比方**（这个技术像生活里的什么东西）→ 💻 **实战代码**（项目里的真代码，逐行拆解）→ 🧪 **动手试试**（照着敲命令）→ 🕳️ **小心踩坑**（前人摔过的跟头）
>
> 配套的 12 支讲解视频在 [video/](../video/README.md) 目录，每个阶段一支，可以先看视频再读文档。

---

## 学习路线总览

| 阶段 | 主题 | 像生活里的什么 | 对应提交 |
| --- | --- | --- | --- |
| 0 | 前置知识 | 认识 4 个新朋友 | - |
| 1 | 项目骨架 | 盖房子先搭框架 | `83d1208` |
| 2 | 调用大模型的 5 种方式 | 五种给笔友传话的办法 | `47e38f6` `1dbdd0b` |
| 3 | ChatClient 与 Advisor | 给 AI 一张身份卡 | `5259d50` |
| 4 | 自定义 Advisor | 学校门口的检查老师 | `513c3e4` `bcab20f` |
| 5 | 对话记忆 | AI 的笔记本 | `1913ab5` `b92588a` |
| 6 | RAG 检索增强 | 开卷考试 | `3c8d159`→`77a34fc` |
| 7 | 工具调用 | 给 AI 一个工具箱 | `abe317c`→`8fcf40d` |
| 8 | 手写 Manus 智能体 | 会自己做手抄报的机器人 ⭐ | `bd434b1` |
| 9 | 接口服务化与流式 | 烤一串上一串 | `1419c4d` `c377b50` |
| 10 | Vue3 前端 | 接盘子的服务员 | `55c89f0` |
| 11 | 部署上线 | 搬家与藏好钥匙 | `7fa8fdf` `8a317ff` |

配套 git 命令（把每次提交当一页教材）：

```bash
git log --oneline --reverse   # 按时间顺序列出所有提交
git show <提交号>             # 看这次提交改了什么（最好的教材！）
git checkout <提交号>         # 把代码倒回那个阶段，跑起来看效果
```

> 而且**每个阶段末尾**都有一个"⏪ 切到本阶段的原始代码"小节——不用自己翻提交号，照抄那里的命令，就能把项目"倒带"回那一课作者刚提交完的原版代码。

---

## 阶段 0：前置知识——先认识 4 个新朋友

### 🎯 打个比方

想象你要跟一个**住在云上的聪明笔友**（大模型）通信：

- 这个笔友什么都懂，但有两个毛病：**记性只有 7 秒**（每次读信都当第一次认识你），而且**只会写字**，不能替你动手做事。
- 整个项目做的事情，就是围绕这个笔友，配齐了**电话（调用）、身份卡（提示词）、笔记本（记忆）、参考书（RAG）、工具箱（Tool）**，最后把他培养成一个能自己干活的**机器人管家（Agent）**。

### 咱们的 4 个新朋友（Spring AI 的 4 个核心概念）

| 新朋友 | 是什么 | 像什么 |
| --- | --- | --- |
| **ChatModel** | 最底层的大模型电话，一问一答 | 一部直拨电话：你说话，笔友回话 |
| **ChatClient** | ChatModel 的升级版，能挂各种配件 | 一部带秘书的电话：秘书帮你递身份卡、翻笔记本 |
| **Advisor** | 挂在电话线上的检查员 | 学校门口的老师：先检查你的信，再决定放不放行 |
| **Tool** | 告诉笔友"你还能用这些工具" | 一个工具箱：笔友喊一声"用螺丝刀"，你的程序就动手拧 |

再认识两个进阶朋友（后面阶段主角）：

- **RAG**（检索增强生成）= **开卷考试**：答题前先翻书，找到相关那页再抄着答，不会的题也不瞎编。
- **ReAct** = **做手抄报的套路**：想一想（用哪本书？）→ 做一步（查资料）→ 看看结果 → 再想下一步……直到做完喊一声"完成！"

### 🧪 动手试试

按 [README.md「快速开始」](../README.md#快速开始) 准备三样东西：**JDK 21、一个灵积 API Key（笔友家的门卡）、MySQL**，把项目跑起来。跑起来了再往下学，事半功倍。

---

## 阶段 1：项目骨架（提交 `83d1208`）

### 🎯 打个比方

盖房子前先搭框架：承重墙（Spring Boot）、门牌号（端口）、小区名字（context-path）、门口传达室（健康检查接口），还有前台的**说明书柜台**（Swagger 接口文档）。这阶段什么 AI 功能都没有，就是先把房子立起来、让访客能敲到门。

本阶段的目标：**不翻代码仓库，只拿着这份手册，从零把骨架搭出来**。要搭的一共 5 样：`pom.xml`（进建材）→ `application.yml`（发门牌、摆说明书柜台）→ 启动类（大门）→ `Healthcontroller`（传达室）→ 跑起来测两遍（curl 一遍、Swagger 一遍）。

先看成品长什么样（这次提交的全部家当）：

```
hui-ai-agent/
├── pom.xml                                                 # 建材清单（依赖）
├── mvnw / mvnw.cmd / .mvn/                                 # Maven 包装器：电脑没装 Maven 也能构建
├── src/main/java/com/hui/huiaiagent/
│   ├── HuiAiAgentApplication.java                          # 大门（启动类）
│   └── controller/
│       └── Healthcontroller.java                           # 传达室（第一个接口）
├── src/main/resources/
│   ├── application.yml                                     # 主配置：端口 + Swagger
│   └── application.properties                              # 只有一行应用名（配置主力是 yml）
└── src/test/java/.../HuiAiAgentApplicationTests.java       # 脚手架自带的空测试类
```

### 💻 实战代码①：pom.xml——请工人、进建材

`pom.xml` 开头的 `<parent>` 是**总包公司（版本管家）**：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.4</version>
</parent>
```

它替你锁好一整套**互相兼容**的依赖版本——所以下面有些依赖不用写版本号，也不会出现"两个零件打架"。

本阶段一共 **4 个依赖**，每个是干嘛的：

| 依赖 | 像生活里的 | 作用 |
| --- | --- | --- |
| `spring-boot-starter-web` | 承重墙 + 水电 | Web 起步依赖：内含 Spring MVC 和**内嵌 Tomcat 服务器**，让 Java 程序能监听端口、收发 HTTP 请求。没有它，后面的 Controller 全是摆设 |
| `lombok` | 装修神器 | 编译时自动生成 getter/setter/构造器等样板代码，一个注解顶几十行。后面几十个实体类全靠它瘦身 |
| `hutool-all` | 瑞士军刀 | 国产 Java 工具库：发 HTTP 请求、读写文件、JSON 转换……阶段 2 用裸 HTTP 调大模型时的 `HttpRequest` 就是它 |
| `knife4j-openapi3-jakarta-spring-boot-starter` | 说明书柜台 | Swagger 接口文档组件：自动扫描 Controller 生成**在线接口文档**，还能在网页上直接调试接口（本阶段末尾就用它） |

照抄进 `<dependencies>` 即可：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.36</version>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.37</version>
    </dependency>
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.4.0</version>
    </dependency>
</dependencies>
```

一个容易看漏的细节：**knife4j 为什么带 `jakarta` 字样？** Spring Boot 3 把老的 `javax.*` 包名全换成了 `jakarta.*`，必须选 jakarta 版 starter；不带 jakarta 的老版是给 Boot 2 用的，混用启动就报错。

还有一段新手最容易整块跳过的地方——`</dependencies>` 后面的 **`<build>`**。打个比方：**依赖是建材，`<build>` 里的插件是施工队**——房子怎么编译、怎么打包，由施工队说了算。阶段 1 的完整 `<build>`（照抄）：

```xml
<build>
    <plugins>
        <!-- 施工队①：编译队长 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.36</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
        <!-- 施工队②：打包队长 -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

两位队长分别干什么（这也是 Lombok 在 pom.xml 里**出现两次**的原因）：

**① `maven-compiler-plugin`（编译队长）——负责把 `.java` 编译成 `.class`。** 它这段配置是在告诉编译器："编译的时候，请让 Lombok 上班。" Lombok 的魔法发生在**编译这一刻**：javac 编译你的类时，Lombok 以"注解处理器"（annotation processor）的身份介入，把 `@Data` 这类注解**现场展开**成 getter/setter/构造器，再一起编译。所以你源码里一个 getter 都没写，编译出来的 class 文件里却全都有。如果不配这段，编译器根本不认识 Lombok 的注解——一旦哪个类用了 `@Data`，直接编译报错"找不到 xxx 方法"。顺带解释依赖里那个 `<optional>true</optional>`：意思是"这栋楼自己用 Lombok，不强迫依赖我的邻居项目也用它"。

**② `spring-boot-maven-plugin`（打包队长）——负责"跑起来"和"打行李"。** 没有它，`mvnw.cmd spring-boot:run` 根本跑不了；打包时它还会把所有依赖塞进**一个能独立运行的"胖 jar"**（fat jar）——阶段 11 部署时 `java -jar app.jar` 一条命令就能启动，用的就是它打的包。但它这段配置偏偏**反着来**：把 Lombok **排除**（exclude）在 jar 之外。为什么？因为 Lombok 是**纯编译期工具**——编译完成、程序开跑的那一刻，getter/setter 早就生成进 class 里了，Lombok 本人已经没活干，没必要跟着进 jar 占地方（打进去还多一分版本冲突的隐患）。

一句话记住这两处 Lombok：**编译队长"请它来"（编译时生成代码），打包队长"送它走"（成品里不留它）**。以后看到 pom 里 Lombok 出现两次、一次引入一次排除，别当成重复配置——一个管进门，一个管出门。

（小预告：阶段 2 会给编译队长再追加 `<source>21</source>`、`<target>21</target>` 和 `--enable-preview` 三行，到那一章再说。）

### 💻 实战代码②：application.yml——门牌号 + 说明书柜台

整份配置（阶段 1 就这么多，可直接照抄）：

```yaml
spring:
  application:
    name: hui-ai-agent     # 应用名字（日志里的署名）

server:
  port: 8123               # 门牌号：这栋楼在 8123 号
  servlet:
    context-path: /api     # 小区名字：所有房间都挂在 /api 下面

# ---- Swagger 接口文档：springdoc 负责"生成"文档数据 ----
springdoc:
  swagger-ui:
    path: /swagger-ui.html     # 原版文档页面的入口
    tags-sorter: alpha         # 接口分组按字母排序
    operations-sorter: alpha   # 组内接口按字母排序
  api-docs:
    path: /v3/api-docs         # 接口描述数据（JSON）的地址，是文档页面的"数据源"
  group-configs:               # 哪些接口收进说明书
    - group: 'default'
      paths-to-match: '/**'    # 路径全收
      packages-to-scan: com.hui.huiaiagent.controller   # 但只扫 controller 这个包

# ---- knife4j 负责"展示"：给文档页面换上好看中文的皮肤 ----
knife4j:
  enable: true         # 打开 knife4j 增强界面
  setting:
    language: zh_cn    # 界面中文
```

**先记住两个门牌号**（后面前端、代理全都要跟它对齐，对不上就是 404，走错门了）：

1. `port: 8123` —— 以后找后端，一律去 `localhost:8123`；
2. `context-path: /api` —— 所有接口（**连文档页面也不例外**）都自动带 `/api` 前缀，比如健康检查的完整地址就是 `http://localhost:8123/api/health`。

**再把 Swagger 这套东西说清楚**（很多人只会打开页面，讲不出它是什么）：

- **Swagger / OpenAPI** 是一套"给接口自动拍说明书"的规范和工具。后端接口一多，谁记得住每个接口的地址、参数、返回值？让它自动扫描你的 Controller，生成一份**在线文档**，还能在网页上填好参数、点一下发送，直接调你的接口——不用写 curl、不用等前端。
- 这个项目里是**两层合作**：`springdoc`（扫描 Controller，生成接口描述数据 `/v3/api-docs`）→ `knife4j`（读这份数据，画出更好看、中文化的文档页 `doc.html`）。一个生产数据，一个负责展示。

### 💻 实战代码③：启动类——大门

`src/main/java/com/hui/huiaiagent/HuiAiAgentApplication.java`：

```java
@SpringBootApplication
public class HuiAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuiAiAgentApplication.class, args);
    }
}
```

`@SpringBootApplication` 是**三合一**注解：

1. **自动装配**：把依赖里现成的零件（比如内嵌 Tomcat）自动组装好，所以一行服务器配置都不用写；
2. **包扫描**：从 `com.hui.huiaiagent` 包开始，往下找所有带注解的类（`@RestController` 等），登记成 Spring 的零件；
3. **配置类**：宣告"这个项目从我这儿启动"。

⚠️ 扫描范围 = 启动类所在包**及其子包**——所以 Controller 必须放在 `com.hui.huiaiagent.controller` 这种子包里，放到外面就扫不到（启动不报错，访问 404，新手第一大坑）。

### 💻 实战代码④：第一个 Controller——门口的传达室

`controller/Healthcontroller.java`（整份照抄）：

```java
package com.hui.huiaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class Healthcontroller {

    @GetMapping
    public String healthCheck(){
        return "ok";
    }
}
```

逐行拆解：

1. `@RestController` —— 双合一：① 我是接口类（`@Controller`）；② 我的返回值直接写给对方，**不是**跳转网页（`@ResponseBody`）；
2. `@RequestMapping("/health")` —— 这类接口统一住在 `/health` 这个房间；
3. `@GetMapping` —— 只接 GET 请求（浏览器地址栏回车就是 GET）；方法上没写路径，就沿用类上的路径；
4. 最终地址 = **context-path + 类路径 + 方法路径** = `/api` + `/health` = `http://localhost:8123/api/health`。

小彩蛋：类名 `Healthcontroller` 的第二个 c 没大写（规范写法是 `HealthController`），照样能跑——**Spring 认注解，不认类名**。

### 🧪 动手试试：从零搭一遍 + 两把尺子各测一次

从零开始也行：去 [start.spring.io](https://start.spring.io) 生成空项目（Maven、Java 21、Spring Boot 3.4.4，依赖先勾 Spring Web），生成出来的就是 `mvnw`、`pom.xml` 那一套。然后：

1. **pom.xml**：照"实战代码①"补齐 4 个依赖，再检查 `<build>` 里两个插件（Lombok 的"请它来 + 送它走"）都在；
2. **application.yml**：照"实战代码②"整份抄进去（`application.properties` 留着不用管，配置集中在 yml 写）；
3. **启动类**：照"实战代码③"，包名 `com.hui.huiaiagent` 一字不差；
4. **传达室**：新建 `controller` 包，写进 `Healthcontroller.java`；
5. **启动**：

```bash
mvnw.cmd spring-boot:run        # 盖好房子，开门
```

用**两把尺子**各测一次服务（这也是以后每加一个接口的固定动作）：

尺子① —— curl / 浏览器直接敲门：

```bash
curl http://localhost:8123/api/health
```

尺子② —— Swagger 在线调试：

1. 打开 `http://localhost:8123/api/doc.html`（⚠️ 别忘了 `/api` 前缀，文档页面一样要过小区大门）；
2. 左侧接口列表找到 `GET /health`，点开 → 切到"调试"标签 → 点"发送"；
3. 响应内容里出现 `"ok"`，成功！

以后每个阶段新增的接口（阶段 9 的流式对话、Manus 都算），都可以先在 `doc.html` 里调试通过，再交给前端——**接口文档 = 前后端之间的合同**，这就是阶段 1 要把 Swagger 一起搭进骨架的原因。

两把尺子都听到了"ok"，阶段 1 毕业！

### 🕳️ 小心踩坑

- **Controller 放错包 → 404**：必须放在启动类所在包的子包里（如 `com.hui.huiaiagent.controller`）。Spring 扫不到它**不会报错**，只会在访问时 404；
- **访问 `doc.html` 忘加 `/api`**：`context-path` 对文档页面同样生效，正确地址是 `http://localhost:8123/api/doc.html`；
- **IDEA 里 Lombok 报红**：新版 IDEA 已内置 Lombok；若 getter/setter 不生成，检查 `Settings → Build → Compiler → Annotation Processors` 是否开启（命令行 `mvnw.cmd` 构建不受影响，pom 里已配好）；
- **knife4j 版本选错**：Spring Boot 3.x 必须用 `knife4j-openapi3-jakarta-*` 4.x 版本，用老的 openapi2 版本启动直接报错。





### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 83d1208        # ★ 阶段 1 终点提交号：整个项目瞬间"倒带"回骨架刚搭好的样子
mvnw.cmd spring-boot:run    # 用作者当时的原版代码跑一遍，和你自己搭的逐文件对比
#   验证：curl http://localhost:8123/api/health → ok；打开 /api/doc.html 能调试
git checkout main           # 学完切回最新代码（随时可切，啥都没丢）
```

**三条使用须知（后面各阶段不再重复）**：

1. `git checkout 提交号` 是"倒带"不是删除——工作目录回到那次提交的瞬间，你的 main 分支和后续代码都原封不动；
2. 这个状态叫**分离头指针**（detached HEAD），**只读学习用，别在这里写代码**；实在想在旧版本上改着玩，先开个分支：`git switch -c study-阶段1 83d1208`；
3. 好消息：`application-local.yml`（你的钥匙）、`tmp/`（工具产物）、`node_modules` 这些**没被 git 跟踪的文件不会被切走**——切到任何旧版本都能直接跑。

---

## 阶段 2：调用大模型的 5 种方式（提交 `47e38f6`、`1dbdd0b`）

### 🎯 打个比方

同一个目的——告诉笔友一句话、听他回一句——有 5 种传话办法，从最笨到最聪明：

| 方式 | 比方 | 要加的依赖 | 代码在 |
| --- | --- | --- | --- |
| ① 裸 HTTP | 自己写好信封跑去邮局寄 | hutool-all（阶段 1 已进） | `demo/invoke/HttpAiInvoke.java` |
| ② 官方 SDK | 打电话给客服，客服帮你转接 | dashscope-sdk-java | `demo/invoke/SdkAiInvoke.java` |
| ③ Spring AI ★ | 请了个秘书，你说一句话他全办好 | spring-ai-alibaba-starter | `demo/invoke/SpringAiAiInvoke.java` |
| ④ LangChain4j | 另一家秘书公司（对比用） | langchain4j-community-dashscope | `demo/invoke/LangChainAiInvoke.java` |
| ⑤ Ollama | 把笔友搬到自己家里住（本地模型，免费） | spring-ai-ollama-spring-boot-starter | `demo/invoke/OllamaAiInvoke.java` |

本阶段目标：**在阶段 1 骨架上，办门卡 → 加依赖 → 写配置 → 建 5 个演示类，把 5 条路全部跑通**。跟着下面第 0~2 步做，再照抄文末"完整代码"，就能搭出来。

### 🧾 第 0 步：先办"门卡"（前置准备）

1. **申请灵积（百炼）API Key**：打开 [百炼控制台](https://bailian.console.aliyun.com/) → 左侧"API-KEY" → 创建，得到一串 `sk-` 开头的钥匙。这把钥匙 ①②③④ 都要用（模型服务商文档见[阿里云百炼](https://help.aliyun.com/zh/model-studio/)）。
2. **建本地密钥文件** `src/main/resources/application-local.yml`（这个文件名已被 git 忽略，真实钥匙永远不进仓库——阶段 11 会把这套"藏钥匙"手法讲透，这里先照做）：

   ```yaml
   spring:
     ai:
       dashscope:
         api-key: sk-你的真实key
   ```

3. **顺手在 `.gitignore` 里加一行**（防止钥匙被拍进 git 照片）：

   ```text
   src/main/resources/application-local.yml
   ```

4. **（只想玩 ⑤ 才需要）装 Ollama**：去 [ollama.com/download](https://ollama.com/download) 装好（装完它常驻后台，11434 端口），再拉一个小模型：

   ```bash
   ollama pull granite4.2:3b   # 也可以换任何小模型，如 qwen2.5:0.5b
   ollama list                 # 记住拉下来的模型名，配置里要填它
   ```

### 📦 第 1 步：加依赖（pom.xml）

5 个依赖一目了然（含官方文档和 Maven 仓库地址，想深挖就点进去看）：

| 依赖（版本） | 给哪条路用 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `cn.hutool:hutool-all`（5.8.37，阶段 1 已引入） | ① 发 HTTP 请求 | [hutool.cn](https://hutool.cn/) | [mvnrepository.com/…/hutool-all](https://mvnrepository.com/artifact/cn.hutool/hutool-all) |
| `com.alibaba:dashscope-sdk-java`（2.19.1，**锁死别升**） | ② 灵积官方 SDK | [阿里云百炼文档](https://help.aliyun.com/zh/model-studio/) | [mvnrepository.com/…/dashscope-sdk-java](https://mvnrepository.com/artifact/com.alibaba/dashscope-sdk-java) |
| `com.alibaba.cloud.ai:spring-ai-alibaba-starter`（1.0.0-M6.1） | ③ Spring AI（主力） | [java2ai.com](https://java2ai.com/) · [GitHub](https://github.com/alibaba/spring-ai-alibaba) | [mvnrepository.com/…/spring-ai-alibaba-starter](https://mvnrepository.com/artifact/com.alibaba.cloud.ai/spring-ai-alibaba-starter) |
| `dev.langchain4j:langchain4j-community-dashscope`（1.0.0-beta2） | ④ LangChain4j | [docs.langchain4j.dev](https://docs.langchain4j.dev/) | [mvnrepository.com/…/langchain4j-community-dashscope](https://mvnrepository.com/artifact/dev.langchain4j/langchain4j-community-dashscope) |
| `org.springframework.ai:spring-ai-ollama-spring-boot-starter`（1.0.0-M6） | ⑤ Ollama | [Spring AI 文档](https://docs.spring.io/spring-ai/reference/) · [Ollama 官网](https://ollama.com/) | [mvnrepository.com/…/spring-ai-ollama-spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-ollama-spring-boot-starter) |

pom.xml 里新增的 `<dependencies>`（照抄）：

```xml
<!-- 灵积官方 SDK（版本需与 langchain4j-community-dashscope 1.0.0-beta2 兼容，2.2x 会报 NoSuchMethodError） -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.19.1</version>
</dependency>
<!-- Spring AI Alibaba：③ 的主力框架 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter</artifactId>
    <version>1.0.0-M6.1</version>
</dependency>
<!-- LangChain4j 社区版对灵积的适配：④ 对比用 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-community-dashscope</artifactId>
    <version>1.0.0-beta2</version>
</dependency>
<!-- Ollama 本地模型：⑤ 用（小插曲：作者当时漏加了这个依赖，到下一个提交才补进 pom；自己搭建时一次加齐即可） -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    <version>1.0.0-M6</version>
</dependency>
```

**还必须在 pom.xml 末尾加一个里程碑仓库**，否则 M 版本的 Spring AI 依赖根本下载不到（它们不在 Maven 中央仓库）：

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### ⚙️ 第 2 步：写配置（application.yml）

先看结论——**每条路需要什么配置**：

| 方式 | 要写的配置 | 钥匙从哪来 |
| --- | --- | --- |
| ① 裸 HTTP | 不用改 yml | 代码里读 `TestApiKey`（它直接去读 application-local.yml） |
| ② 官方 SDK | 不用改 yml | 同上 |
| ③ Spring AI | `spring.ai.dashscope.api-key` | yml → Spring 自动装配 |
| ④ LangChain4j | 不用改 yml | 同①② |
| ⑤ Ollama | `spring.ai.ollama.base-url` + `chat.model` | 本地模型，不要钥匙 |

所以在阶段 1 的 `application.yml` 上加这几行就够了（Ollama 那段先注释着，玩 ⑤ 时再解开）：

```yaml
spring:
  application:
    name: hui-ai-agent
  # 启动时自动加载本地密钥（optional: 表示文件不存在也不报错）
  config:
    import: optional:classpath:application-local.yml
  ai:
    dashscope:
      # 真实 key 在 application-local.yml 里，这里只是占位符
      api-key: sk-请把真实key配置在application-local.yml中
    # ↓ 玩 ⑤ 时解开注释，model 填你 ollama pull 下来的模型名
#    ollama:
#      base-url: http://localhost:11434
#      chat:
#        model: granite4.2:3b
```

另外建一个类 `demo/invoke/TestApiKey.java`——它解决一个真实的小痛点：①②④是普通 main 类（不归 Spring 管），读不到 Spring 的配置；它就用 Spring 自带的 `YamlPropertySourceLoader` **直接去 classpath 读 application-local.yml**，让"一把钥匙两家共用"（完整代码在文末附录）。

### 💻 实战代码①：裸 HTTP（看清本质）

`HttpAiInvoke.java` 节选：

```java
// 1. 笔友家的地址（门牌号要一字不差）
String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

// 2. 把想说的话装进信封（JSON 格式）
JSONObject userMessage = new JSONObject();
userMessage.put("role", "user");          // 我是"用户"角色
userMessage.put("content", "你是谁？");    // 想问的话

// 3. 塞进邮筒（发出 HTTP 请求）
HttpResponse response = HttpRequest.post(url)
        .addHeaders(headers)              // 信封上贴"门卡"（API Key）
        .body(requestBody.toString())
        .execute();
```

信封（请求体）一共三格，对照着记：`model`（找哪个笔友）、`input.messages`（系统人设 + 你说的话）、`parameters.result_format`（回信按"消息"格式打包）。请求地址、参数名这些细节都在[百炼的 HTTP API 文档](https://help.aliyun.com/zh/model-studio/)里。

拆解：**调用大模型的本质，就是往一个网址发一段 JSON，再收回一段 JSON**。后面所有花哨的框架，做的都是"帮你写信封"这一件事。

### 💻 实战代码②：官方 SDK（少写很多字）

`SdkAiInvoke.java` 节选：

```java
Message userMsg = Message.builder()
        .role(Role.USER.getValue())
        .content("你是谁？")
        .build();                              // 说的话打包好

GenerationParam param = GenerationParam.builder()
        .apiKey(TestApiKey.API_KEY)            // 门卡
        .model("qwen-plus")                    // 找哪个笔友（模型名）
        .messages(Arrays.asList(systemMsg, userMsg))
        .build();

return gen.call(param);                        // 拨号，等回话
```

拆解：不用自己写地址、贴信封了，把"门卡、找谁、说什么"交给 `builder` 打包，一句 `gen.call()` 搞定。`apiKey` 支持配环境变量（`DASHSCOPE_API_KEY`），本项目选择显式传，来源就是 `TestApiKey`。（可用的模型名见[模型列表](https://help.aliyun.com/zh/model-studio/getting-started/models)，`qwen-plus` 是性价比常用款）

### 💻 实战代码③：Spring AI（本项目的主力）

`SpringAiAiInvoke.java` 全部核心：

```java
@Resource
private ChatModel dashscopeChatModel;      // Spring 递过来的"AI 电话"

AssistantMessage output = dashscopeChatModel
        .call(new Prompt("你好，我是小辉"))  // 说出问题
        .getResult().getOutput();
System.out.println(output.getText());      // 打印笔友的回信
```

拆解：**你一行配置都没写电话号码**——自动装配的链条是：`spring-ai-alibaba-starter` 在依赖里 → Spring Boot 启动时读到它 → 去 yml 拿 `spring.ai.dashscope.api-key` → 造好一个 `dashscopeChatModel` 零件递给你注入。这个"自动造好"的把戏叫**自动装配**，后面整个项目用的都是它。

还有个新面孔 `CommandLineRunner`：实现它的类会在 **Spring Boot 启动完成后自动执行 `run()` 方法**——所以这类 Demo 不需要 Controller 和浏览器，启动项目就是在做实验。注意类上那行 `//@Component` 被注释了：不注释，它就真的会被 Spring 扫到、每次启动都跑（还会调一次大模型花一次钱）。

### 💻 实战代码④：LangChain4j（另一家秘书公司）

`LangChainAiInvoke.java` 全部核心：

```java
ChatLanguageModel qwenModel = QwenChatModel.builder()
        .apiKey(TestApiKey.API_KEY)     // 门卡
        .modelName("qwen-max")          // 找哪个笔友
        .build();
String answer = qwenModel.chat("我是小辉，一名程序员");   // 一行完成问答
```

拆解：LangChain4j 是另一套流行的 AI 应用框架，社区版同样封装了灵积。和 ③ 对比着记：Spring AI 的 `ChatModel` 靠 Spring 注入；LangChain4j 自己 `builder` 一个就能用，`chat()` 直接返回字符串。本项目只用它开开眼界、主力仍是 Spring AI——但**"同一件事，两家框架都封装了一遍"**这件事本身值得记住，面试聊框架选型时用得上。

### 💻 实战代码⑤：Ollama（换朋友不换电话）

前提：按第 0 步装好 Ollama、拉好模型，并解开 application.yml 里 `spring.ai.ollama` 的注释。

`OllamaAiInvoke.java` 核心：

```java
@Resource
private ChatModel ollamaChatModel;    // 注意！只是换了个名字

// 下面的代码和 SpringAiAiInvoke 一模一样……
AssistantMessage answer = ollamaChatModel.call(new Prompt("你好，我是小辉"))
        .getResult().getOutput();
```

拆解：想用本地模型？**只换注入的 ChatModel，业务代码一个字不改**。这就是框架统一接口的好处——笔友换了，写信的方式不变。`base-url` 不写时默认就是 `http://localhost:11434`（Ollama 的老家门口）。

### 🧪 动手试试：5 条路各跑一遍

①②④是普通 main 类——在 IDEA 里打开对应文件，**右键 → Run 'Xxx.main()'** 直接跑：

| 跑哪个 | 预期看到 |
| --- | --- |
| `HttpAiInvoke` | 控制台打印一大段 JSON（笔友的回信裹在 JSON 里） |
| `SdkAiInvoke` | 一段格式化的 JSON 结果 |
| `LangChainAiInvoke` | 一句中文自我介绍（纯文本，最干净） |

③⑤走 Spring：把类上的 `//@Component` 注释**去掉一个**（比如 ③）→ 启动项目：

```bash
mvnw.cmd spring-boot:run
```

启动日志滚完后，控制台会直接打印笔友的回信。**测完记得把 `//@Component` 重新注释上**——不然下次启动又会自动跑（③⑤都开着的话，两个会一起跑）。

跑 ⑤ 之前可以先确认 Ollama 活着：

```bash
curl http://localhost:11434/api/tags     # 列出已拉取的模型
```

### 🕳️ 小心踩坑

- `dashscope-sdk-java` 必须**锁死 2.19.1** 版本（pom.xml 里已锁好）。升到 2.2x 会跟 langchain4j 打架，报 `NoSuchMethodError`。
- 这些 Demo 类默认加了 `//@Component` 注释（不启动），想跑哪个就把注释去掉重启项目；**别同时开两个**。
- **忘了配里程碑仓库** → `mvn` 报 `Could not find artifact org.springframework.ai:…`。M（Milestone）版本不在 Maven 中央仓库，pom 里的 `spring-milestones` 仓库一段不可省。
- **application-local.yml 没建/没填真实 key** → ①②④一跑就抛 `IllegalStateException`（报错信息里写清楚了怎么修）；③能启动但一调用就 401（钥匙是占位符）。
- **⑤ 报 `Connection refused: localhost/11434`** → Ollama 没在跑（装完要启动）；**报模型不存在** → yml 里的 `model` 名和 `ollama list` 对不上。

### 📦 附录：阶段 2 完整代码（照抄可跑）

目录结构（在阶段 1 基础上新增一个包）：

```
src/main/java/com/hui/huiaiagent/demo/invoke/
├── TestApiKey.java          # 钥匙中转站
├── HttpAiInvoke.java        # ① 裸 HTTP
├── SdkAiInvoke.java         # ② 官方 SDK
├── SpringAiAiInvoke.java    # ③ Spring AI
├── LangChainAiInvoke.java   # ④ LangChain4j
└── OllamaAiInvoke.java      # ⑤ Ollama
```

**1）pom.xml**：依赖与仓库见上文"第 1 步"的两个 XML 块，原样贴进 pom 即可（作者那次提交还顺手给 `maven-compiler-plugin` 加了 `<source>21</source>`、`<target>21</target>` 和 `--enable-preview`，照仓库抄就行）。

**2）application.yml**（阶段 2 完整形态 = 阶段 1 全文 + "第 2 步"新增的几行；小提示：仓库这份提交不小心删了阶段 1 的 knife4j 两行配置，实测不影响 `doc.html` 打开，但建议保留）：

```yaml
spring:
  application:
    name: hui-ai-agent
  # 启动时自动加载本地密钥配置（application-local.yml 被 git 忽略，不存在也不报错）
  config:
    import: optional:classpath:application-local.yml
  ai:
    dashscope:
      # 真实 key 配置在 application-local.yml 中，此处仅为占位符
      api-key: sk-请把真实key配置在application-local.yml中
    # ollama 的注入（玩 ⑤ 时解开注释）
#    ollama:
#      base-url: http://localhost:11434
#      chat:
#          model: granite4.2:3b

server:
  port: 8123
  servlet:
    context-path: /api
# springdoc-openapi
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'default'
      paths-to-match: '/**'
      packages-to-scan: com.hui.huiaiagent.controller
# knife4j
knife4j:
  enable: true
  setting:
    language: zh_cn
```

**3）TestApiKey.java**：

```java
package com.hui.huiaiagent.demo.invoke;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;

/**
 * 原生 SDK 演示用的 API Key。
 * 自动从 classpath 下的 application-local.yml 读取 spring.ai.dashscope.api-key，
 * 该文件已被 git 忽略，真实密钥不会进入仓库。
 */
public interface TestApiKey {

    String API_KEY = loadApiKey();

    private static String loadApiKey() {
        try {
            return new YamlPropertySourceLoader()
                    .load("application-local", new ClassPathResource("application-local.yml"))
                    .get(0)
                    .getProperty("spring.ai.dashscope.api-key")
                    .toString();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "读取 src/main/resources/application-local.yml 中的 spring.ai.dashscope.api-key 失败，"
                            + "请确认该文件存在且格式正确", e);
        }
    }
}
```

**4）HttpAiInvoke.java**：

```java
package com.hui.huiaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpAiInvoke {
    public static void main(String[] args) {
        // 替换为你的实际 API 密钥
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + TestApiKey.API_KEY);
        headers.put("Content-Type", "application/json");

        // 设置请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");

        JSONObject input = new JSONObject();
        JSONObject[] messages = new JSONObject[2];

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages[0] = systemMessage;

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "你是谁？");
        messages[1] = userMessage;

        input.put("messages", messages);
        requestBody.put("input", input);

        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if (response.isOk()) {
            System.out.println("请求成功，响应内容：");
            System.out.println(response.body());
        } else {
            System.out.println("请求失败，状态码：" + response.getStatus());
            System.out.println("响应内容：" + response.body());
        }
    }
}
```

**5）SdkAiInvoke.java**：

```java
package com.hui.huiaiagent.demo.invoke;// 建议dashscope SDK的版本 >= 2.12.0

import java.util.Arrays;
import java.lang.System;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;

public class SdkAiInvoke {

    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你是谁？")
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(TestApiKey.API_KEY)
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

    public static void main(String[] args) {
        try {
            GenerationResult result = callWithMessage();
            System.out.println(JsonUtils.toJson(result));
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        System.exit(0);
    }
}
```

（末尾那句 `System.exit(0)` 有讲究：SDK 内部的异步线程不肯自己退出，不强制退出的话 main 会一直挂着。）

**6）SpringAiAiInvoke.java**：

```java
package com.hui.huiaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// 取消注释即可在 SpringBoot 项目启动时执行
//@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是小辉"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}
```

**7）LangChainAiInvoke.java**：

```java
package com.hui.huiaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        ChatLanguageModel qwenModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-max")
                .build();
        String answer = qwenModel.chat("我是小辉，一名程序员");
        System.out.println(answer);
    }
}
```

**8）OllamaAiInvoke.java**：

```java
package com.hui.huiaiagent.demo.invoke;

import jakarta.annotation.Resource;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型（Ollama）
 */
// 取消注释后，项目启动时会执行
//@Component
public class OllamaAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel ollamaChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("你好，我是小辉"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());
    }
}
```

5 条路全部打通（curl 般的 ①、builder 的 ②④、自动装配的 ③⑤），阶段 2 毕业！

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 1dbdd0b        # ★ 阶段 2 终点：五种调用方式全部就位（含 Ollama）
# 中间对照：git checkout 47e38f6   ← 刚提交四件套（HTTP/SDK/SpringAI/LangChain4j），还没有 Ollama
# 在旧代码上跑：IDEA 里右键运行 demo/invoke 下的 5 个类（③⑤记得解开 //@Component）
git checkout main           # 切回最新
```

（使用须知见阶段 1 的"⏪"小节：倒带不删除、别在分离头指针下写代码、本地未跟踪文件不受影响。）

---

## 阶段 3：ChatClient 与 Advisor——恋爱大师诞生（提交 `5259d50`）

### 🎯 打个比方

裸电话（ChatModel，阶段 2 ③用的）只会一问一答，而且每次都当你是陌生人。想要一个**恋爱专家**，怎么办？给他配一个**秘书**（ChatClient）：

- 秘书每次打电话前，先给笔友递一张**身份卡**："你现在是恋爱专家"（系统提示词）；
- 再把**笔记本**翻开放旁边（记忆 Advisor：打电话前念历史、挂电话后记新话）；
- 你只要说一句话，秘书全办好。

本阶段目标：**在阶段 2 基础上写出第一个真正的 AI 应用类 `LoveApp`——恋爱大师**，并用一个"三句话验证记忆"的测试类证明他真的记得住你。

先对比一眼阶段 2 和阶段 3 的差别（就差在"配了秘书"）：

| | 阶段 2（SpringAiAiInvoke） | 阶段 3（LoveApp） |
| --- | --- | --- |
| 用的电话 | `ChatModel` 裸电话 | `ChatClient` 带秘书的电话 |
| 身份 | 每次都是陌生人 | 每次自动带"恋爱专家"身份卡 |
| 记忆 | 金鱼记忆 | 记忆管理员翻笔记本 |
| 写法 | `.call(new Prompt(...))` | `.prompt().user().call()` 链式 |

### 🧾 第 0 步：前置准备

**不用办任何新门卡**——钥匙还是阶段 2 那把（`application-local.yml` 里的 `spring.ai.dashscope.api-key`）。只提醒一件事：本阶段开始用**测试类**驱动实验，`@SpringBootTest` 会把整个应用真启动、真调大模型——**每跑一次测试就是真实花钱**，心里有数即可。

### 📦 第 1 步：加依赖（pom.xml）

先说个好消息：**ChatClient、ChatMemory、MessageChatMemoryAdvisor 都不需要新依赖**——它们就藏在阶段 2 引入的 `spring-ai-alibaba-starter` 里（ChatClient 概念文档见 [Spring AI 文档](https://docs.spring.io/spring-ai/reference/api/chatclient.html)）。Ollama 的 starter 也**已经在阶段 2 加过了**（史实小注：作者实际是在本阶段的提交 `5259d50` 里才把它补进 pom——按手册搭建的你，这里不用再动它）。真正要新加的只有 **2 个**，都是为本项目的**第一个测试类**服务的：

| 依赖（版本） | 为什么这时候加 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `org.springframework.boot:spring-boot-starter-test`（test scope） | 本项目**第一个测试类**靠它跑（含 JUnit 5、断言库、Mockito 等） | [spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) | [mvnrepository.com/…/spring-boot-starter-test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test) |
| `junit:junit`（test scope） | 测试断言框架 | [junit.org/junit5](https://junit.org/junit5/) | [mvnrepository.com/…/junit](https://mvnrepository.com/artifact/junit/junit) |

照抄进 `<dependencies>`：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>
```

注意 `<scope>test</scope>`：**只在跑测试时生效，不会打进最终 jar**——和阶段 1 讲的"Lombok 只在编译期上班"是同一种"工具用完就送走"的思路。

### ⚙️ 第 2 步：写配置（application.yml）

**一行都不用改。** ChatClient 是代码里 `builder` 出来的、记忆先存内存（`InMemoryChatMemory`），都不需要配置文件插手。配置文件的存在感，要到阶段 5（数据库密码）和阶段 6（向量库）才回来。

### 💻 实战代码①：LoveApp 的"装配车间"（构造器）

新建包 `app`，写 `LoveApp.java`。它的构造器（**全项目最重要的 10 行**，后面所有玩法都在这上面长）：

```java
public LoveApp(ChatModel dashscopeChatModel){
    //初始化基于内存的对话记忆
    ChatMemory chatMemory = new InMemoryChatMemory();
    //初始化 chatClient
    chatClient = ChatClient.builder(dashscopeChatModel)       // ① 拿到底层电话（阶段 2 的 ChatModel）
            .defaultSystem(SYSTEM_PROMPT)                     // ② 递上"恋爱专家"身份卡
            .defaultAdvisors(                                 // ③ 往电话线上挂检查员
                    new MessageChatMemoryAdvisor(chatMemory)  //    记忆管理员（阶段 5 的主角，先领最简版）
            )
            .build();                                         // ④ 秘书上岗！
}
```

逐行拆解：

1. **构造器为什么能收到 `ChatModel`？** 类上标了 `@Component`，Spring 创建它时会自动把阶段 2 自动装配好的"AI 电话"递进来——这叫**构造器注入**；
2. `new InMemoryChatMemory()` —— 造一个"记在脑子里"的笔记本：最快，但程序一重启全忘（阶段 5 会换成文件本、数据库本）；
3. `defaultSystem(SYSTEM_PROMPT)` —— 以后**每一次**打电话，秘书都自动先递这张身份卡，不用你反复说；
4. `defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))` —— 把笔记本交给记忆管理员。他的分工一句话：**打电话前翻开本子念历史，挂电话后把新对话写进本子**。`defaultAdvisors` 这个位置以后还能继续挂别的顾问（阶段 4 的日志员、违禁词检查员就在这排队）。

### 💻 实战代码②：身份卡 SYSTEM_PROMPT（值得背下来的模板）

```java
private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
        "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
        "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
        "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";
```

拆解写身份卡的四个要点：**身份是谁 → 开场说什么 → 按情况分几类问 → 引导对方讲细节**。学会这个套路，你能给 AI 立任何人设（数学老师、旅游向导……）。

### 💻 实战代码③：发起一次对话 doChat

```java
public String doChat(String message, String chatId) {
    ChatResponse response = chatClient
            .prompt()                       // ① 拿出信纸
            .user(message)                  // ② 写上用户的话
            .advisors(spec -> spec
                    .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)  // ③ 告诉记忆管理员：用 chatId 这本笔记
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))        //    只带最近 1 条历史
            .call()                         // ④ 寄出！
            .chatResponse();
    String content = response.getResult().getOutput().getText();     // ⑤ 抄下回信
    log.info("content: {}", content);
    return content;
}
```

`chatId` 就是**笔记本的编号**：同一个 `chatId` 的对话用同一本笔记，AI 才记得住你们聊过什么；换了 `chatId` 就是换了本子，从头认识。

`CHAT_MEMORY_RETRIEVE_SIZE_KEY` 这次提交里设的是 **1**（最省 token 的写法）——意思是每次打电话只把**最近 1 条**历史念给笔友听。太少会"失忆"（隔一句的话就忘了），太多费钱；仓库后来把它调到了 10，权衡点就在这。

### 💻 实战代码④：测试类——三句话验证记忆

`src/test/java` 目录**首次登场**！新建包 `app`，写 `LoveAppTest.java`：

```java
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是程序员小der";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        message = "我想让另一半（perper）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
```

拆解——这三轮对话是**精心设计的记忆实验**：

1. `@SpringBootTest` —— 测试不是干跑：它会**把整个 Spring Boot 应用真的启动起来**（所以能 `@Resource` 注入 LoveApp，也所以 application-local.yml 里的钥匙必须配好）；
2. 三轮全用**同一个 chatId**（同一个笔记本）：第一轮自报家门 → 第二轮说出"另一半叫 perper" → 第三轮问"我的另一半叫什么？"；
3. **判卷标准**：第三轮的回答里能出现"perper"，记忆就是真的生效了——要知道裸 ChatModel 是金鱼记忆，绝无可能答对；`Assertions.assertNotNull(answer)` 只是个保底断言（回信不为空），真正的"判卷"要你亲眼看第三轮的 `content:` 日志。

### 💻 实战代码⑤：加分招——结构化输出（让 AI 填表格）

> 诚实说明：翻 git 会发现 `doChatWithReport` 和配套的 victools 依赖是**下一个提交（`513c3e4`，阶段 4）才进仓库**的。但这招是 ChatClient 的基本功，就在本章先教会你——想现在体验，按下面两步加即可。

第一步，补一个依赖（负责把 Java 类翻译成"JSON 表格说明书"，AI 才知道往哪格填）：

| 依赖 | 官方文档 | Maven 仓库 |
| --- | --- | --- |
| `com.github.victools:jsonschema-generator`（4.38.0） | [victools.github.io](https://victools.github.io/jsonschema-generator/) | [mvnrepository.com/…/jsonschema-generator](https://mvnrepository.com/artifact/com.github.victools/jsonschema-generator) |

```xml
<!--	结构化数据输出的json转化-->
<dependency>
    <groupId>com.github.victools</groupId>
    <artifactId>jsonschema-generator</artifactId>
    <version>4.38.0</version>
</dependency>
```

第二步，往 LoveApp 里加一个方法（和一个"表格类"）：

```java
//结构化输出实体类（record：Java 16+ 的"一行实体类"，自动带构造器/getter）
record LoveReport(String title, List<String> suggestions) {
}

public LoveReport doChatWithReport(String message, String chatId) {
    LoveReport loveReport = chatClient
            .prompt()
            .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
            .user(message)
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .call()
            .entity(LoveReport.class);   // ✨ 告诉AI：别写作文了，按这个表格填！
    log.info("loveReport: {}", loveReport);
    return loveReport;
}
```

拆解：平时 AI 回答是一大段文字；用 `.entity(类.class)`，框架会先把 `LoveReport` 翻译成一份 JSON 表格说明书塞给 AI，AI 照格填好，框架再把它**自动变回 Java 对象**——`loveReport.title()`、`loveReport.suggestions()` 直接就能用。**这是 AI 和普通程序对接的万能接口**（阶段 9 做接口服务化时全靠它）。

### 🧪 动手试试

**实验①：记忆生效了吗？**（用"实战代码④"里写好的 testChat）

```bash
mvnw.cmd test -Dtest=LoveAppTest#testChat
```

盯控制台：三轮 `content: {...}` 依次打出，**重点看第三轮**——回答里冒出"perper"，记忆生效，实验成功！

**实验②：加分招——AI 填的"表格"长什么样？** 往 `LoveAppTest` 里补一个测试方法（模仿 testChat 的写法，把结果打印出来肉眼看）：

```java
// 加分招的测试方法（结构化输出）：把 AI 填好的"表格"打印出来看
@Test
void doChatWithReport() {
    String chatId = UUID.randomUUID().toString();
    LoveApp.LoveReport report = loveApp.doChatWithReport("你好，我是程序员小der，我想让另一半更爱我", chatId);
    Assertions.assertNotNull(report);
    System.out.println("报告标题：" + report.title());
    report.suggestions().forEach(s -> System.out.println("建议：" + s));
}
```

跑起来：

```bash
mvnw.cmd test -Dtest=LoveAppTest#doChatWithReport
```

控制台会看到类似这样的输出（每次内容不同，但**格式是固定的表格**）：

```text
报告标题：小der的恋爱报告
建议：每天留出 15 分钟专注倾听对方分享的日常
建议：每周安排一次双方都期待的共同活动
建议：把"你该怎么爱我"换成"我们可以一起……"来表达
```

——AI 的回答变成了能直接用代码点出来的 Java 对象（`report.title()`、`report.suggestions()`），这就是 AI 和普通程序对接的样子。

**实验③（挑战题实测）：换笔记本 = 从头认识？** 再往 LoveAppTest 里加一个方法（这招仓库代码里没有，是手册为你加的）：

```java
// 挑战题实测：前两句用 chatIdA 说悄悄话，第三句换全新的 chatIdB 再问——它还认得你吗？
@Test
void testChatWithNewChatId() {
    String chatIdA = UUID.randomUUID().toString();
    loveApp.doChat("你好，我是程序员小der", chatIdA);
    loveApp.doChat("我想让另一半（perper）更爱我", chatIdA);

    // 换一本全新的笔记本再问
    String chatIdB = UUID.randomUUID().toString();
    String answer = loveApp.doChat("我的另一半叫什么来着？刚跟你说过，帮我回忆一下", chatIdB);
    Assertions.assertNotNull(answer);
}
```

```bash
mvnw.cmd test -Dtest=LoveAppTest#testChatWithNewChatId
```

盯第三轮的 `content:` 日志——这次回答里**基本不会**出现"perper"（它多半会反问"方便告诉我您和另一半的名字吗"）：换了 chatId 就是换了笔记本，A 本里的悄悄话它根本没看过。

**挑战题（留给你的）**：让第三轮**沿用 chatIdA** 再问一遍同样的问题——对照实验③，一个字之差，结果天壤之别。

### 🕳️ 小心踩坑

- **忘写 `@Component`** → 测试里 `@Resource` 注入 LoveApp 直接报 `NoSuchBeanDefinitionException`：Spring 根本不知道这个类的存在；
- **chatId 每次都随机生成** → 每句话都开新笔记本，永远记不住。测试里三轮必须用**同一个** chatId；
- **没建 application-local.yml 就跑测试** → `@SpringBootTest` 真的启动应用，钥匙是占位符/文件不存在，测试直接红；
- **RETRIEVE_SIZE 设 1 的"失忆"现象**：只带最近 1 条历史，隔一句的话可能想不起来——不是 bug，是带的历史太少；调大即可（仓库后来用 10）；
- ChatClient **没有公开构造器，不能 `new`**，只能 `ChatClient.builder(chatModel)...build()`。

### 📦 附录：阶段 3 完整代码（照抄可跑）

目录结构（在阶段 2 基础上新增一个包 + 第一个测试类）：

```
src/main/java/com/hui/huiaiagent/app/
└── LoveApp.java              # 恋爱大师：ChatClient + 身份卡 + 记忆
src/test/java/com/hui/huiaiagent/app/
└── LoveAppTest.java          # 项目第一个测试类（src/test 目录首次登场）
```

**1）pom.xml**：新加的 2 个测试依赖见"第 1 步"的 XML（Ollama starter 阶段 2 已引入，无需重复；想玩加分招，把 victools 那块也加上）。

**2）LoveApp.java**（完整，含加分招）：

```java
package com.hui.huiaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
                "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
                "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
                "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel){
        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        //初始化 chatClient
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public String doChat(String message, String chatId) {

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("content: {}",content);

        return content;
    }

    //结构化输出实体类
    record LoveReport(String title, List<String> suggestions) {
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }
}
```

**3）LoveAppTest.java**（完整）：

```java
package com.hui.huiaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是程序员小der";
        String answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        message = "我想让另一半（perper）更爱我";
        answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
    }

    // 加分招的测试方法（结构化输出）：把 AI 填好的"表格"打印出来看
    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        LoveApp.LoveReport report = loveApp.doChatWithReport("你好，我是程序员小der，我想让另一半更爱我", chatId);
        Assertions.assertNotNull(report);
        System.out.println("报告标题：" + report.title());
        report.suggestions().forEach(s -> System.out.println("建议：" + s));
    }

    // 挑战题实测（手册补充，仓库没有）：换 chatId = 换笔记本，验证"从头认识"
    @Test
    void testChatWithNewChatId() {
        String chatIdA = UUID.randomUUID().toString();
        loveApp.doChat("你好，我是程序员小der", chatIdA);
        loveApp.doChat("我想让另一半（perper）更爱我", chatIdA);

        // 换一本全新的笔记本再问
        String chatIdB = UUID.randomUUID().toString();
        String answer = loveApp.doChat("我的另一半叫什么来着？刚跟你说过，帮我回忆一下", chatIdB);
        Assertions.assertNotNull(answer);
    }
}
```

看到第三轮回答里的"perper"，阶段 3 毕业！

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 5259d50        # ★ 阶段 3 终点：恋爱大师 + 第一个测试类就位
mvnw.cmd test -Dtest=LoveAppTest#testChat   # 在原版代码上跑"三句话验证记忆"
git checkout main           # 切回最新
```

---

## 阶段 4：自定义 Advisor——学校门口的检查老师（提交 `513c3e4`、`bcab20f`）

### 🎯 打个比方

Advisor（顾问/拦截器）就像**学校门口的老师**，每个进校门（发给大模型）的请求都要过他们这一关。老师有三种风格：

- **观察型**：站在门口只做记录（日志）——`MyLoggerAdvisor`；
- **增强型**：帮你把问题抄一遍再读一遍，让你答得更好（Re-Read）——`ReReadingAdvisor`；
- **拦截型**：检查不合格直接请出去（权限、违禁词）——`AuthorizedAdvisor`、`ForbiddenWordsAdvisor`。

真实项目的 AI 网关 = 这三种老师的组合。本阶段两次提交正好分成两批：`513c3e4` 交付观察型 + 增强型，`bcab20f` 交付两个拦截型。

### 🧾 第 0 步：前置准备

不用办任何新门卡，钥匙、`application-local.yml` 都还是阶段 2/3 的。这阶段是**纯写代码**的一章——4 个 Advisor 全部只用 Spring AI 自带的接口，框架零新增。

### 📦 第 1 步：加依赖（pom.xml）

先说结论：**4 个 Advisor 本身零新依赖**（`CallAroundAdvisor` 等接口都在阶段 2 的 starter 里）。但提交 `513c3e4` 顺手买了两件"建材"：

| 依赖（版本） | 给谁用 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `com.github.victools:jsonschema-generator`（4.38.0） | 结构化输出 `.entity()`（阶段 3 加分招若已加过，跳过） | [victools.github.io](https://victools.github.io/jsonschema-generator/) | [mvnrepository.com/…/jsonschema-generator](https://mvnrepository.com/artifact/com.github.victools/jsonschema-generator) |
| `com.esotericsoftware:kryo`（5.6.2） | **阶段 5** 文件对话记忆的"压缩打包机"，本阶段先囤着 | [github.com/EsotericSoftware/kryo](https://github.com/EsotericSoftware/kryo) | [mvnrepository.com/…/kryo](https://mvnrepository.com/artifact/com.esotericsoftware/kryo) |

```xml
<!--	结构化数据输出的json转化-->
<dependency>
    <groupId>com.github.victools</groupId>
    <artifactId>jsonschema-generator</artifactId>
    <version>4.38.0</version>
</dependency>
<!--	kryo 实现文本序列化	-->
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>5.6.2</version>
</dependency>
```

**📖 顺手搞懂：Kryo 到底是什么？**（表里那个"压缩打包机"展开讲）

**Kryo 是一个高性能的 Java 序列化框架**（EsotericSoftware 出品）。想理解它，先理解**序列化**这个词：

- Java 对象住在**内存**里，程序一关就烟消云散。想把它**存到硬盘**（或**发到网络**对面），必须先变成一串字节——**对象 → 字节**叫**序列化**（压扁），**字节 → 对象**叫**反序列化**（还原）。
- **Java 不是自带序列化吗？** 带（`ObjectOutputStream`），但产出的字节**又大又慢**（附带一堆类信息），还有著名的**反序列化安全漏洞**前科，社区早就不待见它。Kryo 就是来接班的：二进制编码、通常比自带的小几倍、快几十倍。
- **和 JSON 什么区别？** JSON 是**文本**，人能看懂、跨语言通用（Java 写的 Python 能读），但体积大；Kryo 是**二进制**，打开全是乱码、基本只能 Java 自己存自己读，但**又小又快**。一句话分工：**对外交换用 JSON，自己家里存取用 Kryo**。
- **在本项目里干什么？** 阶段 5 的 `FileBasedChatMemory` 用它把整本对话记忆（`List<Message>`）**压扁**成一个 `chatId.kryo` 二进制文件，下次启动再**还原**成原来的 List——"真空压缩袋"：存的时候抽真空变小块，取的时候一拆恢复原样。（pom 里那句注释写的是"文本序列化"，更准确的说法是**对象序列化**。）
- **为什么现在就加？** 因为作者的提交 `513c3e4` 顺手把它带进了 pom，实际要到阶段 5 才用——你自己在阶段 5 再加也完全没问题。

### ⚙️ 第 2 步：写配置（application.yml）

`513c3e4` 加了 4 行"学习辅助"配置（可加可不加，不影响功能）：

```yaml
# 定义日志的打印级别
logging:
  level:
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: debug
```

作用：把 Spring AI **自带的**简易日志顾问调成 debug 级别——启动后控制台能看到官方顾问怎么干活，正好和你自己写的 `MyLoggerAdvisor` 对照着学。

### 💻 实战代码⓪：先背"自定义 Advisor 的万能骨架"

写任何 Advisor 都是这一个套路（背下来，四个文件全是它的变体）：

```java
public class XxxAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    // 同步调用（.call()）走这条门卫流程
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        // ① 前置：在这里"看/改/查"请求（观察、增强、拦截都写在这）
        // ② 放行：调用 chain.nextAroundCall(req)，请求才会继续传给下一位老师 → 最终到大模型
        //    ⚠️ 不调用它 = 请求凭空消失（不是拦截！）；拦截的正确姿势是抛异常
        // ③ 后置：拿到回信后想做的事（比如记日志）
    }

    // 流式调用（.stream()，阶段 9 登场）走这条，套路相同
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest req, StreamAroundAdvisorChain chain) { ... }

    public int getOrder() { return 0; }              // 数字越小越先执行
    public String getName() { return getClass().getSimpleName(); }
}
```

拆解三个关键点：

1. **两个接口都要实现**：`CallAroundAdvisor` 管同步、`StreamAroundAdvisor` 管流式。只实现前者的话，阶段 9 改用 `.stream()` 时你的顾问会悄悄失效；
2. `chain.nextAroundCall(req)` = **放行**。多个 Advisor 就这样手拉手排成一条链（责任链模式）；
3. `getOrder()` 的数字 = **排队序号，越小越靠门口**。

### 💻 实战代码①：观察型——MyLoggerAdvisor

`advisor/MyLoggerAdvisor.java` 核心（去掉了流式分支）：

```java
// 用户的信到我这儿了
private AdvisedRequest before(AdvisedRequest request) {
    log.info("AI Request: {}", request.userText());   // 抄下用户说了啥
    return request;
}

// 大模型的回信经过我这儿
private void observeAfter(AdvisedResponse resp) {
    log.info("AI Response: {}", resp.response().getResult().getOutput().getText());
}

// 串起来的"门卫流程"
public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
    req = this.before(req);                    // ① 先看一眼请求
    AdvisedResponse resp = chain.nextAroundCall(req);  // ② 放行！交给下一位老师
    this.observeAfter(resp);                   // ③ 回信也看一眼
    return resp;
}
```

流式分支里有个新面孔 `MessageAggregator`：流式回答是一段一段到的，先**攒齐了**再交给 `observeAfter`——"等快递全部到货再一起签收"。

### 💻 实战代码②：拦截型——ForbiddenWordsAdvisor + AuthorizedAdvisor

`ForbiddenWordsAdvisor.java` 核心：

```java
// 违禁词清单（演示用，真实项目可以从数据库读）
private static final Set<String> FORBIDDEN_WORDS = Set.of("暴力", "色情", "赌博");

private AdvisedRequest check(AdvisedRequest advisedRequest) {
    String userText = advisedRequest.userText();
    // 用户的话里，只要撞上任何一个违禁词……
    boolean hasForbiddenWord = FORBIDDEN_WORDS.stream().anyMatch(userText::contains);
    // ……就直接抛异常！请求根本到不了大模型（也就不花这一笔钱）
    Assert.isTrue(!hasForbiddenWord, "发送的内容中包含违规词，请重新发送！");
    return advisedRequest;
}
```

拆解：注意它**没有调 `chain.next(...)` 就抛了异常**——这叫"短路"：检查不过，门都不让进。

`AuthorizedAdvisor`（权限老师）多一个本领：**从"传参口袋"里认人**。调用方在 `.advisors(spec -> spec.param(AUTH_USER_ID_PARAM, "用户id"))` 里塞的身份，会进入 `adviseContext`，老师从里面掏出来对黑名单：

```java
// 从 adviseContext 中取出调用方传来的用户身份
Object userId = advisedRequest.adviseContext().get(AUTH_USER_ID_PARAM);
Assert.notNull(userId, "无权调用 AI：未获取到用户身份，请先登录");   // 没传 = 未登录，拒
Assert.isTrue(!bannedUserIds.contains(String.valueOf(userId)), "无权调用 AI：该用户已被封禁");  // 黑名单，拒
```

还有个彩蛋知识点——`getOrder()` 返回的数字**越小越先执行**。源码注释里写了为什么拦截型要抢在最前面：官方记忆 Advisor 默认排 `Integer.MIN_VALUE + 1000`，权限（`MIN_VALUE + 100`）和违禁词（`MIN_VALUE + 200`）必须排在它**前面**——*不然没权限的人说的话会先被记进笔记本*。Advisor 的顺序是有讲究的！

### 💻 实战代码③：增强型——ReReadingAdvisor（Re2，"再读一遍"）

**它干的事一句话**：把用户的问题在提示词里**重复两遍**，让模型"读题读两遍"——就像老师总说"题目读三遍再动笔"，对数学、逻辑类难题能明显提升正确率（这个技巧叫 Re-Read / Re2）。

**改造前后对比**（假设你问"3.11 和 3.9 谁大？"）：

```text
改造前发给模型的：          改造后发给模型的：
3.11 和 3.9 谁大？          3.11 和 3.9 谁大？
                            Read the question again: 3.11 和 3.9 谁大？
```

**核心方法 `before()` 逐行拆解**（它的任务：把"读一遍的请求"改造成"读两遍的请求"）：

```java
private AdvisedRequest before(AdvisedRequest advisedRequest) {

    // ① 复制一份原有的"用户参数表"。为什么不直接改原表？
    //    请求对象在这条 Advisor 链上是共享的，原地修改会污染后面老师看到的数据
    Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());

    // ② 把用户的原话存进参数表，变量名叫 re2_input_query（第 ③ 步的模板要引用它）
    advisedUserParams.put("re2_input_query", advisedRequest.userText());

    // ③ 以原请求为"底稿"，改造出一份新请求
    return AdvisedRequest.from(advisedRequest)
            // 把用户消息替换成"两遍模板"：
            // {re2_input_query} 是占位符——真正发送前，框架会用参数表里的值把它替换掉，
            // 两处占位符填的是同一句话，于是问题被"读了两遍"
            .userText("""
                    {re2_input_query}
                    Read the question again: {re2_input_query}
                    """)
            // ④ 挂上第 ①② 步准备的参数表（re2_input_query 的值就在里面）
            .userParams(advisedUserParams)
            // ⑤ 造出"加料版"新请求返回——原请求没动，链上下一位收到的已是读两遍的版本
            .build();
}
```

**两个放行方法**（和观察型/拦截型的骨架一模一样，只是多了"先改造、再放行"）：

```java
// 同步调用 .call() 走这条：先 before 改造，再放行给下一位老师
public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
    return chain.nextAroundCall(this.before(advisedRequest));
}
// 流式调用 .stream() 走这条：同样的改造、同样的放行（两个接口都要实现，见踩坑）
public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
    return chain.nextAroundStream(this.before(advisedRequest));
}
```

**为什么"再读一遍"会变聪明？** 直观理解：大模型是靠"注意力"读文字的，问题只出现一次时，关键信息容易被后面的内容冲淡；把原问题**在靠近回答的位置再贴一遍**，模型的注意力会重新聚焦到题目本身——数学比较、多步推理这类题的正确率肉眼可见地提升。代价只是 token 略增，所以 LoveApp 构造器里把它注释着、难题场景再解开。

### 💻 实战代码④：把四位老师装进 LoveApp

两处改动（对照阶段 3 你写好的 LoveApp）：

改动一，构造器的 `defaultAdvisors` 从 1 位老师变成 4 位：

```java
chatClient = ChatClient.builder(dashscopeChatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(
                // 权限拦截器
                new AuthorizedAdvisor(Set.of("badGuy")),   // 可传入封禁名单，演示可先写 new AuthorizedAdvisor()
                // 违规词拦截器
                new ForbiddenWordsAdvisor(),
                //记忆拦截器
                new MessageChatMemoryAdvisor(chatMemory),
                // 自定义日志 Advisor，可按需开启
                new MyLoggerAdvisor()
                // 自定义推理增强 Advisor，可按需开启
                // new ReReadingAdvisor()
        )
        .build();
```

改动二，`doChat` 的 advisors 参数里**多报一个身份**（不然权限老师谁都不放行）：

```java
.advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)          // 本阶段还顺手从 1 调到了 10
        .param(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "derder"))  // 告诉权限老师"我是谁"
```

拆解这段"装配代码"，有三个门道：

**门道一：书写顺序 ≠ 执行顺序，真正排队靠 `getOrder()`。** 四位老师在 `defaultAdvisors(...)` 里怎么写只影响阅读习惯，实际谁先检查消息，看的是各自 `getOrder()` 返回的数字（越小越靠门口）：

| 排队顺序 | 老师 | getOrder() | 为什么排这位 |
| --- | --- | --- | --- |
| ① | 权限 AuthorizedAdvisor | `Integer.MIN_VALUE + 100` | 没身份的人，一个字都不该进来 |
| ② | 违禁词 ForbiddenWordsAdvisor | `Integer.MIN_VALUE + 200` | 违规消息必须在被记下**之前**拦住 |
| ③ | 记忆 MessageChatMemoryAdvisor（官方） | `Integer.MIN_VALUE + 1000` | 合法消息才配进笔记本 |
| ④ | 日志 MyLoggerAdvisor | `0` | 排最后，记下的是"已经过审"的最终请求 |

（巧的是书写顺序正好和执行顺序一致——作者按执行顺序写是好习惯，但**别误以为换了书写顺序执行顺序就会变**。）

**门道二：一条消息的完整旅程**（对照上图把整条链走一遍）：

```
你说的话 + 身份"derder"
  → ①权限老师：对黑名单（不是 badGuy，放行）
  → ②违禁词老师：没撞上"暴力/色情/赌博"（放行）
  → ③记忆管理员：翻开 chatId 那本笔记，把最近 10 条历史念出来附在消息里
  → ④日志老师：抄下最终发出的请求（AI Request: ...）
  → 大模型笔回信
  → 回信原路返回：日志老师抄回信（AI Response: ...）→ 记忆管理员把"你一句、它一句"写进笔记
```

在权限/违禁词老师那儿被拦的消息，**根本走不到③④**——不念历史、不记笔记、更不调大模型，一分钱不花。

**门道三：改动二的参数是怎么流到老师手里的。** `.param(键, 值)` 把东西塞进一个**传参口袋**（adviseContext），随请求一路传递——权限老师掏 `AUTH_USER_ID_PARAM` 认人，记忆管理员掏 `CHAT_MEMORY_CONVERSATION_ID_KEY` 领笔记本编号。一个口袋大家各取所需，互不打扰；谁没收到自己要的参数，谁就按"没配"处理（权限老师直接拒）。

至于 `ReReadingAdvisor` 为什么注释着：它的"再读一遍"会把问题**重复一遍发送**（token 翻倍），日常聊天没必要常开，难题场景再解开。

### 🧪 动手试试

```bash
mvnw.cmd test -Dtest=LoveAppTest#testChat
```

1. **看观察型**：控制台出现 `AI Request: ...` / `AI Response: ...`——你自己的日志老师上岗了；
2. **试拦截型**：把 `doChat` 里的 `"derder"` 改成 `"badGuy"` 再跑——被权限老师拦下，异常信息就是 `Assert` 里写的这句话；再把某轮消息改成含"暴力"的话，换违禁词老师拦你；
3. **试增强型**：把构造器里 `new ReReadingAdvisor()` 的注释解开、注释掉 `MyLoggerAdvisor`，重跑看效果；
4. **不花一分钱单测两位拦截老师**（手册补充的 `AdvisorTest`，已加进仓库）：它**不起 Spring、不调大模型**，拿一条"假链子"直接验证两件事——该拦的（违禁词 / 未登录 / 黑名单用户）抛异常**且链子零调用**（请求根本没往后走）；该放的正常请求链子**恰好被调一次**：

   ```bash
   mvnw.cmd test -Dtest=AdvisorTest     # 5 个用例全绿，0.05 秒跑完，不消耗任何 token
   ```

**挑战题**：往 `FORBIDDEN_WORDS` 里加一个"内卷"，再让测试消息里带上它——先猜会不会被拦、再跑验证（用 AdvisorTest 改一版更省：把 userText 换成"内卷"试试）。

### 🕳️ 小心踩坑

- **"拦截"的正确姿势是抛异常**：不调 `chain.next(...)` 但也不抛异常，请求会像进了黑洞一样没有回音——那是 bug 不是拦截；
- **order 排错**：拦截型若排在记忆 Advisor 之后，违规消息会**先被写进笔记本**再被拦，脏数据就留下了；
- **只实现 `CallAroundAdvisor`**：同步没事，但阶段 9 改 `.stream()` 后顾问悄悄失效——骨架里两个接口都要实现；
- `Assert` 抛出的是 `IllegalArgumentException`：在测试里表现为"测试红"，在接口里（阶段 9）表现为 500——不是框架坏了。

### 📦 附录：阶段 4 完整代码（照抄可跑）

目录结构（在阶段 3 基础上新增 `advisor` 包）：

```
src/main/java/com/hui/huiaiagent/advisor/
├── MyLoggerAdvisor.java       # 观察型：日志
├── ReReadingAdvisor.java      # 增强型：Re-Read
├── AuthorizedAdvisor.java     # 拦截型：权限
└── ForbiddenWordsAdvisor.java # 拦截型：违禁词
src/test/java/com/hui/huiaiagent/advisor/
└── AdvisorTest.java           # 两位拦截老师的单元测试（手册补充，不花钱）
```

**1）pom.xml / application.yml**：见上文"第 1 步""第 2 步"的代码块。

**2）MyLoggerAdvisor.java**（完整）：

```java
package com.hui.huiaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * 自定义日志 Advisor
 * 打印 info 级别日志、只输出单次用户提示词和 AI 回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    //前置执行
    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI Request: {}", request.userText());
        return request;
    }

    //后置执行
    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("AI Response: {}", advisedResponse.response().getResult().getOutput().getText());
    }

    // 实现callaroundadvisor方法
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }
}
```

**3）ReReadingAdvisor.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.advisor;


import org.springframework.ai.chat.client.advisor.api.*;   // Advisor 相关接口（AdvisedRequest、两条链等）
import reactor.core.publisher.Flux;                        // 流式响应的"传送带"（StreamAroundAdvisor 需要）

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor（Re-Reading，"再读一遍"）
 * 原理：把用户的问题在提示词里重复两遍，让模型"读题读两遍"，
 * 在数学、逻辑类复杂问题上能明显提升正确率（Re-Read 提示技巧）
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 前置处理：改造请求——把"读一遍的问题"变成"读两遍的问题"
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        // ① 复制一份原有的"用户参数表"。为什么不直接改原表？
        //    请求对象在这条 Advisor 链上是共享的，原地修改会污染后面老师看到的数据
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());

        // ② 把用户的原话存进参数表，变量名叫 re2_input_query（第 ③ 步的模板要引用它）
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        // ③ 以原请求为"底稿"，改造出一份新请求
        return AdvisedRequest.from(advisedRequest)
                // 把用户消息替换成"两遍模板"：
                // {re2_input_query} 是占位符——真正发送前，框架会用参数表里的值把它替换掉，
                // 两处占位符填的是同一句话，于是问题被"读了两遍"
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                // ④ 挂上第 ①② 步准备的参数表（re2_input_query 的值就在里面）
                .userParams(advisedUserParams)
                // ⑤ 造出"加料版"新请求返回——原请求没动，链上下一位收到的已是读两遍的版本
                .build();
    }

    // 同步调用（.call()）的门卫流程：先 before 改造，再放行给下一位老师
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    // 流式调用（.stream()）的门卫流程：同样的改造、同样的放行（两个接口都要实现，见踩坑）
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    // 排队序号：数字越小越先执行。本顾问不拦人也不依赖别人，排 0（较靠后）即可
    @Override
    public int getOrder() {
        return 0;
    }

    // 名字：取类的简单名，方便在日志里认出是它
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
```

**4）AuthorizedAdvisor.java**（完整）：

```java
package com.hui.huiaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 自定义权限校验 Advisor
 * 在请求到达大模型之前校验调用者身份，无权限则直接抛出异常拦截，本次不会调用 AI
 *
 * 使用方式：调用时通过 Advisor 参数传入用户身份，例如
 * .advisors(spec -> spec.param(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "用户id"))
 * 参数会进入 adviseContext，本 Advisor 从中读取并校验
 */
public class AuthorizedAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 用户身份参数名（调用方传参时使用的 key）
     */
    public static final String AUTH_USER_ID_PARAM = "authUserId";

    /**
     * 封禁用户黑名单（演示用；实际项目可改为查询数据库，或对接真实的登录 / 权限系统）
     */
    private final Set<String> bannedUserIds;

    public AuthorizedAdvisor() {
        this(Set.of());
    }

    public AuthorizedAdvisor(Set<String> bannedUserIds) {
        this.bannedUserIds = bannedUserIds;
    }

    /**
     * 前置校验：不通过直接抛异常，请求不会继续沿着 Advisor 链传给大模型
     */
    private AdvisedRequest check(AdvisedRequest advisedRequest) {
        // 从 adviseContext 中取出调用方传来的用户身份
        Object userId = advisedRequest.adviseContext().get(AUTH_USER_ID_PARAM);
        // 1. 没传用户身份，视为未登录，直接拒绝
        Assert.notNull(userId, "无权调用 AI：未获取到用户身份，请先登录");
        // 2. 用户在封禁黑名单中，直接拒绝
        Assert.isTrue(!bannedUserIds.contains(String.valueOf(userId)), "无权调用 AI：该用户已被封禁");
        return advisedRequest;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 先校验，校验通过才放行到下一个 Advisor（最终到达大模型）
        return chain.nextAroundCall(this.check(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.check(advisedRequest));
    }

    @Override
    public int getOrder() {
        // order 越小越先执行。官方记忆 Advisor 默认是 Integer.MIN_VALUE + 1000（几乎最先执行），
        // 权限校验必须排在记忆之前（否则未授权的消息会先被存进聊天记忆），所以取更小的值
        return Integer.MIN_VALUE + 100;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
```

**5）ForbiddenWordsAdvisor.java**（完整）：

```java
package com.hui.huiaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 自定义违规词校验 Advisor
 * 在请求到达大模型之前检查用户输入，包含违规词则直接抛出异常拦截，本次不会调用 AI
 */
public class ForbiddenWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 违规词列表（演示用；实际项目可改为从数据库读取，或接入内容安全审核接口）
     */
    private static final Set<String> FORBIDDEN_WORDS = Set.of("暴力", "色情", "赌博");

    /**
     * 前置校验：不通过直接抛异常，请求不会继续沿着 Advisor 链传给大模型
     */
    private AdvisedRequest check(AdvisedRequest advisedRequest) {
        String userText = advisedRequest.userText();
        Assert.hasText(userText, "user text cannot be null");
        // 只要命中任意一个违规词，就拦截
        boolean hasForbiddenWord = FORBIDDEN_WORDS.stream().anyMatch(userText::contains);
        Assert.isTrue(!hasForbiddenWord, "发送的内容中包含违规词，请重新发送！");
        return advisedRequest;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 先校验，校验通过才放行到下一个 Advisor（最终到达大模型）
        return chain.nextAroundCall(this.check(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.check(advisedRequest));
    }

    @Override
    public int getOrder() {
        // order 越小越先执行。排在权限校验（Integer.MIN_VALUE + 100）之后、
        // 官方记忆 Advisor（Integer.MIN_VALUE + 1000）之前，保证违规消息不会被先存进聊天记忆
        return Integer.MIN_VALUE + 200;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
```

**6）LoveApp 的改动**：构造器和 `doChat` 照"实战代码④"改即可（完整 LoveApp 见阶段 3 附录，把 `defaultAdvisors` 和 advisors 参数两处替换）。

**7）AdvisorTest.java**（完整，手册补充、已加进仓库并实测 5/5 通过；位置 `src/test/java/com/hui/huiaiagent/advisor/`）：

```java
package com.hui.huiaiagent.advisor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 拦截型 Advisor 的单元测试（手册补充，仓库新增）：
 * 不启动 Spring、不调用大模型——直接拿一条"假链子"验证两件事：
 * ① 该拦的请求：抛出 IllegalArgumentException，且链子（后续环节）根本没被调用；
 * ② 该放的请求：链子被调用一次（放行），原样传给下一位。
 */
class AdvisorTest {

    /**
     * 假电话（假 ChatModel）：AdvisedRequest 构造时要求 chatModel 非空，
     * 但拦截器链根本不会真调用它——所以给一个"接了也不接"的空实现即可
     */
    private static final ChatModel FAKE_MODEL = prompt -> null;

    // ================= 违禁词拦截器 =================

    @Test
    void testForbiddenWordsAdvisor_block() {
        ForbiddenWordsAdvisor advisor = new ForbiddenWordsAdvisor();
        // 携带违禁词的请求
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("教我一些暴力的东西")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        // 假链子：一旦被调用就记 1 次
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;   // 拦截器只透传结果，这里用不到返回值
        };
        // 期望：抛异常（被拦下），且链子一次都没被调（请求没到"记忆/模型"那一步）
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("违规词"));
        assertEquals(0, chainCalls.get(), "被拦截的请求不应继续沿链传递");
    }

    @Test
    void testForbiddenWordsAdvisor_pass() {
        ForbiddenWordsAdvisor advisor = new ForbiddenWordsAdvisor();
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("怎么和心仪的人开启话题？")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        advisor.aroundCall(request, chain);
        assertEquals(1, chainCalls.get(), "正常请求应该放行（链子被调用一次）");
    }

    // ================= 权限校验拦截器 =================

    @Test
    void testAuthorizedAdvisor_noIdentity() {
        AuthorizedAdvisor advisor = new AuthorizedAdvisor();
        // 没带身份参数（没登录）
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of())
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("无权调用"));
        assertEquals(0, chainCalls.get());
    }

    @Test
    void testAuthorizedAdvisor_bannedUser() {
        // 构造器传入封禁名单：badGuy 在册
        AuthorizedAdvisor advisor = new AuthorizedAdvisor(java.util.Set.of("badGuy"));
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "badGuy"))
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> advisor.aroundCall(request, chain));
        assertTrue(ex.getMessage().contains("封禁"));
        assertEquals(0, chainCalls.get());
    }

    @Test
    void testAuthorizedAdvisor_normalUser() {
        AuthorizedAdvisor advisor = new AuthorizedAdvisor(java.util.Set.of("badGuy"));
        AdvisedRequest request = AdvisedRequest.builder()
                .userText("你好")
                .chatModel(FAKE_MODEL)
                .adviseContext(Map.of(AuthorizedAdvisor.AUTH_USER_ID_PARAM, "derder"))
                .build();
        AtomicInteger chainCalls = new AtomicInteger();
        CallAroundAdvisorChain chain = req -> {
            chainCalls.incrementAndGet();
            return null;
        };
        advisor.aroundCall(request, chain);
        assertEquals(1, chainCalls.get(), "正常用户应该放行");
    }
}
```

拆解这个测试类的三个小技巧（都是单元测试常用套路）：**① 假链子**——`CallAroundAdvisorChain` 是单方法接口，一个 lambda 就能充当"链子的下一位"，配 `AtomicInteger` 计数，既能断言"放行了"（次数=1）也能断言"真拦住了"（次数=0）；**② 假电话**——`AdvisedRequest` 构造时强制要求 chatModel 非空，但拦截器根本不会真调它，`prompt -> null` 一个空实现即可过关；**③ assertThrows**——把"预期抛异常"变成断言，异常消息还能再用 `contains` 验一遍（确认拦人的是"这位老师"、说的是"那句话"）。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout bcab20f        # ★ 阶段 4 终点：四位门卫老师全部上岗
# 中间对照：git checkout 513c3e4   ← 只有日志老师 + Re2 老师（还有 victools/kryo 依赖）
# 在旧代码上跑：mvnw.cmd test -Dtest=LoveAppTest#testChat，再把 doChat 的 "derder" 改成 "badGuy" 看拦截
git checkout main           # 切回最新
```

---

## 阶段 5：对话记忆——AI 的三个笔记本（提交 `1913ab5`、`b92588a`）

### 🎯 打个比方

大模型是**金鱼记忆**：挂了电话就忘了你说过什么。想让他记住，只能**每次通话前把之前的聊天记录念给他听**。那记录存在哪？项目里准备了三个笔记本：

| 笔记本 | 存哪 | 特点 | 何时登场 |
| --- | --- | --- | --- |
| `InMemoryChatMemory` | 内存（脑子里） | 最快，但程序一关全忘光 | 阶段 3（官方自带） |
| `FileBasedChatMemory` | 文件（一个会话一个 `.kryo` 文件） | 重启不忘，用 Kryo 把对象"压扁"存盘 | 本阶段 `1913ab5` 手写 |
| `MysqlChatMemory` | MySQL 数据库（档案柜） | 最正规，一行一条记录，谁都能查 | 本阶段 `b92588a` 手写 |

三个本子实现**同一个接口** `ChatMemory`（都有"存、取、清空"三个动作），随时换——这就是**策略模式**：换本子不改笔。

### 🧾 第 0 步：前置准备

按你要玩的笔记本二选一（或都玩）：

- **文件本（`1913ab5`）**：零准备，直接写代码；
- **数据库本（`b92588a`）**：
  1. 本机装好 MySQL（[mysql.com](https://www.mysql.com/)，5.7 / 8.x 都行）；
  2. 执行项目里的建表脚本（本阶段新增 `sql/chat_memory.sql`）：`mysql -u root -p < sql/chat_memory.sql`；
  3. 准备好数据库账号密码（马上写进 application-local.yml）。

### 📦 第 1 步：加依赖（pom.xml）

`1913ab5` 零新依赖（Kryo 已在阶段 4 提前囤好）；`b92588a` 加 2 个：

| 依赖（版本） | 作用 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `org.springframework.boot:spring-boot-starter-jdbc` | 提供 `JdbcTemplate`（简化 JDBC 操作）+ HikariCP 连接池 | [spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) | [mvnrepository.com/…/spring-boot-starter-jdbc](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-jdbc) |
| `com.mysql:mysql-connector-j`（8.0.33） | MySQL 官方驱动（8.0.33 兼容本机 MySQL 5.7） | [dev.mysql.com/doc/connector-j](https://dev.mysql.com/doc/connector-j/en/) | [mvnrepository.com/…/mysql-connector-j](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j) |
| `com.esotericsoftware:kryo`（5.6.2，阶段 4 已加） | 文件本的"压缩打包机"：把 Java 对象压成二进制存盘 | [github.com/EsotericSoftware/kryo](https://github.com/EsotericSoftware/kryo) | [mvnrepository.com/…/kryo](https://mvnrepository.com/artifact/com.esotericsoftware/kryo) |

```xml
<!-- MySQL 对话记忆：JDBC 访问 + MySQL 驱动（8.0.33 兼容本机 MySQL 5.7） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

### ⚙️ 第 2 步：写配置

**application.yml 不动**；给 `application-local.yml` 追加数据库连接（密码继续走"不进 git"的本地文件）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/hui_ai_agent?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

`.gitignore` 顺手加两行（文件本的存储目录不进 git）：

```text
chat-memory
tmp
```

配好后的自动装配链条：`jdbc starter` + `datasource` 配置 → Spring 自动造好 `JdbcTemplate` → `MysqlChatMemory` 构造器收下它——全程不用你 new 任何东西。

### 💡 先懂一个概念：记忆的"三个动作"（ChatMemory 接口）

三个本子实现官方接口 `ChatMemory`，一共就三个动作：

```java
public interface ChatMemory {
    void add(String conversationId, List<Message> messages);  // 记住：把新消息写进某本笔记
    List<Message> get(String conversationId, int lastN);      // 回忆：取某本笔记最近 N 条
    void clear(String conversationId);                        // 忘记：清空某本笔记
}
```

记忆管理员（`MessageChatMemoryAdvisor`，阶段 3 就挂上了）只认这个接口：**打电话前调 `get` 念历史，挂电话后调 `add` 记新话**。所以换本子 = 换一个 `new` 的实现类，别的代码一个字不用改。下面两个实操，就是亲手写出两个新本子——**照着步骤一步一步做，每步都有"做什么、为什么"**。

### 🔧 实操一：手写文件本 FileBasedChatMemory（提交 `1913ab5`）——照着做 5 步

**目标**：一个会话（chatId）一本 `xxx.kryo` 二进制文件，全存在 `BASE_DIR` 目录下；程序重启文件还在，记忆**跨重启**。

**第 1 步：建包建类，领"任务清单"**。新建包 `chatmemory`，新建类 `FileBasedChatMemory implements ChatMemory`。这个类的**作用**：把记忆从"内存"搬到"硬盘文件"。此时 IDE 会标红——三个动作（add/get/clear）还没实现，**这正是第 4 步要填的空**。

**第 2 步：写"地基"——根目录 + 打包机 + 构造器**（每行注释都写清"为什么"）：

```java
private final String BASE_DIR;                 // 所有记忆文件的根目录（构造时传入）
private static final Kryo kryo = new Kryo();   // 全局共用一台"打包机"（static：造一次，所有实例共用）

static {
    // 不要求预先登记要序列化的类，省事（否则每个类都得 kryo.register(...)）
    kryo.setRegistrationRequired(false);
    // 设置实例化策略：
    // 反序列化时绕过构造器直接造对象——避免"目标类没有无参构造器就还原失败"的问题
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
}

// 构造器：开柜子——指定目录，没有就建
public FileBasedChatMemory(String dir) {
    this.BASE_DIR = dir;              // 记下根目录，所有笔记都放这
    File baseDir = new File(dir);
    if (!baseDir.exists()) {
        baseDir.mkdirs();             // 目录不存在就一次性建好（含多级目录）
    }
}
```

**第 3 步：写三个"小工"私有方法**（建议从最底层的工具③往上写，最顺）：

```java
// 工具③（最底层）：会话编号 → 具体文件。一个 chatId 一本笔记
private File getConversationFile(String conversationId) {
    return new File(BASE_DIR, conversationId + ".kryo");
}

// 工具①：拿整本笔记；文件不存在就当"新笔记本"返回空列表
private List<Message> getOrCreateConversation(String conversationId) {
    File file = getConversationFile(conversationId);
    List<Message> messages = new ArrayList<>();
    if (file.exists()) {
        // 还原：二进制 → List<Message> 对象
        try (Input input = new Input(new FileInputStream(file))) {
            messages = kryo.readObject(input, ArrayList.class);
        } catch (IOException e) { e.printStackTrace(); }
    }
    return messages;
}

// 工具②：把整本笔记压扁（对象 → 二进制）写进文件
private void saveConversation(String conversationId, List<Message> messages) {
    File file = getConversationFile(conversationId);
    // try-with-resources：花括号结束自动关流，不用手写 close()
    try (Output output = new Output(new FileOutputStream(file))) {
        kryo.writeObject(output, messages);
    } catch (IOException e) { e.printStackTrace(); }
}
```

**第 4 步：填满接口的三个动作**（第 1 步领的任务，现在交卷——每条消息怎么进出笔记本）：

```java
// 记住 = 读旧文件 → 追加新消息 → 整本重写
@Override
public void add(String conversationId, List<Message> messages) {
    List<Message> conversationMessages = getOrCreateConversation(conversationId); // ① 读出现有内容
    conversationMessages.addAll(messages);                                        // ② 追加新消息
    saveConversation(conversationId, conversationMessages);                       // ③ 整本写回文件
}

// 回忆 = 读整本，只取最近 N 条
@Override
public List<Message> get(String conversationId, int lastN) {
    List<Message> allMessages = getOrCreateConversation(conversationId);  // 读整本
    return allMessages.stream()
            // 跳过前面的（总数 - N）条；总条数不足 N 时跳 0 条（= 全要）
            .skip(Math.max(0, allMessages.size() - lastN))
            .toList();                                                    // 剩下的就是最近 N 条
}

// 忘记 = 删文件（整本撕掉）
@Override
public void clear(String conversationId) {
    File file = getConversationFile(conversationId);
    if (file.exists()) {
        file.delete();
    }
}
```

**第 5 步：换上文件本 + 验证**。LoveApp 构造器里把文件本那两行的注释解开：

```java
String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
```

验证两件事：① `mvnw.cmd test -Dtest=LoveAppTest#testChat` 三轮全过；② 打开 `tmp/chat-memory/` 目录——多出一个 UUID 命名的 `.kryo` 文件（这就是你们的对话）；**重启程序、同一 chatId 再问一句**——它还认得 perper，文件本的意义正在于此。

拆解：`Message` 对象不能直接写进文本文件，Kryo 就是那台**打包机**——把对象压成一串二进制（比 JSON 更小更快），读的时候再原样还原。`.kryo` 文件是二进制，别用文本编辑器打开看（全是乱码）。（"序列化是什么、Kryo 和 Java 自带序列化/JSON 的分工"详见阶段 4 的"📖 顺手搞懂"知识块。）

### 🔧 实操二：手写数据库本 MysqlChatMemory（提交 `b92588a`）——照着做 8 步

**目标**：把文件本的"一个会话一个文件"升级成"**一条消息一行记录**"——能多人共享、能用 SQL 随时查、跨重启。第 1 步就是最容易懵的"建表"，先把它讲透。

**第 1 步：建库建表（新建 `sql/chat_memory.sql`）**。

为什么要建这张表？数据库本和文件本**存的东西一样**（都是消息），但**存法**变了：不再"整本读写"，而是**每聊一句插一行**。所以表的字段就按"一句话需要哪些信息"来设计——

```sql
CREATE TABLE IF NOT EXISTS chat_memory (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键，天然记录消息的先后顺序',
    conversation_id VARCHAR(64)     NOT NULL COMMENT '会话 id（对应代码里的 chatId）',
    role            VARCHAR(20)     NOT NULL COMMENT '谁说的：USER=用户 / ASSISTANT=AI / SYSTEM=系统设定',
    content         TEXT            NOT NULL COMMENT '消息原文',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (id),
    KEY idx_conversation (conversation_id, id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI 对话记忆表';
```

每个字段为什么存在：

| 字段 | 存的是什么 | 为什么需要它 |
| --- | --- | --- |
| `id`（BIGINT 自增） | 行号 | **天然记录"谁先谁后"**（id 越小越早）——第 5 步"取最近 N 条"全靠它，不用自己维护顺序 |
| `conversation_id` | 哪本笔记（chatId） | **隔离不同会话**：A 的悄悄话不能念给 B 听；UUID 36 位，VARCHAR(64) 正好放得下 |
| `role` | 谁说的 | USER / ASSISTANT / SYSTEM——第 6 步把一行还原成 Message 对象时要靠它认人 |
| `content`（TEXT） | 消息原文 | 说话的内容本体；TEXT 类型能存长文本 |
| `create_time` | 入库时间 | 给**人**看的（什么时候聊的）；程序排序用的是 id，不靠它 |

最后一行 `KEY idx_conversation (conversation_id, id)` 是**联合索引**：让"按会话取最近 N 条"不用全表扫——数据量大了也不慢。建表就一条命令：

```bash
mysql -u root -p < sql/chat_memory.sql
```

**第 2 步：确认依赖和数据源**（本阶段"第 1 步、第 2 步"已做好，这里只核对三样）：pom 里的 jdbc starter、mysql-connector-j；application-local.yml 里的 `spring.datasource`。配好后的自动装配链条：**依赖 + 配置 → Spring 自动造好 `JdbcTemplate` 零件**——第 3 步的类全靠它干活。

**第 3 步：新建 MysqlChatMemory 类**。这个类的**作用**：和文件本实现**同一个 `ChatMemory` 接口**（三动作不变），只把"整本读/整本写"换成"一行插 / 倒序查"。**为什么要标 `@Component`**：它干活需要 `JdbcTemplate`（Spring 的零件），标了 @Component，Spring 启动就造好它、把 JdbcTemplate 从构造器递进来——你永远不用 new：

```java
@Component
public class MysqlChatMemory implements ChatMemory {

    private final JdbcTemplate jdbcTemplate;

    // 由 Spring 注入 JdbcTemplate（连接信息来自 application-local.yml 的 spring.datasource）
    public MysqlChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
```

**第 4 步：写 add 方法（记住 = 插行）**。方法的**作用**：每聊一句往表里插一行——注意 SQL 里的三个 `?` 和三个参数一一对应，正好是这张表的三个核心字段：

```java
@Override
public void add(String conversationId, List<Message> messages) {
    for (Message message : messages) {              // 一条消息一行，逐条插
        String content = message.getText();
        // 工具调用消息没有纯文本形态，本项目对话用不到，直接跳过
        if (content == null || content.isEmpty()) {
            continue;
        }
        jdbcTemplate.update(
                "INSERT INTO chat_memory (conversation_id, role, content) VALUES (?, ?, ?)",
                conversationId,                       // 第 1 个 ?：哪本笔记（chatId）
                message.getMessageType().name(),      // 第 2 个 ?：谁说的（USER/ASSISTANT）
                content);                             // 第 3 个 ?：说了什么
    }
}
```

**第 5 步：写 get 方法（回忆 = 取最近 N 条）**。方法的**作用**：打电话前把这本笔记最近的 N 条念出来。**两步曲缺一不可**：

```java
@Override
public List<Message> get(String conversationId, int lastN) {
    List<Message> recentMessages = jdbcTemplate.query(
            "SELECT role, content FROM chat_memory WHERE conversation_id = ? ORDER BY id DESC LIMIT ?",
            //                                                        ↑ 倒着取：最新的排在前面  ↑ 只拿 N 条
            (rs, rowNum) -> toMessage(rs.getString("role"), rs.getString("content")),  // 每行现场翻译成 Message
            conversationId, lastN);

    Collections.reverse(recentMessages);   // ✅ 再翻转回来：从旧到新，AI 读着才顺
}
```

为什么"倒着取再翻转"？`LIMIT N` 只会拿"前 N 条"，而聊天要的是**最后** N 条——正序没法直接跳到结尾。自增 id 天然记录先后（越小越早），所以先 `ORDER BY id DESC` 把**最新的 N 条**拿到手（此时新在前），再 `reverse` 翻回"旧在前"的正常聊天顺序。

**第 6 步：写 toMessage 辅助方法（翻译官）**。方法的**作用**：数据库行里存的是两个字符串（role、content），而 Spring AI 要的是 Message 对象——谁说的就还原成谁：

```java
private Message toMessage(String role, String content) {
    return switch (role) {
        case "USER" -> new UserMessage(content);           // 用户说的
        case "ASSISTANT" -> new AssistantMessage(content);  // AI 说的
        case "SYSTEM" -> new SystemMessage(content);        // 系统设定
        default -> new UserMessage(content);   // 兜底（理论走不到）
    };
}
```

**第 7 步：写 clear 方法（忘记 = 删整本）**。方法的**作用**：把这本笔记的全部行删掉：

```java
@Override
public void clear(String conversationId) {
    jdbcTemplate.update("DELETE FROM chat_memory WHERE conversation_id = ?", conversationId);
}
```

**第 8 步：LoveApp 换上数据库本 + 三重验证**。构造器**多收一个 `MysqlChatMemory` 参数**（Spring 自动注入，不用你传）：

```java
public LoveApp(ChatModel dashscopeChatModel, MysqlChatMemory mysqlChatMemory) {
//        初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        // 初始化基于文件的对话记忆
       // String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //  ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        //初始化 chatClient
        // 初始化基于数据库的对话记忆
        ChatMemory chatMemory = mysqlChatMemory;
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new AuthorizedAdvisor(Set.of("badGuy")),
                        new ForbiddenWordsAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory),   // 本子在这里换
                        new MyLoggerAdvisor()
                )
                .build();
}
```

三重验证：① `mvnw.cmd test -Dtest=LoveAppTest#testChat` 三轮全过；② 查库——`SELECT * FROM chat_memory ORDER BY id DESC LIMIT 10;`，你们的对话一行一行躺在表里；③ **重启程序**、同一 chatId 再问"我的另一半叫什么"——照样答得出（数据库跨进程，这就是它相对内存本的杀手锏）。

### 🔧 实操三：一行切换笔记本（策略模式的回报）

三个本子实现了**同一个接口**，于是在 LoveApp 里"换本子"就是换个注释——管理员的代码一行不改，这就是阶段 3 埋下的"接口 + 策略模式"的回报：

```java
//  ChatMemory chatMemory = new InMemoryChatMemory();       // 脑子里：最快，重启忘光
    String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//  ChatMemory chatMemory = new FileBasedChatMemory(fileDir); // 文件本：重启不忘
    ChatMemory chatMemory = mysqlChatMemory;                 // 数据库本：最正规（当前启用）
```

### 🧪 动手试试

**实验①：跑老测试，看新笔记本上岗**（LoveApp 现在用的是 MysqlChatMemory，同一个 chatId 三轮对话）：

```bash
mvnw.cmd test -Dtest=LoveAppTest#testChat
```

**实验②：查库**——你们的对话一行一行躺在表里：

```bash
mysql -u root -p -e "SELECT * FROM hui_ai_agent.chat_memory ORDER BY id DESC LIMIT 10;"
```

**实验③（手册补充的测试代码，仓库没有）：不花一分钱，单测三个本子。** 妙处在：**直接测 `ChatMemory` 接口，不经过大模型**——既验证了本子，又不用花 token。新建 `src/test/java/com/hui/huiaiagent/chatmemory/ChatMemoryTest.java`（完整代码在附录第 5 条），核心是四个测试方法：

```java
/** 文件本：普通 new 就能用（连 Spring 都不依赖），直接验证存与取 */
@Test
void testFileChatMemory() {
    FileBasedChatMemory chatMemory =
            new FileBasedChatMemory(System.getProperty("user.dir") + "/tmp/chat-memory");
    String chatId = "file-memory-test";
    chatMemory.add(chatId, List.of(new UserMessage("我的另一半叫 perper")));
    List<Message> messages = chatMemory.get(chatId, 10);
    messages.forEach(m -> System.out.println("文件本回忆：" + m.getText()));
    Assertions.assertEquals(1, messages.size());
}

/** 数据库本：增、查、清空一套走完（add → get → clear → get） */
@Test
void testMysqlChatMemory() { ... 同款套路，换 mysqlChatMemory，最后多验一步 clear ... }
```

先跑这两个（互不依赖，可以一起跑）：

```bash
mvnw.cmd test -Dtest=ChatMemoryTest#testFileChatMemory+testMysqlChatMemory
```

预期输出：

```text
文件本回忆：我的另一半叫 perper
数据库本回忆：我的另一半叫 perper
clear 之后再看，一页都不剩——clear 生效
```

**实验④（杀手锏的代码版证明）：跨"重启"的记忆。** 一条 mvnw 命令就是一次全新的 JVM——把它当"一次开关机"用。测试类里的两段式方法（固定笔记本编号 `restart-test-notebook`）：

```java
/** 杀手锏·上半场：写入悄悄话（跑完这次 JVM 就"关机"了） */
@Test
void testRestartMemoryPhase1Write() {
    String chatId = "restart-test-notebook";   // 固定编号：两次运行共用
    mysqlChatMemory.add(chatId, List.of(new UserMessage("我的另一半叫 perper")));
    System.out.println("悄悄话已入库。接下来【另开一次】运行 Phase2Read——那就是一次'重启'");
}

/** 杀手锏·下半场：全新 JVM 里读回悄悄话 */
@Test
void testRestartMemoryPhase2Read() {
    String chatId = "restart-test-notebook";
    List<Message> messages = mysqlChatMemory.get(chatId, 10);
    messages.forEach(m -> System.out.println("重启后回忆：" + m.getText()));
    Assertions.assertTrue(
            messages.stream().anyMatch(m -> m.getText().contains("perper")),
            "重启后应该还记得 perper——记不得说明你跑的还是内存本");
}
```

**分两次、各跑一条**（上半场跑完 JVM 就结束了，下半场是全新的进程）：

```bash
mvnw.cmd test -Dtest=ChatMemoryTest#testRestartMemoryPhase1Write   # 第一次：写入
mvnw.cmd test -Dtest=ChatMemoryTest#testRestartMemoryPhase2Read    # 第二次：全新 JVM 读回来！
```

第二次的预期输出：

```text
重启后回忆：我的另一半叫 perper
```

这就是"**记忆存在数据库里，不随程序生死**"的代码级证明。把两段方法里的 `mysqlChatMemory` 换成 `new FileBasedChatMemory(...)`，文件本同样能过；换成 `new InMemoryChatMemory()` 则下半场**必挂**（新 JVM 里内存是空的）——三种本子的差距，一目了然。

**挑战题**：给 `ChatMemoryTest` 再加一个方法，验证"同一本笔记只取最近 N 条"：先 `add` 三条，再 `get(chatId, 2)`，断言只回来 2 条、而且是**最新的两条**（提示：文件本看 `skip`，数据库本看 `ORDER BY id DESC LIMIT`）。

### 🕳️ 小心踩坑

- **没执行 `sql/chat_memory.sql`** → 启动不报错，一聊天就报 `Table 'hui_ai_agent.chat_memory' doesn't exist`；
- **密码/账号错** → 启动直接红 `Access denied for user`；
- **换本子后"失忆"**：老对话存在旧本子里（内存/文件/数据库互不相通），换本子等于开新柜子，历史要自己搬；
- `conversation_id` 列是 `VARCHAR(64)`，UUID（36 位）正好放得下；
- `.kryo` 是二进制文件，看不了也别手改。

### 📦 附录：阶段 5 完整代码（照抄可跑）

目录结构（新增 `chatmemory` 包 + `sql` 目录）：

```
src/main/java/com/hui/huiaiagent/chatmemory/
├── FileBasedChatMemory.java   # 文件本（kryo 二进制）
└── MysqlChatMemory.java       # 数据库本
src/test/java/com/hui/huiaiagent/chatmemory/
└── ChatMemoryTest.java        # 本子的单元测试（手册补充，仓库没有）
sql/
└── chat_memory.sql            # 建库建表脚本
```

**1）FileBasedChatMemory.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;                       // Kryo：高性能序列化框架（对象 ↔ 二进制）
import com.esotericsoftware.kryo.io.Input;                   // Kryo 的"读取管子"：从文件读二进制
import com.esotericsoftware.kryo.io.Output;                  // Kryo 的"写出管子"：往文件写二进制
import org.objenesis.strategy.StdInstantiatorStrategy;       // 绕过构造器创建对象的策略（反序列化时需要）
import org.springframework.ai.chat.memory.ChatMemory;        // 记忆接口：add（记）/ get（忆）/ clear（忘）
import org.springframework.ai.chat.messages.Message;         // 一条对话消息（用户 / AI / 系统说的）

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 * 一个会话（chatId）对应一个 .kryo 二进制文件，全存在 BASE_DIR 目录下；
 * 程序重启后文件还在，所以记忆能跨重启
 */
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;                 // 所有记忆文件的根目录（构造时传入）
    private static final Kryo kryo = new Kryo();   // 全局共用一台"打包机"（static：造一次，所有实例共用）

    static {
        // 不要求预先登记要序列化的类，省事（否则每个类都得 kryo.register(...)）
        kryo.setRegistrationRequired(false);
        // 设置实例化策略：
        // 反序列化时绕过构造器直接造对象——避免"目标类没有无参构造器就还原失败"的问题
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;              // 记下根目录，所有笔记都放这
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();             // 目录不存在就一次性建好（含多级目录）
        }
    }

    // ============ 接口三动作之一：记住（把新消息追加进这本笔记） ============
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId); // ① 先读出这本笔记现有内容
        conversationMessages.addAll(messages);                                        // ② 把新消息追加上去
        saveConversation(conversationId, conversationMessages);                       // ③ 整本重新写回文件
    }

    // ============ 接口三动作之二：回忆（取这本笔记最近 lastN 条） ============
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> allMessages = getOrCreateConversation(conversationId);  // 读出整本笔记
        return allMessages.stream()
                // 跳过前面的（总数 - N）条；总条数不足 N 时跳 0 条（= 全要）
                .skip(Math.max(0, allMessages.size() - lastN))
                .toList();                                                    // 剩下的就是最近 N 条
    }

    // ============ 接口三动作之三：忘记（整本撕掉） ============
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();               // 删掉 .kryo 文件 = 清空这本笔记
        }
    }

    // 私有工具①：拿到这本笔记的全部消息；文件不存在就当"新笔记本"返回空列表
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            // 还原：二进制 → List<Message> 对象
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    // 私有工具②：把整本笔记压扁（对象 → 二进制）写进文件
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        // try-with-resources：花括号结束自动关流，不用手写 close()
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 私有工具③：会话编号 → 具体文件。一个 chatId 一本笔记：BASE_DIR/xxx.kryo
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
```

**2）MysqlChatMemory.java**（完整）：

```java
package com.hui.huiaiagent.chatmemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 基于 MySQL 持久化的对话记忆
 * 和 FileBasedChatMemory 实现同一个 ChatMemory 接口，只是把"一个会话一个文件"
 * 换成"一条消息一行记录"，可以随时在两种实现之间切换
 */
@Component
public class MysqlChatMemory implements ChatMemory {

    private final JdbcTemplate jdbcTemplate;

    // 由 Spring 注入 JdbcTemplate（连接信息来自 application-local.yml 的 spring.datasource）
    public MysqlChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 记住新消息：往表里插行，一行一条消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            String content = message.getText();
            // 工具调用消息没有纯文本形态，本项目对话用不到，直接跳过
            if (content == null || content.isEmpty()) {
                continue;
            }
            jdbcTemplate.update(
                    "INSERT INTO chat_memory (conversation_id, role, content) VALUES (?, ?, ?)",
                    conversationId, message.getMessageType().name(), content);
        }
    }

    /**
     * 回忆最近 lastN 条消息：按 id 倒序取最近 N 条，再反转成正常的时间顺序
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> recentMessages = jdbcTemplate.query(
                "SELECT role, content FROM chat_memory WHERE conversation_id = ? ORDER BY id DESC LIMIT ?",
                (rs, rowNum) -> toMessage(rs.getString("role"), rs.getString("content")),
                conversationId, lastN);
        // 倒序取的是"最新在前"，翻回"最旧在前"，AI 读起来才是正常聊天顺序
        Collections.reverse(recentMessages);
        return recentMessages;
    }

    /**
     * 忘记某个会话：删除该会话的全部记录
     */
    @Override
    public void clear(String conversationId) {
        jdbcTemplate.update("DELETE FROM chat_memory WHERE conversation_id = ?", conversationId);
    }

    /**
     * 把数据库里的 (role, content) 还原成 Spring AI 的 Message 对象
     */
    private Message toMessage(String role, String content) {
        return switch (role) {
            case "USER" -> new UserMessage(content);
            case "ASSISTANT" -> new AssistantMessage(content);
            case "SYSTEM" -> new SystemMessage(content);
            // 理论上不会走到这，兜底当成用户消息处理
            default -> new UserMessage(content);
        };
    }
}
```

**3）sql/chat_memory.sql**（完整）：

```sql
-- ============================================
-- AI 对话记忆表：每聊一句，就往这张表插一行
-- ============================================
CREATE DATABASE IF NOT EXISTS hui_ai_agent DEFAULT CHARSET utf8mb4;

USE hui_ai_agent;

CREATE TABLE IF NOT EXISTS chat_memory (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键，天然记录消息的先后顺序',
    conversation_id VARCHAR(64)     NOT NULL COMMENT '会话 id（对应代码里的 chatId）',
    role            VARCHAR(20)     NOT NULL COMMENT '谁说的：USER=用户 / ASSISTANT=AI / SYSTEM=系统设定',
    content         TEXT            NOT NULL COMMENT '消息原文',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (id),
    KEY idx_conversation (conversation_id, id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI 对话记忆表';
```

**4）ChatMemoryTest.java**（完整，手册补充、仓库没有——不调大模型、不花钱）：

```java
package com.hui.huiaiagent.chatmemory;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 阶段 5 手册补充的测试（仓库没有）：
 * 直接测 ChatMemory 接口，不经过大模型——不花一分钱验证三个笔记本的"存/取/跨重启"
 */
@SpringBootTest
class ChatMemoryTest {

    @Resource
    private MysqlChatMemory mysqlChatMemory;

    /** 文件本：普通 new 就能用（连 Spring 都不依赖），直接验证存与取 */
    @Test
    void testFileChatMemory() {
        FileBasedChatMemory chatMemory =
                new FileBasedChatMemory(System.getProperty("user.dir") + "/tmp/chat-memory");
        String chatId = "file-memory-test";
        chatMemory.add(chatId, List.of(new UserMessage("我的另一半叫 perper")));
        List<Message> messages = chatMemory.get(chatId, 10);
        messages.forEach(m -> System.out.println("文件本回忆：" + m.getText()));
        Assertions.assertEquals(1, messages.size());
    }

    /** 数据库本：增、查、清空一套走完 */
    @Test
    void testMysqlChatMemory() {
        String chatId = "mysql-memory-test";
        mysqlChatMemory.add(chatId, List.of(new UserMessage("我的另一半叫 perper")));
        List<Message> messages = mysqlChatMemory.get(chatId, 10);
        messages.forEach(m -> System.out.println("数据库本回忆：" + m.getText()));
        Assertions.assertEquals(1, messages.size());
        // 清空这本笔记（clear 也是接口三动作之一）
        mysqlChatMemory.clear(chatId);
        Assertions.assertEquals(0, mysqlChatMemory.get(chatId, 10).size());
        System.out.println("clear 之后再看，一页都不剩——clear 生效");
    }

    /** 杀手锏·上半场：写入悄悄话（跑完这次 JVM 就"关机"了） */
    @Test
    void testRestartMemoryPhase1Write() {
        String chatId = "restart-test-notebook";   // 固定编号：两次运行共用
        mysqlChatMemory.add(chatId, List.of(new UserMessage("我的另一半叫 perper")));
        System.out.println("悄悄话已入库。接下来【另开一次】运行 Phase2Read——那就是一次'重启'");
    }

    /** 杀手锏·下半场：全新 JVM 里读回悄悄话 */
    @Test
    void testRestartMemoryPhase2Read() {
        String chatId = "restart-test-notebook";
        List<Message> messages = mysqlChatMemory.get(chatId, 10);
        messages.forEach(m -> System.out.println("重启后回忆：" + m.getText()));
        Assertions.assertTrue(
                messages.stream().anyMatch(m -> m.getText().contains("perper")),
                "重启后应该还记得 perper——记不得说明你跑的还是内存本");
    }
}
```

（注意：四个方法**别在一次运行里全跑**——Phase2Read 依赖 Phase1Write 先执行过；按"动手试试"里的分条命令跑。）

**5）LoveApp 构造器改动 + application-local.yml 增量**：见上文"实操二第 8 步"和"第 2 步（写配置）"。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout b92588a        # ★ 阶段 5 终点：三个记忆本就位（数据库本为默认）
# 中间对照：git checkout 1913ab5   ← 只有文件本（kryo）版本
# 在旧代码上跑：先执行 sql/chat_memory.sql，再 mvnw.cmd test -Dtest=LoveAppTest#testChat，然后查库看记录
git checkout main           # 切回最新
```

---

## 阶段 6：RAG——让 AI 参加开卷考试（提交 `3c8d159` → `77a34fc`，共 12 个）

### 🎯 打个比方

闭卷考试，AI 只能靠脑子里原有知识（有时会一本正经地胡说）。**RAG = 开卷考试**，分三步：

```
① 考前（程序启动时）：把课本拆成一张张"知识卡片"，存进卡片柜（向量库）
② 考试中（用户提问时）：拿你的问题去柜子里找最像的几张卡片
③ 答题：把卡片内容抄在题目旁边，让 AI 照着答 —— 不会的题也不瞎编
```

这个阶段提交最多（12 个），每一步都是把这场开卷考试办得更好。先给一张**提交地图**（迷路时回来对坐标）：

| 提交 | 干了什么 | 里程碑 |
| --- | --- | --- |
| `3c8d159` | 本地知识库：读 md → 向量化 → 内存柜 → 开卷考试 | ⭐ 主线跑通 |
| `4fba4e9` | 云端知识库（百炼上的"恋爱大师"索引） | 支线① |
| `e8f05e5` | 集成 pgvector（生产级卡片柜） | 支线② |
| `79c72e1` | pgVectorStore 的测试方法 | 支线② |
| `83b1e41` | token 切分器（控制卡片大小） | 进阶 |
| `7b46e09` | 给卡片贴 status 元数据 | 进阶 |
| `7a1c1f8` | 自动补充关键词元信息 | 进阶 |
| `0df2e54` | 补注释 | - |
| `733add7` | 查询扩展器（一个问题变三个） | 进阶 |
| `9d3ebbe` | 查询重写器 + 中文模板 | 进阶 |
| `3c485a8` | 检索配置（过滤 + 阈值 + topK） | 进阶 |
| `77a34fc` | 上下文增强器（空结果兜底话术） | 进阶 |

### 🧾 第 0 步：前置准备

1. **主线（本地知识库）零新准备**：把仓库 `src/main/resources/document/` 下的 3 个 Markdown 知识文档放进你的项目（也可换成你自己的，**文件名前两个字符会被当作 status 标签**，见实战代码①）：

   ```text
   单身状态常见恋爱问题与回答.md
   已婚状态常见恋爱问题与回答.md
   恋爱状态常见恋爱问题与回答.md
   ```

2. **支线①（云知识库）可选**：去[百炼控制台](https://bailian.console.aliyun.com/)建一个数据索引（名字就叫"恋爱大师"），导入这 3 份文档——不建就用不了 `doChatWithRag2`；
3. **支线②（pgvector）可选**：需要一个 PostgreSQL（[postgresql.org](https://www.postgresql.org/)，本地或阿里云 RDS 都行）——不玩可以整个跳过。

### 📦 第 1 步：加依赖（pom.xml）

| 依赖（版本） | 干什么用 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `org.springframework.ai:spring-ai-markdown-document-reader`（1.0.0-M6） | 读 Markdown、拆知识卡片（**主线必需**，`3c8d159` 加） | [Spring AI 文档](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html) | [mvnrepository.com/…/markdown-document-reader](https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-markdown-document-reader) |
| `org.springframework.ai:spring-ai-pgvector-store`（1.0.0-M6） | pgvector 向量库接入（支线②，`e8f05e5` 加） | [Spring AI 文档](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html) · [pgvector](https://github.com/pgvector/pgvector) | [mvnrepository.com/…/pgvector-store](https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-pgvector-store) |
| `org.postgresql:postgresql`（runtime） | PostgreSQL 驱动（支线②） | [postgresql.org](https://www.postgresql.org/) | [mvnrepository.com/…/postgresql](https://mvnrepository.com/artifact/org.postgresql/postgresql) |

```xml
<!--	RAG 的 markdown 文档读取依赖	-->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-markdown-document-reader</artifactId>
    <version>1.0.0-M6</version>
</dependency>
<!--	pgvector：M6 版 store 模块（与项目其余 Spring AI M6 依赖保持同版本；
    自动配置已在启动类 exclude，VectorStore 由 PgVectorVectorStoreConfig 手动定义）	-->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store</artifactId>
    <version>1.0.0-M6</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

注意：**只玩主线的话，第一个依赖就够了**；后两个是 pgvector 支线的门票。

### ⚙️ 第 2 步：写配置

**主线零配置！** 卡片柜存内存（`SimpleVectorStore`），"翻译官" `dashscopeEmbeddingModel` 也是阶段 2 的 starter 自动装配出来的（用的还是那把 dashscope 钥匙）。

支线②需要两处改动（pgvector 玩家才做）：

```yaml
spring:
  ai:
    #   向量数据库的配置
    vectorstore:
      pgvector:
        index-type: HNSW  # 向量索引算法
        dimensions: 1536  # 1536 维的向量
        distance-type: COSINE_DISTANCE
        max-document-batch-size: 10000
```

以及启动类加一个 exclude（M6 的 pgvector 自动配置和手动定义的 Bean 会打架，关掉自动的、用下面手写的）：

```java
@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class HuiAiAgentApplication { ... }
```

### 💡 先懂三个概念（10 分钟，看完再动手）

**概念一：为什么需要 RAG——AI 会"一本正经地胡说"**

大模型只学过训练数据里的东西，而且**训练完就定型了**：

- 你问它项目私有知识（比如本项目的恋爱文档），它没见过，只能**编**——编得还挺像真的，这叫**幻觉**；
- 想让它学新知识，只能重新训练模型，贵到离谱。

RAG（检索增强生成）的解法朴素得像学生考试：**允许开卷**。答题前先把相关资料翻出来、抄在题目旁边，AI 照着资料回答——没翻到就老实说不会。一句话原理：**大模型负责"读资料 + 组织语言"，资料由你的程序负责递过去**。后面所有代码，干的都是"递资料"这一件事。

**概念二：先认识"知识卡片"——Document 对象**

整份文档直接塞给 AI 不行吗？不行，两个原因：**太长**（上下文窗口有限、大部分内容和当前问题无关，白花钱）；**检索不准**（混成一大段，"以意思找意思"时干扰项太多）。所以第一步永远是：**把大文档切成一张张小卡片**。

Spring AI 里一张卡片就是一个 `Document` 对象，结构特别简单：

```java
Document {
    String text;                     // 卡片正文（一段内容）
    Map<String, Object> metadata;    // 角落的标签（如 status=已婚, filename=xxx.md）
}
```

`metadata`（元数据）就是**贴在卡片角落的小标签**：正文留给"以意思找"用，标签留给"按条件筛"用——实操三"只翻已婚抽屉"用的就是它。记住一句话：**做卡片 = 切正文 + 贴标签**。

### 🔧 实操一：搭起主线 RAG（提交 `3c8d159`）——照着做 5 步

**目标**：让恋爱大师能引用 `resources/document/` 里的私有知识答题（把闭卷考试变成开卷）。

**第 1 步：放知识文档**（第 0 步已拷好，这里说清"为什么"）：三个 md = 单身/恋爱/已婚各一本"教材"；**文件名前两个字符 = status 标签**，第 2 步要从文件名里抠它——所以起名必须守约定。

**第 2 步：新建"做卡片工人" `rag/LoveAppDocumentLoader.java`**。类的**作用**：读文件 → 切卡片 → 贴标签；核心方法 **`loadMarkdowns()`** 的**作用**：把 document 目录下所有 md 变成 `List<Document>`。方法体分三段看：

**第 1 段：读哪些文件**

```java
// resourcePatternResolver：Spring 的"文件探测器"（构造器注入进来的）
// classpath:document/*.md 是通配地址："resources/document 目录下所有 md 文件"
Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");

for (Resource resource : resources) {          // 逐个文件处理
```

`classpath:document/*.md` 是个通配地址——"resources/document 目录下所有 md"。以后想加知识，**扔个文件进这个目录就行，代码一行不改**。

**第 2 段：从文件名抠标签**

```java
    String fileName = resource.getFilename();  // 拿到文件名，如"已婚状态常见恋爱问题与回答.md"
    String status = fileName.substring(0, 2);  // 截取前两个字符 → "已婚"，当标签用
```

作者的小聪明：文件名前两个字符正好是状态（单身/恋爱/已婚），一刀切出来当标签。代价是**起名要守规矩**——自己加文档时，前两个字必须是有效标签，否则标签是错的、抽屉就翻不到（见踩坑）。

**第 3 段：切 + 贴**

```java
    // 配置"切卡机"怎么切、贴什么标签
    MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)     // 遇到 "---" 分隔线就切一张新卡片
            .withIncludeCodeBlock(false)                // 代码块不单独成卡（本项目文档没有代码）
            .withIncludeBlockquote(false)               // 引用块也不单独成卡
            .withAdditionalMetadata("filename", fileName)  // 给每张卡片贴"文件名"标签（溯源用）
            .withAdditionalMetadata("status", status)   // 贴"状态"标签（实操三 过滤抽屉用）★
            .build();
    // 造出切卡机，喂给它"这份文件 + 切卡规则"
    MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
    allDocuments.addAll(reader.get());                  // get() 一调用就完成切卡，收集进总列表
}
```

三段合起来，`loadMarkdowns()` 的返回值就是**全部文件的全部卡片**（`List<Document>`）。

（两个**备用刀具**，仓库里有、默认注释不启用，知道存在即可：`MyTokenTextSplitter`——按 token 数硬切、每段 200，是文档没有 `---` 分隔线时的保底手段；`MyKeywordEnricher`——让 AI 给每张卡片再补 2 个关键词标签，多一路检索线索，但每张卡都要多花一次 AI 调用。）

**概念三：把文字变成数字——Embedding（本阶段最重要的原理，第 3 步马上要用）**

卡片做好后有个核心难题：**怎么在几千张卡片里找到"意思相关"的那几张？** 按关键词匹配太死板——用户问"家务分工闹矛盾"，卡片里写的是"夫妻责任分配冲突"，一个字都对不上，意思却是一回事。

解法是 **Embedding（向量化）**：用一个"翻译官"模型（`EmbeddingModel`）把每句话翻译成**一串数字**（本项目是 1536 个，叫 1536 维向量）。可以把它想成**给每句话标一个 GPS 坐标**，而且这个坐标系很神奇——**意思越近的话，坐标离得越近**：

```text
"家务分工闹矛盾"   → [0.12, -0.35, 0.88, ...]   ┐
"夫妻责任分配冲突" → [0.11, -0.33, 0.90, ...]   ┘ 两点距离很近 ✅
"今天天气不错"     → [-0.72, 0.44, -0.10, ...]  → 离上面很远 ❌
```

于是"找相关卡片"就变成了初中学的"两点比距离"：**把问题也变成坐标，看谁离得近**。这就是 RAG 能"以意思找意思"的全部秘密。（"翻译官" `dashscopeEmbeddingModel` 哪来的？和 ChatModel 一样，是阶段 2 的 starter 自动装配的。）

**第 3 步：新建"卡片柜" `rag/LoveAppVectorStoreConfig.java`**。类的**作用**：程序启动时开一个内存向量柜，把第 2 步做出的卡片全部翻译成坐标（概念三的"翻译官"）存进去：

```java
@Configuration                                  // 配置类：告诉 Spring"我这里有你想要的零件"
public class LoveAppVectorStoreConfig {

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;        // 备用刀具①：token 切分器（本主线未启用）

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;   // 第 2 步的"做卡片工人"

    @Resource
    private MyKeywordEnricher myKeywordEnricher;           // 备用刀具②：关键词增强器（本主线未启用）

    @Bean                                        // 造一个名叫 loveAppVectorStore 的零件，启动时执行一次
    // 参数 dashscopeEmbeddingModel：Spring 自动递进来的"翻译官"（阶段 2 starter 自动装配的）
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 开一个卡片柜（存在内存里），并指定入柜/查询时用哪位"翻译官"
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 让工人开工：读 markdown → 切卡片 → 贴标签，拿到全部卡片
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 全部入柜：每张卡片都会被"翻译官"转成 1536 维坐标存好（真调 Embedding 接口）
        simpleVectorStore.add(documents);
        // 备用方案（注释着，按需换用）：
        // List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);  // 先切更小
        // simpleVectorStore.add(splitDocuments);
        // List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents); // 再补关键词标签
        // simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;                          // 柜子交给 Spring，哪里要用哪里注入
    }
}
```

三个要点：

1. `@Bean` 方法在**程序启动时执行一次**——"考前把书搬进考场"；之后问答只管翻柜子，不再重复翻译（省钱）；
2. `SimpleVectorStore` = **内存柜**：最快，重启清空（反正下次启动会重跑一遍这个 @Bean，自动重新装填）；生产环境要"重启不丢、多人共享"就换实操四（选读）的 pgvector；
3. `store.add(documents)` 会**真的调用 Embedding 接口**（每张卡片翻译一次）——所以启动会稍慢，也才会踩到"一次最多 25 条"的限制（见踩坑）。

**第 4 步：LoveApp 挂上"开卷考试开关"**。加两个字段、一个方法，RAG 就通了：

```java
@Resource
private VectorStore loveAppVectorStore;      // 第 3 步造的卡片柜，Spring 自动注入进来

//    RAG 本地知识库
public String doChatWithRag(String message, String chatId) {
    ChatResponse chatResponse = chatClient          // 阶段 3 装好的"带秘书的电话"
            .prompt()                               // ① 拿出信纸
            .user(message)                          // ② 写上用户的问题
            .advisors(spec -> spec                  // ③ 给这通电话临时塞两个参数：
                    .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)  //    记忆管理员用 chatId 这本笔记
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))       //    念最近 10 条历史
            // ④ ✨ 开卷考试开关——挂上 RAG 顾问。它在幕后替你干 4 件事：
            //    问题变坐标 → 去柜子挑卡片 → 卡片抄进 prompt → 发给大模型
            .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
            .call()                                 // ⑤ 寄出（同步等待完整回信）
            .chatResponse();                        // ⑥ 拿到完整回信对象
    String content = chatResponse.getResult().getOutput().getText();  // ⑦ 从回信里抽出文字部分
    log.info("content: {}", content);               // ⑧ 打到控制台，方便肉眼验收
    return content;                                 // ⑨ 交给调用方
}
```

和普通 `doChat` 的区别就一行：多挂了一个 `QuestionAnswerAdvisor`。**它在幕后替你干 4 件事**（这是 RAG 的完整流水线，值得背下来）：

```text
你说"已婚家务怎么分"
 ① 把你的问题交给翻译官 → 变成坐标（Embedding）
 ② 拿坐标去卡片柜比距离 → 挑出最相关的几张卡片（检索）
 ③ 把卡片正文抄在题目旁边，拼成新 prompt（增强）：
    "参考以下资料：[卡片内容]……请回答：已婚家务怎么分"
 ④ 大模型照着资料作答（生成）
```

注意：RAG 在 Spring AI 里**本质就是一个 Advisor**——和阶段 4 的门卫老师们站同一条链，这位老师干的活是"递资料"。

**第 5 步：验证**：

```bash
mvnw.cmd test -Dtest=LoveAppTest#doChatWithRag
```

问"已婚家务分工"（只有文档里才有的内容），看回答带上了文档里的内容——主线通了，阶段 6 的"开卷考试"开考！

### 🔧 实操二：查询重写 QueryRewriter（提交 `9d3ebbe`）——照着做 6 步

**第 1 步：想清楚要解决什么问题**。实操一第 4 步的流水线里，"把问题变坐标"这一环有个隐患：用户的话往往很口语——

> "我老婆天天跟我吵家务咋整啊，顺便给我推个课呗"

拿这句话整体去算坐标，**闲聊和口水词会把坐标带偏**，翻出来的卡片就不准。理想做法是先整理成一个干净、独立的问题：

> "已婚夫妻家务分工矛盾如何解决"

"先把问题改好、再去检索"就叫**查询重写**（发生在检索之前，术语 pre-retrieval）。实现思路一句话：**用大模型来改写**——本质是两次模型调用：第一次小调用把口语改成规范问题，第二次才走实操一的"检索 + 答题"。

**第 2 步：新建 `rag/QueryRewriter.java`**。类的**作用**：检索前的"问题加工车间"——进一句口语、出一句规范问题；标 @Component 交给 Spring 管理，LoveApp 直接注入。类里只养一个"改写引擎"（`QueryTransformer`），放构造器里装配——启动时造一次，之后每次改写共用，不重复建设：

```java
@Component                                        // 交给 Spring 管理，哪里要用哪里注入
public class QueryRewriter {

    private final QueryTransformer queryTransformer;   // "改写引擎"：Spring AI 官方的查询变换器

    // 构造器注入 ChatModel（阶段 2 自动装配的电话）；引擎在这里装配一次，全局共用
    public QueryRewriter(ChatModel dashscopeChatModel) {
```

第 3 步——给引擎写中文"岗位说明书"（PromptTemplate）：

```java
        // 先备一个 ChatClient 的 builder（待会交给改写引擎用）
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 给引擎写"岗位说明书"（中文提示词模板）。
        // ⚠️ 注意：三引号里是真正要发给大模型的文字，里面不能写 // 注释！
        //    {query} {target} 是框架的填空位，由代码在运行时填入
        PromptTemplate chinesePromptTemplate = new PromptTemplate("""
                你是一个查询改写助手。请把下面的用户查询改写成更适合在{target}中检索的形式：
                去掉与检索无关的闲聊内容，把它整理成一个独立、完整的问题。
                必须使用中文输出，禁止翻译成英文。

                用户查询：{query}

                改写后的查询：
                """);
```

两个占位符是框架的"填空位"：`{query}` 由框架自动填用户原话；`{target}` 由下一行的 `targetSearchSystem("向量数据库")` 填入——告诉 AI "改写是为了去哪查"（去向量库查，就该去掉寒暄、突出关键词）。**为什么非换中文模板？**（作者踩过的坑）`RewriteQueryTransformer` 官方默认模板是英文的、又没要求"保持语言"——中文问题丢进去常被**翻译成英文**吐出来；可我们的卡片全是中文，拿英文问题去和中文卡片比坐标，方向全偏。**为什么占位符不能删？** builder 会校验模板必须含 `{query}`，删了启动直接报错。

第 4 步——装配引擎 + 写对外唯一入口 `doQueryRewrite`（方法的**作用**：进一句口语、出一句规范问题）：

```java
        // 装配改写引擎：谁改写 + 按什么规则改 + 改写是为了往哪查
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)              // 用哪个大模型来改写
                .promptTemplate(chinesePromptTemplate)   // 按哪份"岗位说明书"改
                .targetSearchSystem("向量数据库")         // 这个值会填进模板的 {target} 占位符
                .build();
    }

    // 对外只暴露这一个方法：进来一句口语，出去一句规范问题
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);                  // 把用户原话装进框架认识的"查询"对象
        Query transformedQuery = queryTransformer.transform(query);  // 真正执行改写（内部是一次大模型调用）
        return transformedQuery.text();                   // 从结果对象里取出改写后的文本
    }
}
```

`transform()` 内部就是"填模板 → 发给大模型 → 收回改写结果"，`.text()` 把结果取成字符串。

**第 5 步：串进 LoveApp**（新方法 `doChatWithRagQueryWriter`）——先改写，再走实操一的 RAG：

```java
public String doChatWithRagQueryWriter(String message, String chatId) {
    // ★ 第 1 次大模型调用：把口语改成规范问题
    //   （"我老婆天天跟我吵家务咋整啊…" → "已婚夫妻家务分工矛盾如何解决"）
    String rewrittenMessage = queryRewriter.doQueryRewrite(message);
    // 下面 = 实操一第 4 步的 doChatWithRag 原样再来一遍，唯一的变化是 user 换成了改写后的问题
    ChatResponse chatResponse = chatClient
            .prompt()                                     // 拿出信纸
            .user(rewrittenMessage)                       // ← 用"干净的问题"去检索，坐标不再被口水词带偏
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))  // ★ 第 2 次调用：检索 + 答题
            .call()
            .chatResponse();
    return chatResponse.getResult().getOutput().getText();
}
```

看清结构：**改写是前面单独的一步，后面的代码和实操一第 4 步一模一样**——这正是"检索前处理"的字面意思。

**第 6 步：验证**：

```bash
mvnw.cmd test -Dtest=LoveAppTest#doChatWithRagQueryWriter
```

盯控制台：先看到改写后的规范问题，再看到 RAG 的回答——和 `doChatWithRag` 的检索质量对比一下，口语问题越重、差距越明显。

（同族玩具 `demo/rag/MultiQueryExpanderDemo.java`（提交 `733add7`）：思路反着来——一个问题**扩写成 3 个**不同角度的问法，各查一遍再合并。和重写器同属"查询变换"家族，代码在附录。）

### 🔧 实操三：只翻指定抽屉 + 空结果兜底（提交 `3c485a8`、`77a34fc`）——照着做 6 步

**第 1 步：想清楚要解决什么问题**。柜子里三本档案（单身/恋爱/已婚）混放，已婚用户提问时很可能翻出"单身"卡片混进参考——答非所问。好在实操一第 2 步贴过 status 标签，现在让检索**只翻指定抽屉**。

**第 2 步：新建 `rag/LoveAppRagCustomAdvisorFactory.java`（工厂类）**。类的**作用**：给别人造"自选套餐"Advisor——检索器、增强器分开自己配（区别于全家桶 QuestionAnswerAdvisor）。核心方法 `createLoveAppRagCustomAdvisor(柜子, 状态)` 分三段：

第 3 步——**写过滤条件**（相当于 SQL 的 `WHERE status = '已婚'`——概念二说的"标签给按条件筛用"在这里闭环）：

```java
// 方法参数 status 由调用方传入："已婚" / "恋爱" / "单身"
Filter.Expression expression = new FilterExpressionBuilder()  // 条件构造器
        .eq("status", status)      // eq = 相等；相当于 SQL 的 WHERE metadata.status = '已婚'
        .build();                  // 造出一个"过滤条件"对象，待会交给检索器
```

第 4 步——**写检索器**：定义"怎么翻柜子"的完整规则：

```java
DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
        .vectorStore(vectorStore)          // 去哪个柜子翻（入参：第 3 步造的柜子）
        .filterExpression(expression)      // 只翻带这个标签的抽屉（第 2 步贴的标签闭环了）
        .similarityThreshold(0.5)          // 相似度门槛：低于 0.5 的卡片宁可不带（带错的更糟）
        .topK(3)                           // 最多带 3 张（带多了 prompt 冗长、费钱）
        .build();                          // 造出"检索器"：一套完整的翻柜子规则
```

`similarityThreshold(0.5)`：坐标距离换算成 0~1 的相似度，**低于 0.5 的卡片不带**——带一张不相关的进来比不带更糟（AI 会被带偏）；`topK(3)`：最多带几张，带多了 prompt 冗长费钱。

第 5 步——**组装成 Advisor**（queryAugmenter 那行先留个悬念，第 6 步造它）：

```java
return RetrievalAugmentationAdvisor.builder()     // "自选套餐"Advisor，零件自己配
        .documentRetriever(documentRetriever)     // 零件① 翻柜子规则（上面刚造的检索器）
        .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())  // 零件② 抄题规则（第 6 步的兜底增强器）
        .build();                                 // 组装完返回，调用方 .advisors(它) 即可启用
```

新面孔 `RetrievalAugmentationAdvisor` 和实操一第 4 步的 `QuestionAnswerAdvisor` 什么关系？——**`QuestionAnswerAdvisor` 是"全家桶"**（检索规则、拼 prompt 规则全帮你定死）；**`RetrievalAugmentationAdvisor` 是"自选套餐"**（检索器、增强器分开自己配）。要过滤、要定制，就得从全家桶换成自选套餐。

**第 6 步：新建 `rag/LoveAppContextualQueryAugmenterFactory.java` 造"空结果兜底"**。为什么需要：默认行为下，就算检索**一张卡片都没找到**，拼出来的 prompt 仍是"请回答：xxx"——没有参考资料，AI 就开始自由发挥（幻觉回来了）。这个工厂就做两件事：

```java
// "没翻到书"时改用的固定话术（同样：三引号内是要发给大模型的文字，不能写注释）
PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
        你应该输出下面的内容：
        抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
        有问题可以联系编程导航客服 https://codefather.cn
        """);

return ContextualQueryAugmenter.builder()
        .allowEmptyContext(false)                                // ① 关掉"没资料也硬答"的默认行为
        .emptyContextPromptTemplate(emptyContextPromptTemplate)  // ② 检索结果为空时，改用上面这套话术
        .build();                                                // 造出"增强器"，交给第 5 步的组装挂载
```

这个工厂造出来的增强器，正是第 5 步组装时挂进 `queryAugmenter(...)` 的那位——**开卷考试没翻到书，就老实说不会**。

### 🔧 实操四（选读）：两条生产级支线（提交 `4fba4e9`、`e8f05e5` 等）

主线用的内存柜够学习用，仓库里还有两条支线，**不玩可整体跳过**：

- **支线① 云知识库**（`LoveAppRagCloudAdvisorConfig.java`）：把"读文档 + 切卡片 + 入柜"整套杂活**外包给阿里云百炼**——在控制台建一个"恋爱大师"数据索引并导入文档，本地只留一个云端检索器：

  ```java
  DashScopeApi dashScopeApi = new DashScopeApi(dashScopeApiKey);
  DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
          DashScopeDocumentRetrieverOptions.builder()
                  .withIndexName("恋爱大师")     // 云端索引名
                  .build());
  return RetrievalAugmentationAdvisor.builder()
          .documentRetriever(documentRetriever)
          .build();                              // LoveApp 注入它 → doChatWithRag2
  ```

  省事、免运维，代价是知识要传到云端托管。

- **支线② pgvector**（`PgVectorVectorStoreConfig.java`，默认 `//@Configuration` 注释关闭）：把内存柜换成 **PostgreSQL + pgvector 插件**——真正的数据库柜，重启不丢、多人共享。两个注意点：`initializeSchema(true)` 自动建表；入库必须**分批**（灵积翻译官一次最多翻 25 张，代码里有 batchSize=10 的循环示例）。前提：PostgreSQL + 第 1 步那两个依赖 + 第 2 步的配置。

### 🧪 动手试试

```bash
mvnw.cmd test -Dtest=LoveAppTest#doChatWithRag              # 基础开卷考试
mvnw.cmd test -Dtest=LoveAppTest#doChatWithRagQueryWriter   # 对比：查询先被改写成了什么样
```

测试里问的就是知识文档里才有的问题（已婚家务分工），看 AI 的回答是不是带上了文档里的内容——**再故意问一个文档里没有的问题**（比如"怎么做红烧肉"），看兜底话术是否接管。

**挑战题**：往 `resources/document/` 加一份你自己写的 md（比如`恋爱状态约会指南.md`——前两字必须是已有标签），重启项目问它里面的内容。

### 🕳️ 小心踩坑

- 灵积的向量化接口**一次最多 25 张卡片**，入库要分批（`PgVectorVectorStoreConfig` 里有分批示例），否则报 `The input texts limit 25`；
- 切分卡片太长检索不准、太短意思破碎（`MyTokenTextSplitter` 用的默认 200 token 一段）；
- PgVector 版本（生产可用）默认**注释关闭**，需要 PostgreSQL 才打开；
- QueryRewriter 的中文模板里 `{query}`、`{target}` 两个占位符**删了启动就报错**（builder 会校验模板）；
- **status 标签来自文件名前两个字符**：自己加文档时文件名起错，标签就错，抽屉过滤就翻不到。

### 📦 附录：阶段 6 完整代码（照抄可跑）

目录结构（新增 `rag` 包 + `demo/rag` + 知识文档）：

```
src/main/java/com/hui/huiaiagent/rag/
├── LoveAppDocumentLoader.java              # ① 做卡片
├── MyTokenTextSplitter.java                # 切分器（备用）
├── MyKeywordEnricher.java                  # 关键词增强（备用）
├── LoveAppVectorStoreConfig.java           # ② 内存卡片柜
├── QueryRewriter.java                      # ④ 查询重写
├── LoveAppRagCustomAdvisorFactory.java     # ⑤ 自定义检索
├── LoveAppContextualQueryAugmenterFactory.java  # ⑤ 空结果兜底
├── LoveAppRagCloudAdvisorConfig.java       # 支线①：云知识库
└── PgVectorVectorStoreConfig.java          # 支线②：pgvector（默认注释）
src/main/java/com/hui/huiaiagent/demo/rag/
└── MultiQueryExpanderDemo.java             # 查询扩展玩具
src/main/resources/document/
└── 单身/已婚/恋爱状态常见恋爱问题与回答.md   # 知识文档（从仓库拷贝或自己写）
```

**1）pom.xml / application.yml / 启动类**：见"第 1 步""第 2 步"的代码块。

**2）LoveAppDocumentLoader.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;                        // 一张"知识卡片"（正文 + 标签）
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;   // Spring AI 的"切卡机"
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;  // 切卡规则
import org.springframework.core.io.Resource;                            // 一个文件的抽象
import org.springframework.core.io.support.ResourcePatternResolver;     // Spring 的"文件探测器"（按通配符找文件）
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//文档读取："读 md 文件 → 切知识卡片 → 贴标签"的做卡片工人
@Component
@Slf4j
class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    // 构造器注入"文件探测器"（Spring 自带的零件，能按通配符搜 classpath）
    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    //读取markdown 文件的代码：把 document 目录下所有 md 变成卡片列表 List<Document>
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();      // 收集全部卡片的总列表
        try {
            // 通配地址："resources/document 目录下所有 md 文件"——以后加知识扔文件进目录即可，代码不用改
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {             // 逐个文件处理
                String fileName = resource.getFilename();     // 如"已婚状态常见恋爱问题与回答.md"
                // 获取文件的状态名:单身，已婚，恋爱
                // 截文件名前两个字符当标签——所以文件起名必须守约定，前两字=有效状态
                String status = fileName.substring(0,2);
                // 配置"切卡机"：怎么切、贴什么标签
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)   // 遇到 "---" 分隔线就切一张新卡片
                        .withIncludeCodeBlock(false)              // 代码块不单独成卡（本项目文档没有代码）
                        .withIncludeBlockquote(false)             // 引用块不单独成卡
                        .withAdditionalMetadata("filename", fileName)  // 贴"文件名"标签（出问题时溯源用）
                        .withAdditionalMetadata("status",status)  // 贴"状态"标签（检索时只翻指定抽屉用）★
                        .build();
                // 造出切卡机，喂给它"这份文件 + 切卡规则"
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());            // get() 一调用就完成切卡，收集进总列表
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;                                  // 全部文件的全部卡片
    }
}
```

**3）MyTokenTextSplitter.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

// 备用刀具①：按 token 数硬切卡片——文档没有 "---" 分隔线时的保底切分手段
// （主线用的是 MarkdownDocumentReader 按分隔线切；这个类默认注释不启用）
@Component
class MyTokenTextSplitter {

    // 全默认参数切分：默认每段约 200 token
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    // 自定义参数切分（五个参数从左到右）：
    // 200 = 每段目标 token 数（太长检索不准、太短意思破碎，200 是常用平衡点）
    // 100 = 段内最少字符数（太短的碎块并进上一段）
    // 10  = 最短可嵌入长度（比这还短的段不值得送去算向量）
    // 5000 = 最多切多少段（防失控保险丝）
    // true = 保留切分处的分隔符（尽量不破坏语句）
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }
}
```

**4）MyKeywordEnricher.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

// 备用刀具②：让 AI 给每张卡片再补几个"关键词"标签——检索时多一路线索
// （代价：每张卡都要多花一次 AI 调用；默认注释不启用）
@Component
class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;   // 用哪个模型来提炼关键词（阶段 2 自动装配的电话）

    List<Document> enrichDocuments(List<Document> documents) {
        // 参数 2 = 每张卡片提炼 2 个关键词，写进卡片的 metadata（标签区）
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 2);
        return enricher.apply(documents);
    }
}
```

**5）LoveAppVectorStoreConfig.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

//文档向量化：开一个"卡片柜"（向量库），程序启动时把卡片全部翻译成坐标存进去

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库 Bean）
 * 启动时执行一次："考前把书搬进考场"；之后问答只管翻柜子，不再重复翻译（省钱）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;        // 备用刀具①：token 切分器（本主线未启用）

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;   // 做卡片工人：读 md → 切卡片 → 贴标签

    @Resource
    private MyKeywordEnricher myKeywordEnricher;           // 备用刀具②：关键词增强器（本主线未启用）

    @Bean
    // 参数 dashscopeEmbeddingModel：Spring 自动递进来的"翻译官"（阶段 2 starter 自动装配）
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 开一个卡片柜（存在内存里），并指定入柜/查询时用哪位"翻译官"
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 让工人开工：读 markdown → 切卡片 → 贴标签，拿到全部卡片
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 全部入柜：每张卡片都会被"翻译官"转成 1536 维坐标存好（真调 Embedding 接口）
        simpleVectorStore.add(documents);
        // 需要则开启注释：自主切分（文档没有 "---" 分隔线时换这条路线）
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
//        simpleVectorStore.add(splitDocuments);
        // 需要时开启注释：自动补充关键词元信息（每张卡多花一次 AI 调用，换一路检索线索）
      //  List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        //simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;                          // 柜子交给 Spring，哪里要用哪里注入
    }
}
```

**6）QueryRewriter.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器：检索前的"问题加工车间"——进一句口语，出一句规范问题
 * （例："我老婆天天跟我吵家务咋整啊" → "已婚夫妻家务分工矛盾如何解决"）
 * 本质是两次模型调用：先本类改写（小调用），再走正常 RAG 检索 + 答题
 */
@Component
public class QueryRewriter {

    // 改写引擎：Spring AI 官方的查询变换器（我们只负责换中文"岗位说明书"）
    private final QueryTransformer queryTransformer;

    // 构造器里装配一次，全局共用（启动时造好，之后每次改写不再重复建设）
    public QueryRewriter(ChatModel dashscopeChatModel) {
        // 先备一个 ChatClient 的 builder（待会交给改写引擎用）
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 默认提示词是英文的且没要求保持语言，模型会顺着英文输出——可我们的卡片是中文，
        // 拿英文问题对中文卡片比坐标方向全偏，所以换成中文提示词
        // 注意：模板里必须保留 {query} 和 {target} 两个占位符，否则 builder 校验会直接报错
        // （{query} 由框架运行时填用户原话；{target} 由下面的 targetSearchSystem 填入）
        PromptTemplate chinesePromptTemplate = new PromptTemplate("""
                你是一个查询改写助手。请把下面的用户查询改写成更适合在{target}中检索的形式：
                去掉与检索无关的闲聊内容，把它整理成一个独立、完整的问题。
                必须使用中文输出，禁止翻译成英文。

                用户查询：{query}

                改写后的查询：
                """);
        // 创建查询重写转换器：谁改写 + 按哪份说明书改 + 改写是为了往哪查
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)              // 用哪个大模型来改写
                .promptTemplate(chinesePromptTemplate)   // 按哪份"岗位说明书"改
                .targetSearchSystem("向量数据库")         // 这个值会填进 {target} 占位符
                .build();
    }

    // 对外唯一入口：进一句口语，出一句规范问题
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);                  // 把用户原话装进框架认识的"查询"对象
        // 执行查询重写（内部是一次大模型调用：填模板 → 发给模型 → 收回改写结果）
        Query transformedQuery = queryTransformer.transform(query);
        // 输出重写后的查询（从结果对象里取出文本）
        return transformedQuery.text();
    }
}
```

**7）LoveAppRagCustomAdvisorFactory.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义 RAG Advisor 的工厂：造"自选套餐"——检索器、增强器分开自己配
 * （区别于全家桶 QuestionAnswerAdvisor：它的检索规则、拼 prompt 规则都是定死的）
 */
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 造一个"只翻指定抽屉"的 RAG Advisor
     *
     * @param vectorStore 卡片柜
     * @param status      只翻哪个抽屉（单身 / 恋爱 / 已婚——入库时贴的标签）
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {

        // 第 3 步：过滤条件——相当于 SQL 的 WHERE metadata.status = '已婚'
        // （入库时贴的 status 标签，在这里闭环：只翻这一个抽屉，别的抽屉再像也不翻）
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        // 第 4 步：检索器——定义"怎么翻柜子"的完整规则
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)          // 去哪个柜子翻
                .filterExpression(expression)      // 只翻带这个标签的抽屉
                .similarityThreshold(0.5)          // 相似度门槛：低于 0.5 的卡片宁可不带（带错的更糟）
                .topK(3)                           // 最多带 3 张（带多了 prompt 冗长、费钱）
                .build();

        // 第 5 步：组装成 Advisor——零件① 翻柜子规则 + 零件② 抄题规则（空结果兜底）
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }

//    LoveApp 的 ChatClient 对象应用这个 Advisor：
//    chatClient.advisors(
//            LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//    loveAppVectorStore, "已婚"
//            )
//            )
}
```

**8）LoveAppContextualQueryAugmenterFactory.java**（完整，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂：负责"空结果兜底"
 * 默认行为下，就算检索一张卡片都没找到，prompt 仍是"请回答：xxx"——
 * 没有参考资料，AI 就开始自由发挥（幻觉回来了）；这个工厂把这种行为关掉，
 * 检索为空时改输出一套固定话术——开卷考试没翻到书，就老实说不会
 */
public class LoveAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createInstance() {
        // "没翻到书"时改用的固定话术（三引号内是真正要发给大模型的文字，不能写注释）
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);

        return ContextualQueryAugmenter.builder()
                // ① 关掉"没资料也硬答"的默认行为
                .allowEmptyContext(false)
                // ② 检索结果为空时，改用上面这套固定话术
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
```

**9）LoveAppRagCloudAdvisorConfig.java**（完整，支线①，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
*   云知识库向量存储配置（生产级支线①）
*   把"读文档 → 切卡片 → 入柜"整套杂活外包给阿里云百炼：
*   在控制台建好数据索引并导入文档后，本地只留一个云端检索器
*/
@Configuration
@Slf4j
class LoveAppRagCloudAdvisorConfig {

    //读取Key（从 application-local.yml 拿灵积钥匙）
    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        // 根据key创建API（访问云端知识库的"门禁卡"）
        DashScopeApi dashScopeApi = new DashScopeApi(dashScopeApiKey);
        final String KNOWLEDGE_INDEX = "恋爱大师";   // 云端数据索引名（需在百炼控制台提前建好）
        // 云端检索器：去百炼的"恋爱大师"索引里找相关文档（切分/向量化全在云端完成）
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX)
                        .build());
        // 组装成 Advisor——LoveApp 注入它，doChatWithRag2 用的就是这位
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}
```

**10）PgVectorVectorStoreConfig.java**（完整，支线②，默认注释关闭，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

// 支线②：pgvector 生产级卡片柜（默认注释关闭——需要 PostgreSQL 才打开）
// 和内存柜 SimpleVectorStore 的区别：真正的数据库柜，重启不丢、多人共享、可扩展
//@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;   // 做卡片工人（入库时用它读文档）

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        // jdbcTemplate 连 PostgreSQL（配置来自数据源）；dashscopeEmbeddingModel 还是那位"翻译官"
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // 向量维度：和灵积 Embedding 输出对齐
                .distanceType(COSINE_DISTANCE)       // 距离算法：余弦相似度（算"意思像不像"）
                .indexType(HNSW)                     // 索引算法：高维快速近邻搜索
                .initializeSchema(true)              // 自动建表建索引（首次启动免手写 DDL）
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
        // 加载文档，分批添加（DashScope Embedding API 限制单次最多 25 条，一次性全塞会报 The input texts limit 25）
//       已加入一次，需要文档时再继续加入
//        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
//        int batchSize = 10;                          // 每批 10 张，稳在 25 的限制之下
//        for (int i = 0; i < documents.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, documents.size());
//            vectorStore.add(documents.subList(i, end));
//        }
        return vectorStore;
    }
}
```

**11）MultiQueryExpanderDemo.java**（完整，demo/rag 包，含逐行注释——与仓库当前文件一致）：

```java
package com.hui.huiaiagent.demo.rag;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询扩展器 Demo（和 QueryRewriter 同属"查询变换"家族，思路反着来）：
 * 重写器是把一个口语问题"整理成一个规范问题"；扩展器是把一个问题
 * "扩写成 3 个不同角度的问法"，各查一遍再合并——提高召回率
 */
@Component
public class MultiQueryExpanderDemo {

    // 备一个 ChatClient builder（待会交给扩展器用）
    private final ChatClient.Builder chatClientBuilder;

    // 构造器里备好（启动造一次，全局共用）
    public MultiQueryExpanderDemo(ChatModel dashscopeChatModel) {
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }

    // 进一个问法，出多个角度的问法（例："如何挽回感情" → "复合方法"/"分手后怎么复合"/"感情修复技巧"）
    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)   // 用哪个大模型来扩写
                .numberOfQueries(3)                     // 扩成 3 个（含原问题）
                .build();
        // 执行扩展：把原话装进 Query 对象 → 扩写出多个 Query → 返回列表
        List<Query> queries = queryExpander.expand(new Query(query));
        return queries;
    }
}
```

**12）LoveApp 新增内容**：字段 `@Resource private VectorStore loveAppVectorStore;`、`@Resource private QueryRewriter queryRewriter;`、`@Resource private Advisor loveAppRagCloudAdvisor;`，方法 `doChatWithRag` / `doChatWithRagQueryWriter` / `doChatWithRag2` 见"实操一、二"和实操四（选读）（完整源码可直接对照仓库 `app/LoveApp.java`）。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 77a34fc        # ★ 阶段 6 终点：RAG 全家桶（含查询重写、抽屉过滤、空结果兜底）
# 本阶段 12 个提交都可以单独切，对照"提交地图"逐个看每一步长什么样：
#   3c8d159 → 4fba4e9 → e8f05e5 → 79c72e1 → 83b1e41 → 7b46e09 → 7a1c1f8 → 0df2e54 → 733add7 → 9d3ebbe → 3c485a8 → 77a34fc
# 在旧代码上跑：mvnw.cmd test -Dtest=LoveAppTest#doChatWithRag
git checkout main           # 切回最新
```

---

## 阶段 7：工具调用——给 AI 一个工具箱（提交 `abe317c` → `8fcf40d`，共 7 个）

### 🎯 打个比方

前面的 AI 只会**说**，现在给他一个**工具箱**（7 件工具）。神奇之处在于：**你只负责把工具放进箱子，什么时候用、用哪件、怎么用，AI 自己决定**。

就像给机器人管家一个工具箱，你说"今晚做个红烧肉"，它自己判断：先拿手机（搜索菜谱）→ 再拿锅铲（做菜）。你不需要一步步教。

本阶段 7 个提交，每个（几乎）交付一件工具——**提交地图**：

| 提交 | 交付 |
| --- | --- |
| `abe317c` | ① 文件读写工具 + `FileConstant`（统一存文件的地方） |
| `e572071` | ② 联网搜索（SearchAPI + 百度引擎） |
| `c5f78f2` | ③ 网页抓取（jsoup） |
| `563cb68` | ④ 终端命令 + 工具类路径重构 |
| `0311275` | ⑤ 资源下载 |
| `b33e133` | ⑥ PDF 生成（iText） |
| `8fcf40d` | ⑦ 工具注册类（ToolRegistration）+ 测试方法 ⭐ |

### 🧾 第 0 步：前置准备

1. **申请 SearchAPI Key**（② 联网搜索的门票，暂时不玩搜索可先填占位符）：去 [searchapi.io](https://www.searchapi.io/) 注册获取；
2. 往 `application-local.yml` 追加一段：

   ```yaml
   search-api:
     api-key: 你的SearchAPI密钥
   ```

3. 文件类工具的产物都会写进项目下的 `tmp/` 目录（`.gitignore` 阶段 5 已加过 `tmp`，不会误提交）。

### 📦 第 1 步：加依赖（pom.xml）

7 件工具大多是**纯 Java**（读写文件、执行命令……零依赖），只有两件要买" specialized 建材"：

| 依赖（版本） | 给哪件工具 | 官方文档 | Maven 仓库 |
| --- | --- | --- | --- |
| `org.jsoup:jsoup`（1.19.1） | ③ 网页抓取（解析 HTML） | [jsoup.org](https://jsoup.org/) | [mvnrepository.com/…/jsoup](https://mvnrepository.com/artifact/org.jsoup/jsoup) |
| `com.itextpdf:itext-core`（9.1.0，type=pom） | ⑥ PDF 生成 | [itextpdf.com](https://itextpdf.com/) | [mvnrepository.com/…/itext-core](https://mvnrepository.com/artifact/com.itextpdf/itext-core) |
| `com.itextpdf:font-asian`（9.1.0，test scope） | ⑥ PDF 的中文字体支持 | 同上 | [mvnrepository.com/…/font-asian](https://mvnrepository.com/artifact/com.itextpdf/font-asian) |

```xml
<!--	网页抓取：解析网页内容的工具-->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.19.1</version>
</dependency>
<!--	PDF 生成依赖	-->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>9.1.0</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>font-asian</artifactId>
    <version>9.1.0</version>
    <scope>test</scope>
</dependency>
```

（`@Tool` / `@ToolParam` 注解和 `ToolCallbacks` 工具全在阶段 2 的 starter 里——又是零新增。Spring AI 官方工具文档见 [docs.spring.io/spring-ai/reference/api/tools.html](https://docs.spring.io/spring-ai/reference/api/tools.html)）

### ⚙️ 第 2 步：写配置

就上面第 0 步那两行 `search-api.api-key`（注册类用 `@Value("${search-api.api-key}")` 读它）。`FileConstant` 统一管文件目录（代码，不是配置）：

```java
public interface FileConstant {
    /** 文件保存目录：项目根目录下的 tmp */
    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";
}
```

### 💻 知识点①：开发一件工具的"万能四步"——先用手写一个最小例子

任何工具都是同一个套路，四步：

```text
第 1 步：新建一个普通 Java 类（不加 @Component！它由注册类 new 出来，不走 Spring）
第 2 步：写一个普通方法——真正干活的代码，参数、返回值都是普通 Java
第 3 步：方法上加 @Tool(description = "...")        ← 给 AI 看的"说明书"
第 4 步：每个参数加 @ToolParam(description = "...") ← 每个参数是干嘛的
```

拿"读文件"举例。先只做第 2 步——一个普通方法（此时它还只是一段普通 Java 能力）：

```java
public String readFile(String fileName) {
    return FileUtil.readUtf8String(FILE_DIR + "/" + fileName);   // hutool 一行读出文本
}
```

再补上第 3、4 步的两个注解，它就"上架"成 AI 的工具了：

```java
@Tool(description = "Read content from a file")   // 说明书：这件工具能读文件
public String readFile(
        @ToolParam(description = "Name of a file to read") String fileName) {
    // ↑ 参数说明书：要传"文件名"
    return FileUtil.readUtf8String(FILE_DIR + "/" + fileName);   // 方法体一个字没改！
}
```

三个关键认知：

1. **注解只贴"说明书"，不改干活逻辑**——加不加 `@Tool`，方法体都是普通 Java；
2. **AI 永远碰不到你的电脑**：它只会"喊"（发出工具调用指令），真正执行方法的是**你的 Java 程序**——所以安全可控；
3. **说明书是写给 AI 看的**，黄金法则：写得越清楚（能干什么、什么时候用、参数啥意思），AI 挑得越准。说明书用英文是惯例（模型对英文指令更稳），也可以试中文对比效果。

注册之后（知识点⑨），AI 眼里看到的就是这样一份"菜单"——这就是两个注解的最终效果：

```text
可用工具菜单（AI 视角，每次对话都随信附上）：
- readFile(fileName)：Read content from a file
    参数 fileName：Name of a file to read
- searchWeb(query)：Search for information from Baidu Search Engine
    参数 query：Search query keyword
- ……（7 件工具依次列出）
```

AI 每次收到你的消息，就是照着这份菜单挑工具——**菜单条目写得越准，它挑得越对**。所以"开发工具"四个字拆开就是：**写普通方法 + 把菜单条目写清楚**。

### 💻 知识点②：工具① 文件读写——FileOperationTool（提交 `abe317c`）

**缘由**：Manus 要把约会计划、用户档案**落成文件**，AI 得有"读写文件"的手。先新增 `constant/FileConstant.java`，统一"东西放哪"：

```java
public interface FileConstant {
    /** 文件保存目录：项目根目录下的 tmp */
    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";
}
```

`tools/FileOperationTool.java`（完整，逐行注释）：

```java
public class FileOperationTool {

    // 这件工具的"辖区"：tmp/file 目录（FILE_SAVE_DIR 是所有工具的公共根目录）
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")     // 说明书：能读文件
    public String readFile(
            @ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;    // 拼出完整路径
        try {
            return FileUtil.readUtf8String(filePath);   // hutool 一行读出文本
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();   // 出错也返回字符串（见拆解）
        }
    }

    @Tool(description = "Write content to a file")      // 说明书：能写文件
    public String writeFile(
            @ToolParam(description = "Name of the file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            FileUtil.mkdir(FILE_DIR);                    // 目录不存在就先建
            FileUtil.writeUtf8String(content, filePath); // hutool 一行写入
            return "File written successfully to: " + filePath;   // 把结果告诉 AI
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
```

拆解两个**所有工具通用**的习惯：**① 返回值永远是 String**——AI 读的是文字，返回"干完了 + 结果在哪"它才能继续决策；**② 异常也 catch 成字符串**——"Error xxx"不是掩饰错误，而是把错误喂给 AI：它会换个姿势重试，或老实告诉用户。

### 💻 知识点③：工具② 联网搜索——WebSearchTool（提交 `e572071`）

**缘由**：大模型的知识有"截止日期"，问新鲜事它只能编——给它一只能上网查资料的手。

```java
public class WebSearchTool {

    // SearchAPI（第三方搜索服务）的接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;                         // 搜索服务的钥匙

    // ★ 钥匙从构造器传进来：工具类不是 Spring 零件，没人自动注入，
    //   注册类 new 它的时候会把从 application-local.yml 读到的钥匙递过来（知识点⑨）
    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Baidu Search Engine")  // 说明书：能联网搜
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);                        // 搜索关键词
        paramMap.put("api_key", apiKey);                 // 钥匙（第 0 步申请的 SearchAPI Key）
        paramMap.put("engine", "baidu");                 // 用百度引擎搜
        try {
            // 发 GET 请求，拿到响应 JSON 字符串
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            JSONObject jsonObject = JSONUtil.parseObj(response);             // 解析 JSON
            JSONArray organicResults = jsonObject.getJSONArray("organic_results"); // 取"自然搜索结果"
            List<Object> objects = organicResults.subList(0, 5);             // 只取前 5 条（多了费 token）
            // 拼成一段文字返回——AI 读的是文本，不是 JSON 对象
            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
            return result;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}
```

拆解：这件工具的新知识点是**"钥匙从构造器进"**——后面会看到注册时手动 `new WebSearchTool(searchApiKey)` 递钥匙。其余就是"发 HTTP → 解析 JSON → 拼字符串"的老三样。

### 💻 知识点④：工具③ 网页抓取——WebScrapingTool（提交 `c5f78f2`）

**缘由**：搜索结果只有标题和摘要，AI 想看网页**正文细节**，得有"打开网页"的手。7 件里最短的一件（jsoup 依赖就是为它买的）：

```java
public class WebScrapingTool {

    @Tool(description = "Scrape the content of a web page")   // 说明书：能抓网页内容
    public String scrapeWebPage(
            @ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            // jsoup：像打开浏览器一样请求网页并解析成 Document 对象
            Document doc = Jsoup.connect(url).get();
            return doc.html();                              // 返回整个页面的 HTML 源码
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
```

拆解：`Jsoup.connect(url).get()` 一行完成"发请求 + 收响应 + 解析 HTML"（[jsoup.org](https://jsoup.org/)）。返回整页 HTML 虽然很长，但信息最全——AI 自己会从里面挑有用的。

### 💻 知识点⑤：工具④ 终端命令——TerminalOperationTool（提交 `563cb68`）

**缘由**：跑脚本、装依赖，AI 得有"敲命令行"的手。⚠️ 也是**最危险**的一件——演示项目玩玩即可，真实项目慎开。

```java
//仅在windows中可以使用
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")   // 说明书：能执行终端命令
    public String executeTerminalCommand(
            @ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();             // 收集命令打印的所有输出
        try {
            // ProcessBuilder：在 Java 里启动一个系统进程
            // "cmd.exe /c 命令" = 让 Windows 的 cmd 执行完这条命令就退出
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();                  // 启动进程（命令开始跑）
            // 挂一根"读输出的管子"：命令打印什么，我们就读什么
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {    // 一行行读
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();                   // 等命令跑完，拿退出码
            if (exitCode != 0) {                                // 非 0 = 命令失败
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();                               // 输出整段交回给 AI
    }
}
```

拆解：Java 老三样——**启动进程（ProcessBuilder）→ 读输出（InputStream）→ 等结束拿退出码（waitFor）**。写死了 `cmd.exe` 所以**仅 Windows 可用**；Mac/Linux 把第一参数换成 `new ProcessBuilder("sh", "-c", command)`。

### 💻 知识点⑥：工具⑤ 资源下载——ResourceDownloadTool（提交 `0311275`）

**缘由**：做约会计划要配图——AI 得有"把网上的图片/文件存到本地"的手。套路和知识点②完全一样，正好体会"熟练后 5 分钟一件"：

```java
public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given URL")  // 说明书：能下载资源
    public String downloadResource(
            @ToolParam(description = "URL of the resource to download") String url,
            @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";   // 辖区：tmp/download
        String filePath = fileDir + "/" + fileName;
        try {
            FileUtil.mkdir(fileDir);                        // 建目录
            HttpUtil.downloadFile(url, new File(filePath));  // hutool 一行：从 url 下载存盘
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
```

### 💻 知识点⑦：工具⑥ PDF 生成——PDFGenerationTool（提交 `b33e133`）

**缘由**：任务成果要**交付成品**——"生成一份约会计划 PDF"（iText 依赖就是为它买的）。

```java
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")  // 说明书：能生成 PDF
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";      // 辖区：tmp/pdf
        String filePath = fileDir + "/" + fileName;
        try {
            FileUtil.mkdir(fileDir);
            // iText 的"三层套娃"（try-with-resources 自动关流），照抄即可：
            // PdfWriter（往文件写字节）→ PdfDocument（PDF 文档结构）→ Document（排版入口）
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // ★ 全工具箱唯一的大坑：必须设中文字体，否则 PDF 里中文全是乱码！
                // "STSongStd-Light" 是 iText 内置宋体（font-asian 依赖提供）
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                document.add(new Paragraph(content));        // 内容作为一个段落写入
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
```

拆解：三层结构不必深究（照抄就行），**必须记住中文字体那两行**。想要更好看的字体，仓库代码注释里留了"自己下载 ttf 字体文件再加载"的方案。

### 💻 知识点⑧：工具⑦ 结束按钮——TerminateTool（随阶段 8 的提交 `bd434b1` 进仓库）

**缘由**：阶段 8 的 Manus 是循环干活的，得有办法喊"做完了，收工"——就是这件**不干活、只喊话**的工具：

```java
public class TerminateTool {

    // 说明书特意写长：告诉 AI"任务完成（或没法继续）时，就按我这个按钮"
    @Tool(description = """
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.
            "When you have finished all the tasks, call this tool to end the work.
            """)
    public String doTerminate() {        // 没有参数，所以不需要 @ToolParam
        return "任务结束";               // 只返回一句话，什么活都不干
    }
}
```

拆解：它的"功能"全在**说明书**里——AI 读完知道"该停时就调它"。阶段 8 的 `act()` 正是靠检查"这轮工具调用里有没有 `doTerminate`"来结束循环的，伏笔在此埋下。

### 💻 知识点⑨：把 7 件工具装进箱子——ToolRegistration（提交 `8fcf40d`）

工具造好了 AI 还看不见，得**集中登记**。新增 `tools/ToolRegistration.java`：

```java
@Configuration                                   // 配置类：本类负责造零件
public class ToolRegistration {

    @Value("${search-api.api-key}")              // 从 application-local.yml 读搜索钥匙
    private String searchApiKey;

    @Bean                                         // 造一个名叫 allTools 的零件（ToolCallback 数组）
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();          // ① 文件读写
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);          // ② 联网搜索（★钥匙在这递）
        WebScrapingTool webScrapingTool = new WebScrapingTool();                // ③ 抓网页
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool(); // ⑤ 下载资源
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool(); // ④ 终端命令
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();          // ⑥ PDF 生成
        TerminateTool terminateTool = new TerminateTool();                      // ⑦ 结束按钮
        return ToolCallbacks.from(               // ★ 翻译：7 个普通对象 → 框架统一的 ToolCallback[]
            fileOperationTool,
            webSearchTool,
            webScrapingTool,
            resourceDownloadTool,
            terminalOperationTool,
            pdfGenerationTool,
            terminateTool
        );
    }
}
```

拆解：核心就一行 `ToolCallbacks.from(...)`——把"普通 Java 对象"**翻译**成统一的 `ToolCallback[]`（适配器模式）：从此 AI 眼里 7 件工具长一个样 = 一份说明书 + 一个可调用方法。注意工具全是 **`new` 出来的**（不是 Spring 零件）——这正是知识点③ 里"钥匙要手动递"的原因。类注释还点明它用到四个模式：工厂（集中创建）、依赖注入（@Value）、注册（中央登记）、适配器（统一接口）。

### 💻 知识点⑩：使用时只需一行——doChatWithTools

LoveApp 加字段和方法：

```java
@Resource
private ToolCallback[] allTools;      // 工具箱（知识点⑨ 的 @Bean 造的）

public String doChatWithTools(String message, String chatId) {
    ChatResponse response = chatClient
            .prompt()                 // ① 拿出信纸
            .user(message)            // ② 写上用户的话
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))   // ③ 记忆参数（老朋友了）
            .advisors(new MyLoggerAdvisor())   // ④ 挂日志老师：控制台能看见 AI 挑了什么工具
            .tools(allTools)          // ⑤ ✨ 工具箱说明书随信附上，AI 自己挑
            .call()                   // ⑥ 寄出
            .chatResponse();
    String content = response.getResult().getOutput().getText();
    log.info("content: {}", content);
    return content;
}
```

幕后发生了什么？你说"帮我搜今天杭州天气"→ AI 回话（不是文字，是**指令**）："请用 `searchWeb` 工具，参数是 `杭州 天气`"→ 框架收到指令，**在你的 Java 程序里执行**那个方法 → 把结果念给 AI 听 → AI 这才组织成人话回你。**AI 动嘴，你的程序动手**——所以安全可控。

### 🧪 动手试试

```bash
mvnw.cmd test -Dtest=LoveAppTest#doChatWithTools
```

测试里连发 6 句话（搜索推荐、看网站案例、下载壁纸、跑脚本、存档案、生成 PDF），盯着控制台日志：你会亲眼看到 AI 发出的"工具指令"和参数长什么样，以及 `tmp/` 目录下陆续多出来的文件。

**挑战题**：自己写一个 `@Tool(description = "Get current time")` 返回 `new Date().toString()` 的工具，加进 `ToolRegistration`，然后问 AI"现在几点了"——看它会不会自己用上。

### 🕳️ 小心踩坑

- **没配 `search-api.api-key`** → 启动直接红（`@Value` 找不到占位符）——这就是为什么第 0 步说"暂时不玩也要填占位符"；
- **PDF 中文乱码** → 忘了 `document.setFont(font)` 那两行；
- **TerminalOperationTool 只认 Windows**（写死 `cmd.exe`）→ Mac/Linux 换 `sh`；真实项目慎给 AI 开终端权限；
- 工具方法**尽量返回字符串**并 try-catch 兜底（"Error xxx"）——异常信息也是 AI 判断"下一步怎么办"的线索；
- `@Tool` 的 description 写含糊（比如就写 "search"），AI 挑工具会挑错——说明书要写"能干什么、什么时候用"。

### 📦 附录：阶段 7 完整代码（照抄可跑）

目录结构（新增 `tools` 包 + `constant` 包）：

```
src/main/java/com/hui/huiaiagent/constant/
└── FileConstant.java                 # 文件保存目录常量
src/main/java/com/hui/huiaiagent/tools/
├── FileOperationTool.java            # ① 文件读写
├── WebSearchTool.java                # ② 联网搜索
├── WebScrapingTool.java              # ③ 网页抓取
├── TerminalOperationTool.java        # ④ 终端命令
├── ResourceDownloadTool.java         # ⑤ 资源下载
├── PDFGenerationTool.java            # ⑥ PDF 生成
├── TerminateTool.java                # 结束按钮（阶段 8 的提交才创建）
└── ToolRegistration.java             # 工具注册（@Bean）
```

**1）FileConstant.java**（完整）：

```java
package com.hui.huiaiagent.constant;

public interface FileConstant {

    /**
     * 文件保存目录
     */
    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";
}
```

**2）FileOperationTool.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.hui.huiaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（提供文件读写功能）
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content
    ) {
        String filePath = FILE_DIR + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
```

**3）WebSearchTool.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);
            // 拼接搜索结果为字符串
            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
            return result;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}
```

**4）WebScrapingTool.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class WebScrapingTool {

    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
```

**5）TerminalOperationTool.java**（完整，仅 Windows）：

```java
package com.hui.huiaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//仅在windows中可以使用
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
//            Process process = Runtime.getRuntime().exec(command);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
```

**6）ResourceDownloadTool.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.hui.huiaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
```

**7）PDFGenerationTool.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.hui.huiaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;


public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
//                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
//                        .toAbsolutePath().toString();
//                PdfFont font = PdfFontFactory.createFont(fontPath,
//                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用内置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
```

**8）TerminateTool.java**（完整。史实小注：这个文件其实是**阶段 8 的提交 `bd434b1`** 才写出来并加进 `ToolRegistration` 的——阶段 7 就先建好文件也完全没问题，正文按当前仓库的最终样子展示）：

```java
package com.hui.huiaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;

public class TerminateTool {

    @Tool(description = """
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.
            "When you have finished all the tasks, call this tool to end the work.
            """)
    public String doTerminate() {
        return "任务结束";
    }
}
```

**9）ToolRegistration.java**（完整）：

```java
package com.hui.huiaiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    /*
    1. 工厂模式：allTools() 方法作为一个工厂方法，负责创建和配置多个工具实例，然后将它们包装成统一的数组返回。这符合工厂模式的核心思想 - 集中创建对象并隐藏创建细节。
    2. 依赖注入模式：通过 @Value 注解注入配置值，以及将创建好的工具通过 Spring 容器注入到需要它们的组件中。
    3. 注册模式：该类作为一个中央注册点，集中管理和注册所有可用的工具，使它们能够被系统其他部分统一访问。
    4. 适配器模式的应用：ToolCallbacks.from 方法可以看作是一种适配器，它将各种不同的工具类转换为统一的 ToolCallback 数组，使系统能够以一致的方式处理它们。
     */

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        return ToolCallbacks.from(
            fileOperationTool,
            webSearchTool,
            webScrapingTool,
            resourceDownloadTool,
            terminalOperationTool,
            pdfGenerationTool,
            terminateTool
        );
    }
}
```

**10）LoveApp / LoveAppTest 新增**：字段 `@Resource private ToolCallback[] allTools;` + 方法 `doChatWithTools`（见"知识点⑩"）；测试方法 `doChatWithTools` + 辅助方法 `testMessage`（见"动手试试"，完整源码对照仓库 `app/LoveAppTest.java`）。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 8fcf40d        # ★ 阶段 7 终点：7 件工具 + 注册类全部就位
# 逐件对照（每个提交交付一件工具）：abe317c → e572071 → c5f78f2 → 563cb68 → 0311275 → b33e133 → 8fcf40d
# 在旧代码上跑：mvnw.cmd test -Dtest=LoveAppTest#doChatWithTools（6 句话连测，注意 tmp/ 下长出的文件）
git checkout main           # 切回最新
```

---

## 阶段 8：手写 Manus 智能体——会自己做手抄报的机器人 ⭐（提交 `bd434b1`）

> 这是全项目**最核心**的一章。不装任何 Agent 框架，用 4 个类手写一个迷你 Manus。

### 🎯 打个比方

让普通小朋友（AI）独立完成一份手抄报，他会怎么做？

> 想一想（"我需要查资料"）→ 动手做（拿工具书查）→ 看看结果（"哦，资料有了"）→ 再想（"接下来画图"）→ 动手做（拿彩笔）→ …… → 做完喊一声"完成！"✅

这就是 **ReAct 模式**：**想一想（Reason）→ 做一步（Act）→ 看结果 → 循环**，直到任务完成。Manus 智能体就是把这套流程写成代码。

### 四层积木（按这个顺序读代码）

```
AgentState   —— 状态灯：空闲/运行中/已完成/出错
BaseAgent    —— 定好"最多做几步、什么时候停"（纪律委员）
ReActAgent   —— 规定每一步 = 想一想 + 做一步（流程模板）
ToolCallAgent—— 真正实现"想"和"做"（干活的人）
HuiManus     —— 起名字、发工具箱、上岗（最终产品）
```

**为什么分四层、而不是写一个大类？** 每一层只回答一个问题，改起来互不牵连：

| 层 | 只回答的问题 | 套路 |
| --- | --- | --- |
| BaseAgent | 循环纪律：跑几圈、怎么停、状态灯怎么转 | 模板方法：run() 把流程焊死，step() 留空给子类填 |
| ReActAgent | 每一步的形状：想 + 做 | 模板方法再套一层：step() = think() + act() |
| ToolCallAgent | 想和做的真实现 | 具体干活层 |
| HuiManus | 配置：叫什么、什么人设、几件工具、几步封顶 | 纯配置，零逻辑 |

好处马上兑现：想换终止策略只动 BaseAgent；想造第二个智能体（比如"代码助手"），继承 ToolCallAgent 写 30 行配置就行——官方框架（LangChain4j 的 AiServices 之类）藏起来的，其实就是这四层。`

整台机器转起来的样子（先混个眼熟，读完知识点②~⑦ 再回来看，会恍然大悟）：

```text
run("把'你好'写入hello.txt")                     ← 测试类点火
 └─ for 循环（最多 20 圈；状态灯变 FINISHED 才提前停）
     └─ step()
         ├─ think()：把全部历史 + 7 件工具菜单 发给大模型
         │    ├─ 挑了工具 → act()：真正执行工具
         │    │              ├─ 结果记回 messageList（下轮"想"的输入）
         │    │              └─ 这轮调了 doTerminate？→ 是：状态灯 FINISHED，循环结束 ✅
         │    └─ 没挑工具 → 返回"思考完成 - 无需行动"
```

### 🧾 第 0 步：前置准备

**零新依赖、零新配置、零新门卡**——本章的全部素材前面都备齐了：

- 阶段 7 的工具箱：`ToolCallback[] allTools`（Spring 注入）；
- 阶段 2 的 `ChatModel`；
- 阶段 4 的 `MyLoggerAdvisor`；
- 本提交补上最后一块积木：`TerminateTool`（阶段 7 预告的"结束按钮"，随本章一起进仓库）。

这正是本章想证明的事：**Agent 不神秘 = 普通类 + 一个循环 + 一个工具箱**。

### 📦 第 1 步：加依赖（pom.xml）

无。所有类用的都是 Spring AI 已有能力（`ToolCallingManager`、`DashScopeChatOptions` 等都在 starter 里）。

### ⚙️ 第 2 步：写配置（application.yml）

无。

### 💻 知识点①：先造"状态灯"——AgentState

`agent/model/AgentState.java`（4 个状态，一枚状态灯）：

```java
public enum AgentState {
    IDLE,       // 空闲：随时可以接活
    RUNNING,    // 运行中：正在一步步干活
    FINISHED,   // 已完成：AI 喊了"做完了"，或步数用光
    ERROR       // 出错：半路摔了
}
```

### 💻 知识点②：BaseAgent——纪律委员（防止无限循环）

读代码前，先记住它手里的**三个关键属性**（后面所有逻辑都围着它们转）：

```java
private int maxSteps = 10;                        // 最多走几步（保险丝的"长度"）
private int currentStep = 0;                      // 现在走到第几步
private List<Message> messageList = new ArrayList<>();  // 工作记忆：全程对话按顺序记在这
```

`agent/BaseAgent.java` 的 `run()` 核心（就是开篇那张循环图的代码版）：

```java
public String run(String userPrompt) {
    if (this.state != AgentState.IDLE) {                       // 保险丝①：跑着的不能再跑
        throw new RuntimeException("Cannot run agent from state: " + this.state);
    }
    if (StringUtil.isBlank(userPrompt)) {                      // 保险丝②：空任务不接
        throw new RuntimeException("Cannot run agent with empty user prompt");
    }
    state = AgentState.RUNNING;                                // 点灯：运行中
    messageList.add(new UserMessage(userPrompt));              // 用户的话记进工作记忆
    List<String> results = new ArrayList<>();
    try {
        for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {   // 两道保险！
            int stepNumber = i + 1;
            currentStep = stepNumber;
            String stepResult = step();                        // 做一步（子类决定怎么做）
            results.add("Step " + stepNumber + ": " + stepResult);
        }
        if (currentStep >= maxSteps) {                         // 步数用光也算完成
            state = AgentState.FINISHED;
            results.add("Terminated: Reached max steps (" + maxSteps + ")");
        }
        return String.join("\n", results);                     // 把每步结果拼成总报告
    } catch (Exception e) {
        state = AgentState.ERROR;                              // 摔了 → 记错误
        return "执行错误" + e.getMessage();
    } finally {
        this.cleanup();                                        // 无论成败都清理
    }
}
```

拆解——两道保险，防止 AI 陷入"永远做不完"死循环（每一步都在花你的钱！）：

1. `i < maxSteps`：**最多 maxSteps 步**（HuiManus 设了 20），像妈妈说"最多磨蹭 20 分钟必须交作业"；
2. `state != FINISHED`：AI 主动喊"做完了"（知识点⑤ 的 doTerminate），随时可以提前结束。

另外两个设计：`messageList` 是机器人的**工作记忆**（你说的话、它的想法、每个工具的结果按顺序全记在里面）；`@Data`（Lombok）给这十来个属性自动生成了 getter/setter，所以后面的代码才能 `setState(...)`、`getChatClient()` 这样调。

### 💻 知识点③：ReActAgent——流程模板

```java
@Override
public String step() {               // "做一步"的完整定义：
    try {
        boolean shouldAct = think();     //   先想一想：需要动用工具吗？
        if (!shouldAct) {
            return "思考完成 - 无需行动";   //   不需要 → 直接回答就行
        }
        return act();                    //   需要 → 动手！
    } catch (Exception e) {
        return "步骤执行失败: " + e.getMessage();   // 单步摔了不算全盘输
    }
}
```

它只定了"每一步 = think + act"这个**模板**，但 think 和 act 都是抽象方法——**怎么想、怎么做**留给下一层。

### 💻 知识点④：think()——想一想（完整代码 + 真实任务追踪）

`ToolCallAgent.think()` 完整实现，逐行注释（读完再跟着任务走一遍就通透了）：

```java
@Override
public boolean think() {
    // ① 每圈开始，先把"下一步提醒"作为一条用户消息塞进历史
    //    （提醒内容见知识点⑧：主动挑工具、做完就按 terminate——没有它模型容易忘记收工）
    if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
        UserMessage userMessage = new UserMessage(getNextStepPrompt());
        getMessageList().add(userMessage);
    }
    // ② 把【全部历史 + 代理模式选项(chatOptions)】打包成一个 Prompt
    List<Message> messageList = getMessageList();
    Prompt prompt = new Prompt(messageList, chatOptions);
    try {
        // ③ 发起调用：身份卡(system) + 7 件工具菜单(tools) 一起递上
        ChatResponse chatResponse = getChatClient().prompt(prompt)
                .system(getSystemPrompt())
                .tools(availableTools)
                .call()
                .chatResponse();
        // ④ 把这轮回话存进字段——act() 马上要靠它执行工具（两个方法的"接力棒"）
        this.toolCallChatResponse = chatResponse;
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        // ⑤ 打印思考日志：它想了什么、挑了哪几件工具、参数是什么（肉眼验收全靠这段）
        String result = assistantMessage.getText();
        List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
        log.info(getName() + "的思考: " + result);
        log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
        String toolCallInfo = toolCallList.stream()
                .map(toolCall -> String.format("工具名称：%s，参数：%s",
                        toolCall.name(),
                        toolCall.arguments()))
                .collect(Collectors.joining("\n"));
        log.info(toolCallInfo);
        if (toolCallList.isEmpty()) {
            // ⑥ 没挑工具 = 它只想说话 → 把这句"纯发言"记进历史，返回"不用行动"
            getMessageList().add(assistantMessage);
            return false;
        } else {
            // ⑦ 挑了工具 = 要动手 → 这条"指令单"先不手动记录！
            //    因为下一步 executeToolCalls 会把它和工具结果一起补进历史，手动记会重复
            return true;
        }
    } catch (Exception e) {
        // ⑧ 思考过程出问题：把错误也写进历史（下圈它会看到并自我调整），返回"不用行动"
        log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
        getMessageList().add(
                new AssistantMessage("处理时遇到错误: " + e.getMessage()));
        return false;
    }
}
```

三个容易看漏的点：**④是 think 和 act 的接力棒**（回话存在字段里传过去）；**⑥⑦的记忆规则不对称**——纯发言要手动记、指令单不记（act 会代记）；**⑧连出错都往历史上写**，Agent 才能从错误里恢复。

光看代码容易晕，**跟着真实任务走一遍**（任务：`把"你好"写入 hello.txt`）：

```text
think() 第 1 圈开始时，messageList 里已经有两条：
  [用户消息："把'你好'写入 hello.txt"]
  [下一步提醒："Based on user needs, proactively select..."]

→ 把【全部历史 + 身份卡 + 7 件工具菜单】打包发给大模型
→ 大模型回话——注意！它不是回文字，而是回一张"指令单"：
  toolCallList = [ { 工具名: writeFile, 参数: {fileName:"hello.txt", content:"你好"} } ]
→ 指令单非空 → return true（意思是"我要动手了"）
```

### 💻 知识点⑤：act()——做一步（完整代码 + 真实任务追踪）

```java
@Override
public String act() {
    // ① 保险：think 存的回话里根本没有工具指令 → 没什么可执行的
    if (!toolCallChatResponse.hasToolCalls()) {
        return "没有工具需要调用";
    }
    // ② 真正执行工具：ToolCallingManager 拿着【think 递来的指令单 + 当前历史】干活
    //    它会在你的 JVM 里调用那 7 个 @Tool 方法（AI 动嘴，你的程序动手）
    Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
    ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
    // ③ 全方法最关键的一行：用返回的 conversationHistory 整包替换 messageList
    //    这份新历史 = 原有全部内容 + AI 的指令单 + 工具的执行结果
    //    → 下一圈 think 时，AI 看得见工具干了什么（ReAct 的"看结果"环节）
    setMessageList(toolExecutionResult.conversationHistory());
    // ④ 从最后一条消息（工具结果）里检查：这轮有没有喊"做完了"
    ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
    boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
            .anyMatch(response -> response.name().equals("doTerminate"));
    if (terminateToolCalled) {
        // ⑤ 喊了 → 状态灯置 FINISHED → run() 的循环条件不成立 → 收工
        setState(AgentState.FINISHED);
    }
    // ⑥ 把工具结果拼成人话返回（同时打进日志），作为本轮 step 的成果
    String results = toolResponseMessage.getResponses().stream()
            .map(response -> "工具 " + response.name() + " 返回的结果：" + response.responseData())
            .collect(Collectors.joining("\n"));
    log.info(results);
    return results;
}
```

一句话记住 act：**执行（②）→ 记账（③）→ 查收工（④⑤）→ 交报告（⑥）**。其中 ③ 是整个 Agent 的命脉——工具结果不写回历史，下一圈思考就是睁眼瞎。

接着上面的任务**继续走**：

```text
act() 第 1 圈：
→ executeToolCalls 真的执行了 writeFile —— tmp/file/hello.txt 出现在硬盘上！
→ conversationHistory 里现在是 4 条：
    [用户消息] [下一步提醒] [AI 的指令单] [工具结果："File written successfully..."]
  整包替换进 messageList —— 下一圈"想"时，AI 看得见工具干了什么
→ 检查这圈有没有调 doTerminate —— 没有 → 状态灯不变，回到 run() 的循环

think() 第 2 圈：
→ AI 看到"文件已写好、任务已完成" → 按下一步提醒的约定，挑了 doTerminate
act() 第 2 圈：
→ 执行 doTerminate → 检查发现它被调用了 → setState(FINISHED)
→ run() 的循环条件 state != FINISHED 不成立 → 收工，返回总报告 🎉
```

这就是"它会连续做事"的全部秘密：**每一圈都带着全部历史重新想**，工具结果自然接上了下一圈的决策。

### 💻 知识点⑥：messageList 的完整演化——Agent 的"心脏"（务必看懂）

上面零散提到的消息记录规则，汇成一张总表（还是 `把"你好"写入 hello.txt` 这个任务）：

| 时间点 | messageList 里有什么（按顺序） |
| --- | --- |
| 圈 1 开始前（run() 里） | ① 用户消息：`把"你好"写入 hello.txt` |
| 圈 1 think 第①步后 | ② 下一步提醒（**每圈开头都会新塞一条**） |
| 圈 1 think 返回 true | （指令单**不记**——act 会代记，见知识点④第⑦条注释） |
| 圈 1 act 第③步后 | ③ AI 指令单：`writeFile(fileName="hello.txt", content="你好")`<br>④ 工具结果：`File written successfully to: .../hello.txt` |
| 圈 2 think 第①步后 | ⑤ 又一条新的下一步提醒 |
| 圈 2 think | AI 看完 ①~⑤：文件已写好、任务完成 → 挑了 `doTerminate` |
| 圈 2 act 第③步后 | ⑥ `doTerminate` 指令单 + 结果"任务结束" → 状态灯 FINISHED，循环结束 🎉 |

三条规律（这就是 Agent 的记忆法则）：

1. **只增不删**：历史从第一条用户消息一直累加到收工，谁都不删——"记忆"就是这么粗暴；
2. **每圈固定进账 2~3 条**：一条提醒 + （一条指令单 + 一条工具结果）；
3. **think 读全部、act 写全部**：想的时候带着完整历史，干完把新账整包记回——所谓"工作记忆"，就是这个不断变长的消息列表。

（推论：任务越长列表越长，token 花费也越多——maxSteps 不仅防死循环，也是在防"账本爆炸"。）

### 💻 知识点⑦：withProxyToolCalls(true)——为什么必须"拿回控制权"

ToolCallAgent 构造器里有一行不起眼但**至关重要**的配置：

```java
this.chatOptions = DashScopeChatOptions.builder()
        .withProxyToolCalls(true)    // "代理模式"：框架别替我自动执行工具，我自己管！
        .build();
```

用一张对比表讲清它关掉了什么：

| | 默认模式（不设/ false） | 代理模式（true，本项目用） |
| --- | --- | --- |
| 谁执行工具 | **框架自动执行**，结果自动塞回对话 | **你的 act() 手动执行** |
| 消息列表 | 框架自动维护 | 你自己 `setMessageList` 管理 |
| 中途插自己的逻辑 | 插不进手（黑盒跑完） | 每一圈都能插（比如"检查是否收工"） |

阶段 7 让框架代劳很省心；但写 Agent 必须自己掌握"想 → 做 → 看结果"的**节奏**——不拿回控制权，就写不出这个循环。

### 💻 知识点⑧：HuiManus——上岗！

```java
@Component
public class HuiManus extends ToolCallAgent {

    public HuiManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);                       // 领工具箱
        this.setName("huiManus");
        String SYSTEM_PROMPT = """
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);  // 身份卡：全能助手
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);  // 每一步前的提醒
        this.setMaxSteps(20);                  // 最多 20 步
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())   // 挂个日志老师，方便看它干活
                .build();
        this.setChatClient(chatClient);
    }
}
```

拆解：这一层没有任何新逻辑——**纯粹是"配置"**：起名字、写两张提示词、领工具箱。`@Component` 让 Spring 造好它并注入测试/控制器（`ToolCallback[]` 和 `ChatModel` 都是前面阶段造好的零件，自动递进来）。

### 一次真实任务的完整剧本

`huiManusTest.java` 里给它的任务（真事，项目作者跑通过的）：

> "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，并结合一些网络图片，制定一份详细的约会计划，并以 PDF 格式输出"

Manus 的做法：**想**（要搜地点）→ **做**（WebSearchTool）→ **想**（要抓网页细节）→ **做**（WebScrapingTool）→ **想**（下载图片）→ **做**（ResourceDownloadTool）→ **想**（写计划）→ **做**（FileOperationTool）→ **想**（转 PDF）→ **做**（PDFGenerationTool）→ **做完了**（TerminateTool）🎉。每一步的结果，都通过阶段 9 的 SSE 实时推到了前端页面上。

逐圈看这个任务（圈数和顺序由模型**当场自己决定**，下表是一次真实运行的示意——每次可能略有不同，这正是"自主"的含义）：

| 圈 | think 的决定 | act 执行了什么 | 这圈结束后，messageList 里多了什么 |
| --- | --- | --- | --- |
| 1 | 挑 `searchWeb("上海静安区 约会地点")` | 联网搜索 | 提醒 + 指令单 + 搜索结果（候选地点列表） |
| 2 | 挑 `scrapeWebPage(某地点页面)` | 抓网页正文 | 提醒 + 指令单 + 网页细节 |
| 3 | 挑 `downloadResource(图片url, ...)` | 图片存到本地 | 提醒 + 指令单 + 下载路径 |
| 4 | 挑 `writeFile("计划书.md", ...)` | 写约会计划 | 提醒 + 指令单 + 写盘成功 |
| 5 | 挑 `generatePDF("约会计划.pdf", ...)` | 转成 PDF | 提醒 + 指令单 + PDF 路径 |
| 6 | 挑 `doTerminate` | —— | 指令单 + "任务结束" → **FINISHED** 🎉 |

对照知识点⑥ 的规律数一数：每圈恰好进账 2~3 条、只增不删——表里最后一列就是"心脏"跳动的记录。

### 🧪 动手试试

```bash
mvnw.cmd test -Dtest=huiManusTest
```

控制台会打印每一步它在"想"什么、挑了什么工具——像看机器人写日记。（这个测试是真金白银跑完整个任务的：搜索、下载、写文件、生成 PDF 一步不少，注意 `tmp/` 目录里长出来的成品。）

**挑战题**：把 `maxSteps` 调成 3 再跑同一个任务——看它在被强制打断时，`results` 里的最后一句是什么。

### 🕳️ 小心踩坑

- **`withProxyToolCalls(true)` 忘了设** → 框架抢着自动执行工具，消息列表被打乱，"想一想→做一步"的循环彻底失效——这是本章最容易翻车的一行；
- **`TerminateTool` 的说明书含糊** → AI 不知道什么时候"喊停"，每次都磨到 maxSteps 用光（多花冤枉钱）——原文特意写清"任务满足或无法继续时就调用它"；
- **复用同一个实例连跑两次** → 第二次直接抛 `Cannot run agent from state: RUNNING`（状态灯没回 IDLE；阶段 9 的做法是每次 `new` 一个新的）；
- **maxSteps 设太大** → AI 钻牛角尖时每一步都在真实花钱，20 步是比较稳的上限；
- 小史实：这次提交里类名其实叫 `huiManus`（小写 h），阶段 9 才改成 `HuiManus`——自己搭的时候直接用规范名即可。

### 📦 附录：阶段 8 完整代码（照抄可跑）

目录结构（新增 `agent` 包）：

```
src/main/java/com/hui/huiaiagent/agent/
├── BaseAgent.java            # 纪律委员：run 循环 + 两道保险
├── ReActAgent.java           # 流程模板：step = think + act
├── ToolCallAgent.java        # 干活的人：think/act 真实现
├── HuiManus.java             # 最终产品：配置层
└── model/
    └── AgentState.java       # 状态灯
src/test/java/com/hui/huiaiagent/agent/
└── huiManusTest.java         # 真实任务测试
```

**1）AgentState.java**（完整）：

```java
package com.hui.huiaiagent.agent.model;

/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 已完成状态
     */
    FINISHED,

    /**
     * 错误状态
     */
    ERROR
}
```

**2）BaseAgent.java**（完整，阶段 8 原版）：

```java
package com.hui.huiaiagent.agent;

import com.hui.huiaiagent.agent.model.AgentState;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 *
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态
    private AgentState state = AgentState.IDLE;

    // 执行控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // Memory（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        // 更改状态
        state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }
}
```

（阶段 9 会在这一层追加一个 `runStream` 流式方法，到时候再抄进去。）

**3）ReActAgent.java**（完整）：

```java
package com.hui.huiaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 行动执行结果
     */
    public abstract String act();

    /**
     * 执行单个步骤：思考和行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败: " + e.getMessage();
        }
    }
}
```

**4）ToolCallAgent.java**（完整）：

```java
package com.hui.huiaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.hui.huiaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于 Act
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 输出提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName() + "的思考: " + result);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s",
                            toolCall.name(),
                            toolCall.arguments())
                    )
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if (toolCallList.isEmpty()) {
                // 只有不调用工具时，才记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具需要调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            // 任务结束，更改状态
            setState(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 返回的结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }
}
```

（上面 `think()` 里用到的 `UserMessage` 需要补一句 `import org.springframework.ai.chat.messages.UserMessage;`——照仓库抄 import 即可。）

**5）HuiManus.java**（完整，规范类名版）：

```java
package com.hui.huiaiagent.agent;

import com.hui.huiaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class HuiManus extends ToolCallAgent {

    public HuiManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("huiManus");
        String SYSTEM_PROMPT = """
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
```

**6）TerminateTool + ToolRegistration 增补**：`TerminateTool.java` 全文见阶段 7 附录；往 `ToolRegistration.allTools()` 里补上 `TerminateTool terminateTool = new TerminateTool();` 和 `ToolCallbacks.from(..., terminateTool)` 两个位置各一行。

**7）huiManusTest.java**（完整，测试类名当时就叫 `huiManusTest`）：

```java
package com.hui.huiaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class huiManusTest {

    @Resource
    private HuiManus huiManus;

    @Test
    void run() {
        String userPrompt = """
                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";
        String answer = huiManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}
```

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout bd434b1        # ★ 阶段 8 终点：Manus 智能体诞生（agent 包 + TerminateTool 一起进仓库）
mvnw.cmd test -Dtest=huiManusTest   # 真金白银跑一个完整任务：搜索→抓取→下载→写文件→PDF
git checkout main           # 切回最新
```

（小提醒：这个提交里类名还是小写的 `huiManus`/`huiManusTest`——正好可以亲眼看看阶段 9 的改名提交改了什么。）

---

## 阶段 9：接口服务化与流式输出——烤一串上一串（提交 `1419c4d`、`c377b50`）

### 🎯 打个比方

AI 生成一段回答要 20 秒。如果等全部写完再给用户，用户盯着空白屏幕 20 秒早跑了。**流式输出 = 烤串店老板烤好一串就先端上来一串**，用户边吃边等，体验完全不同。

本阶段两次提交分工：`1419c4d` 把 LoveApp 的聊天做成 HTTP 接口（含流式）；`c377b50` 把 Manus 也搬上网（给 `BaseAgent` 加 `runStream`、类名规范成 `HuiManus`），并解决跨域。

### 🧾 第 0 步：前置准备

无新门卡。前端（阶段 10）还没来，本阶段用 **curl 和阶段 1 的 Swagger 文档页（doc.html）** 当测试员。

### 📦 第 1 步：加依赖（pom.xml）

**无新增。** `SseEmitter` 在 spring-webmvc 里；`Flux`（reactor-core）早随着 Spring AI 的依赖悄悄进了 classpath。这也是个信号：**服务化只是"把已有能力摆上门面"，不需要新材料**。

### ⚙️ 第 2 步：写配置（application.yml）

**无改动。**

### 💻 知识点⓪：什么是"接口服务化"——把功能变成网址

到目前为止，想触发这个项目的 AI 只有一种办法：**在 IDEA 里跑测试类**。但真实用户在浏览器里、在手机 App 里——他们怎么用你的功能？

答案是：把功能做成一个个**网址（接口）**。用户（或前端页面）访问一个网址，就等于按下一个按钮——后端干对应的活，把结果从网址返回。

怎么做？就是阶段 1 传达室那一套：`@RestController` + `@GetMapping`，把一个 Java 方法变成网址。本阶段全部工作其实就是它——只不过返回值从 `"ok"` 这种一次性字符串，换成了**会流动的 AI 回答**。

### 💻 知识点①：先让业务层学会"流式说话"——doChatByStream（一个词的差别）

LoveApp 加一个方法（和 `doChat` 只差**一个词**）：

```java
public Flux<String> doChatByStream(String message, String chatId) {
    return chatClient
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .stream()           // ← 唯一的区别：.call() 换成 .stream()！
            .content();
}
```

拆解：`.call()` 是"等整封信写完再给你"；`.stream()` 返回 `Flux<String>`（**传送带**）——不是一个完整的箱子，而是苹果一个接一个滚出来。

### 💻 知识点②：门面层——AiController 的 5 个接口

新建 `controller/AiController.java`（阶段 1 的 Healthcontroller 有了新同事）。它一口气摆出 5 个接口，**前 4 个是同一件事的四种写法**（作者特意全留着给你对比）：

| 接口 | 返回 | 特点 |
| --- | --- | --- |
| `GET /ai/love_app/chat/sync` | `String` | 普通同步接口：憋到最后一次性给 |
| `GET /ai/love_app/chat/sse` | `Flux<String>` | 传送带（`produces = TEXT_EVENT_STREAM_VALUE`） |
| `GET /ai/love_app/chat/sse`（重载） | `Flux<ServerSentEvent<String>>` | 同上，但每段包上"事件信封"（可带 event/id 字段） |
| `GET /ai/love_app/chat/sse/emitter` | `SseEmitter` | 手动端盘：自己订阅 Flux，一段段 `emitter.send()` |
| `GET /ai/manus/chat` | `SseEmitter` | Manus 流式：机器人每干完一步播报一步 |

Flux 版（最简洁的流式写法）：

```java
@GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
                                    // ↑ 声明：我返回的是"事件流"，不是普通网页
public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
    return loveApp.doChatByStream(message, chatId);
}
```

SseEmitter 版（手动端盘，能感知"结束"和"出错"的时机）：

```java
@GetMapping("/love_app/chat/sse/emitter")
public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
    SseEmitter emitter = new SseEmitter(180000L);   // 3 分钟超时
    loveApp.doChatByStream(message, chatId)
            .subscribe(
                    chunk -> emitter.send(chunk),        // 每来一段：端一盘
                    emitter::completeWithError,          // 摔了：收盘
                    emitter::complete);                  // 完了：收工
    return emitter;
}
```

两个知识点：

1. **为什么每次 `new HuiManus`？** 机器人是**有状态**的（脑子里装着消息列表和状态机）——如果全班共用一个机器人，A 同学的任务和 B 同学的会混在一起（串台了）。所以每个客人发一套全新文具。对比：LoveApp 的 ChatClient 是无状态的，可以全班共用。
2. 两种方案（Flux / SseEmitter）殊途同归，都能做出打字机效果，按项目口味选一个即可。

```java
@GetMapping("/manus/chat")
public SseEmitter doChatWithManus(String message) {
    HuiManus huiManus = new HuiManus(allTools, dashscopeChatModel);  // ⚠️ 注意：每次 new 一个！
    return huiManus.runStream(message);    // 机器人边干边播报
}
```

### 💻 知识点③：给 BaseAgent 装上"播报喇叭"——runStream

阶段 8 的 `run()` 是闷头干完、一次性返回大字符串；`c377b50` 给 `BaseAgent` 加了 `runStream`（完整代码在附录），主干只多三件事：

```java
public SseEmitter runStream(String userPrompt) {
    SseEmitter emitter = new SseEmitter(300000L);          // ① 5 分钟超时的喇叭
    CompletableFuture.runAsync(() -> {                     // ② 换个线程干活，别堵住 HTTP 线程
        ...
        for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
            String stepResult = step();
            emitter.send("Step " + stepNumber + ": " + stepResult);   // ③ 每干完一步，立刻播报
        }
        emitter.complete();                                //    收工：告诉前端"没有了"
    });
    emitter.onTimeout(...);                                // 超时/完成的收尾回调
    emitter.onCompletion(...);
    return emitter;
}
```

拆解：`CompletableFuture.runAsync` 是关键——HTTP 请求线程把喇叭一挂就返回了，真正的"想-做循环"在另一个线程里跑，每一步的结果通过 `emitter.send()` 实时推给前端。

拿餐厅打比方：你点了一桌菜，**服务员（HTTP 线程）把小票挂进厨房（新线程）就回去接待下一位客人**；厨师（Agent 循环）每做好一道菜就从传菜口端出来一道（`emitter.send`）；全部上完喊一声"齐了"（`emitter.complete`）。反过来如果不开新线程（同步干等），服务员就得杵在厨房门口等到全部做完——**一个客人占死一个服务员**，来三个人店就瘫了。

### 💻 知识点④：跨域（CORS）——浏览器的门禁

浏览器有条"**同源策略**"：网址由**协议 + 域名 + 端口**三件套决定，三样全相同才叫"同源"。这条门禁规矩防的是"恶意网站偷偷替你访问别家网站的数据"。5173（前端）和 8123（后端）**端口不同 → 不同源 → 默认拦截**——这就是"跨域"问题的由来。

解法：新建 `config/CorsConfig.java`，由**后端主动贴告示**："5173 是自己人，放行"：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")          // ⚠️ 必须用 patterns！
                // .allowedOrigins("*")             // ← 开了 allowCredentials 再用这行会直接报错！
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
```

### 🧪 动手试试

先启动项目，再用两招各测一遍：

```bash
# ① 同步版：憋到最后一坨
curl "http://localhost:8123/api/ai/love_app/chat/sync?message=你好&chatId=test01"

# ② 流式版：-N 关掉缓冲，你会看到结果一段一段蹦出来
curl -N "http://localhost:8123/api/ai/love_app/chat/sse?message=你好&chatId=test01"

# ③ Manus 流式：给机器人派个真活
curl -N "http://localhost:8123/api/ai/manus/chat?message=把你好二字写入hello.txt文件"
```

也可以打开阶段 1 的 `http://localhost:8123/api/doc.html`，在新冒出来的 5 个接口里点"调试"（流式接口在 Swagger 里能看到逐段到达的响应）。

### 🕳️ 小心踩坑

- **CORS 一行报错**：开了 `allowCredentials(true)` 又用 `allowedOrigins("*")` → 启动/请求直接报错，必须换成 `allowedOriginPatterns("*")`（手册代码已是正确写法）；
- **curl 不加 `-N`** → 看起来"流式失效"（其实被 curl 自己缓冲了），是测量工具的锅；
- **`huiManus` 复用单例跑流式** → 第二个请求撞上 `Cannot run agent from state: RUNNING`；`/manus/chat` 必须每次 `new`；
- **SseEmitter 超时别设太短**：Manus 一整个任务可能跑几分钟（本项目设了 5 分钟），超时后前端只收到半截；
- 两个 `@GetMapping("/love_app/chat/sse")` 重载**不能同时完全生效**（同路径同方法，Spring 只认一个）——仓库里留着是教学对比，自己项目里二选一删掉另一个。

### 📦 附录：阶段 9 完整代码（照抄可跑）

目录变化：新增 `controller/AiController.java`、`config/CorsConfig.java`；`agent/huiManus.java` 改名为 `HuiManus.java`（顺手把测试类里的引用改掉）。

**1）AiController.java**（完整）：

```java
package com.hui.huiaiagent.controller;

import com.hui.huiaiagent.agent.HuiManus;
import com.hui.huiaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;
    @Autowired
    private HuiManus huiManus;

    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    @GetMapping(value = "/love_app/chat/sse")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppByServerEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }


    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        HuiManus huiManus = new HuiManus(allTools, dashscopeChatModel);
        return huiManus.runStream(message);
    }


}
```

**2）CorsConfig.java**（完整）：

```java
package com.hui.huiaiagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
```

**3）LoveApp 新增方法**：`doChatByStream`（见"实战代码①"，`import reactor.core.publisher.Flux;` 记得加）。

**4）BaseAgent 新增方法**：`runStream`（完整版如下，补进阶段 8 的 BaseAgent；`import java.util.concurrent.CompletableFuture;` 和 `SseEmitter` 的 import 也要加）：

```java
/**
 * 运行代理（流式输出）
 *
 * @param userPrompt 用户提示词
 * @return SseEmitter实例
 */
public SseEmitter runStream(String userPrompt) {
    // 创建SseEmitter，设置较长的超时时间
    SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

    // 使用线程异步处理，避免阻塞主线程
    CompletableFuture.runAsync(() -> {
        try {
            if (this.state != AgentState.IDLE) {
                emitter.send("错误：无法从状态运行代理: " + this.state);
                emitter.complete();
                return;
            }
            if (StringUtil.isBlank(userPrompt)) {
                emitter.send("错误：不能使用空提示词运行代理");
                emitter.complete();
                return;
            }

            // 更改状态
            state = AgentState.RUNNING;
            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));

            try {
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step " + stepNumber + "/" + maxSteps);

                    // 单步执行
                    String stepResult = step();
                    String result = "Step " + stepNumber + ": " + stepResult;

                    // 发送每一步的结果
                    emitter.send(result);
                }
                // 检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    emitter.send("执行结束: 达到最大步骤 (" + maxSteps + ")");
                }
                // 正常完成
                emitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("执行智能体失败", e);
                try {
                    emitter.send("执行错误: " + e.getMessage());
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            } finally {
                // 清理资源
                this.cleanup();
            }
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    });

    // 设置超时和完成回调
    emitter.onTimeout(() -> {
        this.state = AgentState.ERROR;
        this.cleanup();
        log.warn("SSE connection timed out");
    });

    emitter.onCompletion(() -> {
        if (this.state == AgentState.RUNNING) {
            this.state = AgentState.FINISHED;
        }
        this.cleanup();
        log.info("SSE connection completed");
    });

    return emitter;
}
```

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout c377b50        # ★ 阶段 9 终点：5 个接口 + CORS + runStream 全部就位
# 中间对照：git checkout 1419c4d   ← 只有 love_app 的流式接口，还没有 Manus 上网和跨域配置
# 在旧代码上跑（两个终端）：
#   终端① mvnw.cmd spring-boot:run
#   终端② curl -N "http://localhost:8123/api/ai/manus/chat?message=把你好二字写入hello.txt文件"
git checkout main           # 切回最新
```

---

## 阶段 10：Vue3 前端——接盘子的服务员（提交 `55c89f0`）

### 🎯 打个比方

后端的传送带（SSE）不断往外送苹果，前端要有个**服务员**盯着传送带，来一个接一个、随时摆上桌。整个前端就 4 个角色：**服务员（utils/sse.js）→ 点单传话（api/ai.js）→ 聊天室（ChatRoom.vue）→ 门牌导航（router）**。

### 🧾 第 0 步：前置准备

1. 装 Node.js（[nodejs.org](https://nodejs.org/)，LTS 版即可，自带 npm）——可以理解为"前端的 JDK"；
2. **后端先跑起来**（`mvnw.cmd spring-boot:run`——前端的所有数据都靠它）；
3. **前端代码不用写**——仓库 `hui-ai-agent-frontend/` 目录里是现成的，整个拷过来用（或直接在仓库里跑）。

### 🚀 第 1 步：把现成的前端跑起来（就 3 条命令）

```bash
cd hui-ai-agent-frontend   # 进入前端目录（代码现成）
npm install                # 按 package.json 清单下载全部依赖（几十秒到几分钟）
npm run dev                # 启动开发服务器
```

浏览器打开 `http://localhost:5173`——看到首页就成功了。依赖清单里就 3 个主角：`vue`（前端框架本体）、`vue-router`（页面导航）、`axios`（普通请求库）；构建工具是 Vite（[vitejs.dev](https://cn.vitejs.dev/)，改代码自动刷新页面）。

### 💻 知识点①：前端到底是什么（和后端什么关系）

**前端 = 跑在浏览器里的程序**（页面、按钮、动画归它管）；**后端 = 跑在你电脑/服务器上的 Java 程序**（调 AI、用工具、存记忆归它管）。拿餐厅打比方：**前端是前厅**（菜单、点菜、把菜端给客人看），**后端是后厨**（真正做菜）。客人从不进后厨——同样，浏览器从不运行 Java；两边靠"端菜"（HTTP 请求）联系。

### 💻 知识点②：总体架构一张图

```text
          你的浏览器                                你的电脑后台
┌────────────────────────────┐              ┌────────────────────────┐
│  前端（Vue3，端口 5173）      │              │  后端（Spring Boot，    │
│                            │    HTTP      │  端口 8123）            │
│  /        首页              │ ──────────→  │  /api/ai/love_app/...  │
│  /love    恋爱大师聊天        │ ←──────────  │  /api/ai/manus/chat    │
│  /manus   Manus 聊天         │  逐字流动     │        │               │
└────────────────────────────┘              │        ↓               │
                                            │  大模型 / 工具箱 / 记忆  │
                                            └────────────────────────┘
```

看懂三件事就够了：① 前端 5173、后端 8123，**各占一个端口互不打扰**；② 前端所有请求都以 `/api` 开头——正好对上阶段 1 定的 context-path；③ 回答是**逐字流动**着送回来的（阶段 9 的 SSE）。

### 💻 知识点③：目录结构地图（每个文件夹是干嘛的）

| 目录 / 文件 | 是什么 | 像餐厅里的 |
| --- | --- | --- |
| `src/views/` | 三个页面：首页、/love、/manus | 三张餐桌（各招待各的客人） |
| `src/components/ChatRoom.vue` | 通用聊天室组件，两个聊天页共用 | 一套桌椅（搬到哪桌都能用） |
| `src/api/ai.js` | 封装"点单"：消息发到哪个接口 | 服务员手里的点菜单 |
| `src/utils/sse.js` | 封装"收菜"：接住流动的回答 | 传菜口 |
| `src/router/index.js` | 导航：输 /love 就去 /love 页 | 门牌号 + 引位员 |
| `src/utils/uuid.js` | 生成 chatId（后端凭它认笔记本） | 桌号牌 |
| `package.json` | 依赖清单（npm install 按它下载） | 采购清单 |
| `vite.config.js` | 构建配置：端口、代理 | 店面规则 |

### 💻 知识点④：跨域双保险（vite.config.js 里的 proxy）

`vite.config.js` 里最值得看的一段：

```javascript
server: {
  port: 5173,
  proxy: {
    // 后端 context-path 为 /api，代理时保留前缀、不做 rewrite
    '/api': { target: 'http://localhost:8123', changeOrigin: true }
  }
}
```

拆解 `proxy`：开发时浏览器请求 5173 的 `/api/...`，Vite 偷偷转发给 8123 的后端——**这是跨域的第二种解法**（阶段 9 的 CorsConfig 是后端放行，这个是前端绕路），双保险。

### 💻 知识点⑤：一条消息的完整旅程（把整条链走一遍）

你在 /love 页面输入"你好"点发送，幕后依次发生：

```text
① ChatRoom（页面）：把"你好"显示到屏幕，生成/沿用本次会话的 chatId
② api/ai.js：拼出完整地址
   http://localhost:8123/api/ai/love_app/chat/sse?message=你好&chatId=xxx
③ utils/sse.js：用 fetch 发起请求，然后守着"响应流"一块块读
④ 后端 AiController → LoveApp.doChatByStream：调大模型，一个字一个字往回送
⑤ sse.js 每收到一小段，就回调 onMessage(那一小段)
⑥ ChatRoom 把它拼到当前消息尾部 → Vue 发现数据变了，自动重画页面（打字机效果）
⑦ 流结束触发 onDone → 输入框解锁，等下一句
```

下面两个知识点，就是把这条链上的"收菜"（sse.js）和"点单"（ai.js）掰开看。

### 💻 知识点⑥：服务员的核心动作（utils/sse.js）

```javascript
const reader = response.body.getReader()       // ① 站到传送带边上
const decoder = new TextDecoder('utf-8')       // ② 学会认字（字节→文字）
let buffer = ''                                // ③ 手里的小托盘

while (true) {
    const { done, value } = await reader.read()  // ④ 等下一批苹果
    if (done) break                              // ⑤ 传送带停了，收工
    buffer += decoder.decode(value, { stream: true })

    const events = buffer.split('\n\n')   // ⑥ 按节掰甘蔗（SSE 用空行分隔事件）
    buffer = events.pop()                 // ⑦ 最后一节可能没掰完 → 留在托盘里等下一批！

    for (const event of events) {
        const data = parseEventData(event)   // 只挑出 data: 行的内容
        if (data !== null) onMessage(data)   // ⑧ 完整的苹果 → 端上桌
    }
}
```

第 ⑥⑦ 行是精髓：传送带送货不保证一次送一个整的，可能半截半截地来——**按 `\n\n` 切开后，最后一段不确定是不是完整的，先留在托盘（buffer）里**，等下一批到货拼上再判断。这个套路是所有流式解析的通用范式，值得背下来。

**为什么不用浏览器自带的 `EventSource`？** 它像个只会端盘、不会说话的服务员——感知不到"传送带正常结束"，也没法中途喊停（做不了"停止生成"按钮）。所以这里用 `fetch` + `signal`（AbortController）手搓。

### 💻 知识点⑦：点单传话——api/ai.js 的两个细心设计

```javascript
// 根据环境变量设置 API 基础 URL
const BASE_URL = process.env.NODE_ENV === 'production'
    ? '/api'                              // 生产：同一栋楼，走相对路径
    : 'http://localhost:8123/api'         // 开发：直连后端（所以后端才需要 CORS！）
```

设计一：**恋爱大师接口的 data 是"半个字"**——逐 token 输出，前端拿到就往消息尾巴上**直接拼接**；设计二：**Manus 接口的 data 是"一整步"**——每段是完整的 `Step N: ...`，直接拼会粘连成一大坨，所以事件之间**垫一个空行**：

```javascript
// chatWithManus 的包装：第一段原样给，之后每段前面垫 \n\n
handlers.onMessage(firstEvent ? data : `\n\n${data}`)
```

这就是前端两个页面体验不同的根源：恋爱大师**一个字一个字**往外蹦，Manus **一整步一整步**播报——不是两套前端技术，只是拼接策略不同。

### 💻 知识点⑧：打字机效果——其实没有魔法

```javascript
// ChatRoom 组件里（示意）：每来一个 chunk，就追加到当前消息后面
currentMessageText += chunk     // Vue 发现数据变了 → 自动重新画页面
```

每收到一小段文字就往消息尾巴上拼，Vue 的响应式系统自动刷新画面——"打字机"就是这么做出来的。另一个细节：AI 回答常带 Markdown（`**加粗**`、代码块），`utils/markdown.js` 负责把流式中的半截 Markdown 也能安全渲染。

### 🧪 动手试试

```bash
cd hui-ai-agent-frontend
npm install && npm run dev
```

打开 `http://localhost:5173/love` 和 `/manus` 对比：恋爱大师逐字蹦，Manus 逐步播报；再问一句恋爱大师"用 Markdown 列三条建议"，看格式渲染。

**挑战题**：给聊天室加个"停止生成"按钮（提示：`AbortController`，sse.js 已经留好了 `signal` 参数）。

### 🕳️ 小心踩坑

- **后端没启动 / 地址写错** → 页面能开但发消息一直转圈（F12 控制台一堆红色 `net::ERR_CONNECTION_REFUSED`）；
- **跨域报错**（F12 看到 CORS 字样）→ 检查后端 CorsConfig 在不在 + BASE_URL 端口对不对；
- **`process is not defined`** → 在纯浏览器代码里用了 `process.env.NODE_ENV` 之外的 Node 变量；本项目只在 ai.js 里用这一个，照抄即可；
- 恋爱大师接口若忘了传 `chatId` → 每句话都是新会话，AI"失忆"（后端靠 chatId 隔离记忆）；
- `npm run dev` 端口被占 → 改 vite.config.js 的 `server.port`。

### 📦 附录：代码在哪、想深入怎么读

- **代码现成**：`hui-ai-agent-frontend/` 整个目录就是全部前端（独立于 Java 工程），无需手写一行——拷贝下来 `npm install && npm run dev` 即可用。
- **想读懂它**，按由易到难的顺序啃三个文件：`src/utils/sse.js`（70 行，怎么收流）→ `src/api/ai.js`（51 行，怎么发请求）→ `src/components/ChatRoom.vue`（702 行，怎么渲染界面）。读 `ChatRoom.vue` 抓三条主线就够：
  1. 页面通过 `send-function` 属性告诉聊天室"用哪个接口"——**聊天室本身不知道后端是谁**（组件复用的关键）；
  2. 每次新会话 `generateId()` 生成 chatId，随每条消息传给后端；
  3. `onMessage / onError / onDone / signal` 四个回调驱动界面：追加文字（打字机）、显示错误、解锁输入框、支持"停止生成"。
- 前端知识入门：Vue3 官方文档 [cn.vuejs.org](https://cn.vuejs.org/)、Vite [vitejs.dev](https://cn.vitejs.dev/)、MDN 的 [使用服务器发送事件](https://developer.mozilla.org/zh-CN/docs/Web/API/Server-sent_events/Using_server-sent_events)。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 55c89f0        # ★ 阶段 10 终点：前端工程整体进仓库（hui-ai-agent-frontend/）
# 在旧代码上跑（两个终端）：
#   终端① mvnw.cmd spring-boot:run                      ← 后端先起
#   终端② cd hui-ai-agent-frontend && npm install && npm run dev
# 浏览器打开 http://localhost:5173/love 和 /manus
git checkout main           # 切回最新
```

（node_modules 没被 git 跟踪、切走也不受影响——但第一次到这个提交时需要重新 `npm install`，因为 package-lock 记录的依赖要现装。）

---

## 阶段 11：部署上线——搬家与藏好钥匙（提交 `7fa8fdf`、`8a317ff`）

### 🎯 打个比方

要搬家（部署）了，两件大事：

1. **行李要轻**：厨房（Maven）里把菜做好打包，路上只带做好的菜（jar 包），不把整个厨房搬走——多阶段构建；
2. **钥匙要藏好**：家门钥匙（API Key）**绝对不能**和照片一起发朋友圈（提交进 git / 打进镜像）——三层配置隔离。

本阶段两次提交分工：`7fa8fdf` 后端上线的三件套（Dockerfile + application-prod.yml + .dockerignore 调整）；`8a317ff` 前端上线的三件套（前端 Dockerfile + nginx.conf + 根 .dockerignore）。作者实际部署到了 Serverless 平台（腾讯云开发 CloudBase，nginx 配置里的后端地址就是它）。

### 💻 知识点⓪：Docker 是什么（三分钟扫盲）

新手最常听到的噩梦是"**在我电脑上明明能跑啊**"——因为你机器上有 JDK 21、有 Maven、有一堆配置，换台电脑就缺这少那。Docker 的解法粗暴有效：**把应用连同它的整个运行环境打包成一个"集装箱"**，任何装了 Docker 的电脑都能原样把它跑起来。

记住两个词、三条命令，就算入门了：

| 词 | 是什么 | 像生活里的 |
| --- | --- | --- |
| **镜像（image）** | 打包好的一整套"程序 + 环境"，一个静态文件 | 装机盘 / 预制菜料理包 |
| **容器（container）** | 把镜像"开机"跑起来的实例 | 把料理包下锅的那顿饭 |

```bash
docker build -t 名字 .            # 打包：照着 Dockerfile 菜谱做成镜像
docker run -p 8123:8123 名字      # 开机：镜像变容器，跑起来
docker ps                         # 看看现在哪些容器在跑
```

本阶段的 Dockerfile，就是写给 Docker 看的"打包菜谱"——下面正式开工。

### 🧾 第 0 步：前置准备

1. 装 Docker Desktop（[docker.com](https://www.docker.com/)，Windows 用 WSL2 后端）；
2. 准备一个部署平台：作者用的是[腾讯云开发 CloudBase](https://tcb.cloud.tencent.com/) 的"云托管/Serverless"，阿里云 SAE、Render 等同类平台思路一样；
3. 备好三把真实钥匙：DashScope Key、SearchAPI Key、数据库密码（马上用环境变量注入）。

### 📦 第 1 步：后端三件套（提交 `7fa8fdf`）

在**项目根目录**新建三个文件（完整内容在附录）：

| 文件 | 作用 |
| --- | --- |
| `Dockerfile` | 两站式打包：第一站 Maven 做 jar，第二站只带 JRE + jar 上路 |
| `src/main/resources/application-prod.yml` | 生产环境专属配置（非敏感项 + 日志降级），跟着 jar 进 git |
| `.dockerignore` | **Docker 版的 .gitignore**：`COPY src ./src` 不看 .gitignore，必须在这里再挡一层，把 `application-local.yml` 挡在镜像外 |

### 📦 第 2 步：前端三件套（提交 `8a317ff`）

在 `hui-ai-agent-frontend/` 下新建：

| 文件 | 作用 |
| --- | --- |
| `Dockerfile` | 两站式：node 阶段 `npm run build` 出静态文件，nginx 阶段托管它们 |
| `nginx.conf` | 4 件事：托管静态页、Vue 路由兜底、**把 /api 反向代理到后端**、SSE 专项配置 |
| （根目录 `.dockerignore` 一并补齐） | 挡 `node_modules`、`dist` 等，镜像不背废料 |

### 💻 知识点①：后端 Dockerfile 两站式打包

```dockerfile
# ---- 第一站：厨房里做菜（打包）----
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests      # 做出 jar 这盘"菜"

# ---- 第二站：只带成品上路 ----
FROM amazoncorretto:21                 # 干净的新家（只有 JRE，没有源码和 Maven）
WORKDIR /app
COPY --from=builder /app/target/hui-ai-agent-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8123
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar",
            "--spring.profiles.active=prod"]
```

拆解：第一站的 Maven、源码统统不带进最终镜像——**镜像从 1GB+ 瘦到几百 MB**，Serverless 冷启动快得多。`--spring.profiles.active=prod` 激活 application-prod.yml。`MaxRAMPercentage=75.0` 让 JVM 堆按"房子实际多大"自动调整，住小房子（Serverless 内存限额）不撑爆。

### 💻 知识点②：三层配置隔离（最值得抄走的思想）

| 层 | 文件 | 放什么 | 进不进 git / 镜像 |
| --- | --- | --- | --- |
| ① 公开配置 | `application.yml` + `application-prod.yml` | 端口、开关、日志级别等非敏感信息 | ✅ 都进 |
| ② 本地密钥 | `application-local.yml` | 你的 API Key、数据库密码 | ❌ 被 `.gitignore` + `.dockerignore` 双重拦截 |
| ③ 生产密钥 | 环境变量 | 服务器上的真实钥匙 | 部署平台注入，**优先级最高** |

生产环境用环境变量发钥匙（名字规则：**点换成下划线、全大写**——`application-prod.yml` 头部注释里列了全部映射）：

```bash
docker run -e SPRING_AI_DASHSCOPE_API_KEY=sk-xxx \
           -e SEARCH_API_API_KEY=xxx \
           -e SPRING_DATASOURCE_PASSWORD=xxx \
           -p 8123:8123 hui-ai-agent
```

环境变量的优先级**高于 jar 包里所有配置文件**——所以同一个 jar，插上不同的钥匙就能在任何地方开跑。

### 💻 知识点③：前端 Dockerfile 与 nginx 的三处关键

前端镜像 = 静态文件 + nginx（[nginx.org](https://nginx.org/)）。`nginx.conf` 里最值得看的三段：

```nginx
# ① Vue 路由兜底：用户直接访问 /love 时服务器上并没有这个文件，
#    一律回 index.html，交给前端路由去画页面（否则 404）
location / {
    try_files $uri $uri/ /index.html;
}

# ② API 反向代理：前端同域的 /api 请求转发给后端容器
#    （把 proxy_pass 换成你自己的后端地址——作者的是腾讯云 Serverless 域名）
location ^~ /api/ {
    proxy_pass https://你的后端地址/api/;
}

# ③ SSE 专项配置：不开这三样，打字机效果会被 nginx"攒一坨再发"
proxy_set_header Connection "";   # 保持连接
proxy_buffering off;              # 关缓冲：来一段转一段
proxy_read_timeout 600s;          # Manus 任务长，读超时给足
```

**③ 是本项目踩过的真坑**：nginx 默认会把响应攒够一缓冲区再发——流式接口穿过默认配置的 nginx 后，"打字机"变成"憋十秒蹦一屏"。`proxy_buffering off` 一行救命。

### 🧭 从零到上线的总清单（照着打勾）

```text
□ 1. 装好 Docker Desktop
□ 2. 写好后端三件套（Dockerfile / application-prod.yml / .dockerignore）
□ 3. docker build -t hui-ai-agent .          ← 打包后端镜像
□ 4. docker run -p 8123:8123 -e SPRING_AI_DASHSCOPE_API_KEY=你的key hui-ai-agent
□ 5. curl http://localhost:8123/api/health    ← 验证后端活着
□ 6. 写好前端三件套（前端 Dockerfile / nginx.conf / .dockerignore）
□ 7. 把 nginx.conf 里的 proxy_pass 换成你的后端真实地址
□ 8. cd hui-ai-agent-frontend && docker build -t hui-ai-agent-frontend .
□ 9. docker run -p 80:80 hui-ai-agent-frontend   ← 浏览器打开 http://localhost 试聊
□ 10.（真上线）把两个镜像推到 Serverless 平台，控制台填好环境变量钥匙
```

本地把 1~9 步走通，就等于**在自己电脑上完整模拟了一次上线**；第 10 步才是真把家搬到云上。

### 🧪 动手试试

```bash
# 后端本地验证（prod 档 + 环境变量钥匙）
docker build -t hui-ai-agent .
docker run -p 8123:8123 -e SPRING_AI_DASHSCOPE_API_KEY=你的key hui-ai-agent
curl http://localhost:8123/api/health

# 前端本地验证（记得先把 nginx.conf 里的后端地址改成你的）
cd hui-ai-agent-frontend
docker build -t hui-ai-agent-frontend .
docker run -p 80:80 hui-ai-agent-frontend
# 浏览器打开 http://localhost ，点点页面、发条消息
```

（没装 Docker 的话，看懂 Dockerfile / nginx.conf 每一行注释即可。真要上线：把两个镜像推到 Serverless 平台、在平台控制台填好环境变量钥匙，就大功告成。）

### 🕳️ 小心踩坑

- **`.dockerignore` 忘了建** → `COPY src ./src` 会把 `application-local.yml` 连同真实钥匙打进镜像（.gitignore 管不到 Docker）；
- **打字机变"憋一屏"** → nginx 没关 `proxy_buffering`（或中间还有别的代理层在缓冲）；
- **改了 pom 的版本号** → Dockerfile 里 `COPY --from=builder .../hui-ai-agent-0.0.1-SNAPSHOT.jar` 的 jar 名要同步改；
- **Vue 路由 404** → nginx 没配 `try_files ... /index.html`；
- **上线前记得关门**：`application-prod.yml` 里文档接口是验证期临时开着的（`knife4j.production: false`），正式对外前按文件注释改回关闭，别把接口文档裸奔在公网。

### 📦 附录：阶段 11 完整代码（照抄可用）

**1）后端 `Dockerfile`**（完整）：

```dockerfile
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
```

**2）后端 `src/main/resources/application-prod.yml`**（完整）：

```yaml
# ============================================================
# 生产环境配置：只放非敏感配置项，此文件会提交到 git
# 真实密钥（DashScope / SearchAPI / 数据库密码）严禁写在这里，
# 一律在部署平台用环境变量注入：
#   spring.ai.dashscope.api-key    -> SPRING_AI_DASHSCOPE_API_KEY
#   search-api.api-key             -> SEARCH_API_API_KEY
#   spring.datasource.url          -> SPRING_DATASOURCE_URL
#   spring.datasource.username     -> SPRING_DATASOURCE_USERNAME
#   spring.datasource.password     -> SPRING_DATASOURCE_PASSWORD
# （环境变量优先级高于 jar 内所有 application*.yml，会自动覆盖）
# ============================================================

 # 验证服务启动时临时开启接口文档；确认能访问后，上线前改回
 # springdoc.api-docs.enabled: false 和 knife4j.production: true 关闭文档
springdoc:
  api-docs:
    enabled: true
knife4j:
  production: false

# 生产环境降低 AI 调用链路的日志级别，避免刷屏
logging:
  level:
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: info
```

**3）根目录 `.dockerignore`**（完整）：

```text
# Docker COPY 不看 .gitignore，必须在这里再挡一层
target/
.git/
.idea/
chat-memory/
tmp/

# 本地真实密钥配置，严禁打进镜像（密钥通过环境变量注入）
src/main/resources/application-local.yml
```

**4）前端 `hui-ai-agent-frontend/Dockerfile`**（完整）：

```dockerfile
# 前端构建阶段
FROM node:20-alpine AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# 运行阶段 - 使用 nginx 托管静态文件
FROM nginx:alpine
# 复制构建产物到 nginx 静态文件目录
COPY --from=build /app/dist /usr/share/nginx/html
# 复制自定义 nginx 配置替换默认配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
# 暴露端口
EXPOSE 80
# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
```

**5）前端 `hui-ai-agent-frontend/nginx.conf`**（完整；`proxy_pass`/`Host` 里的域名**换成你自己的后端地址**）：

```nginx
server {
    listen       80;
    server_name  localhost;

    # 前端静态文件根目录
    root   /usr/share/nginx/html;

    # 所有HTML请求都返回index.html（解决Vue路由的404问题）
    location / {
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    # API请求反向代理到指定后端
    location ^~ /api/ {
        # 指定后端地址
        proxy_pass https://hui-ai-agent-backend-107890-8-1326669019.sh.run.tcloudbase.com/api/;

        # 设置请求头
        proxy_set_header Host hui-ai-agent-backend-107890-8-1326669019.sh.run.tcloudbase.com;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # SSE (Server-Sent Events)配置
        proxy_set_header Connection ""; # 保持连接打开
        proxy_http_version 1.1;
        proxy_buffering off;
        proxy_cache off;
        chunked_transfer_encoding off;
        proxy_read_timeout 600s;

        # 增加错误调试信息
        proxy_intercept_errors off;
    }

    # 处理静态资源请求
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        access_log off;
        add_header Cache-Control "public";
    }

    # 处理错误页
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

**6）前端 `.dockerignore`**（`hui-ai-agent-frontend/.dockerignore`，从仓库拷贝，主要挡 `node_modules`、`dist`、`gui-test-screenshots` 等）。

### ⏪ 切到本阶段的原始代码（对照学习）

```bash
git checkout 8a317ff        # ★ 阶段 11 终点：前后端部署三件套全部就位（本手册的终点站）
# 中间对照：git checkout 7fa8fdf   ← 只有后端部署三件套（Dockerfile / prod.yml / .dockerignore）
# 在旧代码上跑：照"从零到上线的总清单"走一遍（docker build / docker run / curl 验证）
git checkout main           # 切回最新——恭喜，你已经把整个项目的历史走完了 🎉
```

---

## 学完之后：过关检查 🎓

全部能用自己的话讲出来，就算毕业：

1. ChatModel 和 ChatClient 有什么不一样？为什么开发应用要用后者？
2. 系统提示词的四个要点是什么？给 AI 立"数学老师"人设你会怎么写？
3. 想拦截含违禁词的消息，Advisor 里关键是不调用哪个方法？
4. `chatId` 在对话记忆里起什么作用？三个笔记本各适合什么场合？
5. RAG 三步曲是哪三步？"给卡片贴 status 标签"之后多了什么新玩法？
6. 查询重写解决什么问题？为什么默认的英文模板不行？
7. `@Tool` 的 description 为什么要认真写？AI 是怎么"用"工具的（谁在真正执行）？
8. Manus 的两道保险是什么？`withProxyToolCalls(true)` 拿回了什么控制权？
9. 为什么 `/manus/chat` 每次要 `new HuiManus()`，而 LoveApp 的 chatClient 可以共用？
10. 前端解析 SSE 时，为什么切完 `\n\n` 要把最后一段留在 buffer 里？
11. 生产环境的 API Key 应该放哪？为什么不能提交进 git？

### 接下来玩什么（进阶方向）

- **给 Manus 加记忆**：把它的消息列表接到 MySQL，让它跨会话记住任务；
- **加第五种记忆**：写一个 `RedisChatMemory`，其实只要实现 3 个方法；
- **换本地模型跑 Manus**：接入 Ollama，对比和千问的效果差距；
- **给自己的知识库做 RAG**：把你的笔记变成 Markdown 放进 `resources/document/`；
- **评测**：给 RAG 写个"命中率"统计，别靠感觉。

---

> 📖 环境搭建、密钥申请等实操信息见 [README.md](../README.md)；每个阶段的视频讲解见 [video/](../video/README.md)。
> 📄 学完想写简历、准备面试？见 [RESUME.md](./RESUME.md)（简历写法 + 14 条按公式写好的条目）和 [INTERVIEW.md](./INTERVIEW.md)（面试官视角 30 题自检，逐条对应简历）。
> 最后一招学习心法：**每读完一个阶段，先 `git checkout` 到对应提交跑一遍，再回到最新代码看它后来长成了什么样**——看着代码"长大"，是读项目最快的方式。
