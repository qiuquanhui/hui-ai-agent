# STUDY.md 配套讲解视频

本目录存放 [STUDY.md](../study/STUDY.md) 十二个阶段的配套讲解视频，每个阶段一支，按学习顺序观看即可走完「从 0 到 1」全流程。

- 规格：1280×720 / MP4 / 中文旁白（AI 配音 edge-tts · 云希）
- 形式：幻灯片要点 + 语音讲解，每支视频对应 STUDY.md 的一个阶段

## 视频清单

| 视频 | 时长 | 阶段主题 | 对应 STUDY.md | 对应提交 |
| --- | --- | --- | --- | --- |
| `stage-00-intro.mp4` | 1′51″ | 前置知识：项目介绍与核心概念 | 阶段 0 | - |
| `stage-01-skeleton.mp4` | 1′15″ | 项目骨架：Spring Boot 工程 | 阶段 1 | `83d1208` |
| `stage-02-invoke.mp4` | 1′43″ | 调用大模型的 5 种方式 | 阶段 2 | `47e38f6` `1dbdd0b` |
| `stage-03-chatclient.mp4` | 1′45″ | ChatClient 与 Advisor（恋爱大师诞生） | 阶段 3 | `5259d50` |
| `stage-04-advisor.mp4` | 1′35″ | 自定义 Advisor（日志/权限/违禁词） | 阶段 4 | `513c3e4` `bcab20f` |
| `stage-05-memory.mp4` | 1′37″ | 对话记忆的三种实现 | 阶段 5 | `1913ab5` `b92588a` |
| `stage-06-rag.mp4` | 1′56″ | RAG 检索增强（知识库进化史） | 阶段 6 | `3c8d159`…`77a34fc` |
| `stage-07-tools.mp4` | 1′44″ | 工具调用 Tool Call（7 个工具） | 阶段 7 | `abe317c`…`8fcf40d` |
| `stage-08-manus.mp4` | 2′13″ | 手写 Manus 智能体（ReAct 循环）⭐ | 阶段 8 | `bd434b1` |
| `stage-09-api.mp4` | 1′23″ | 接口服务化与流式输出 | 阶段 9 | `1419c4d` `c377b50` |
| `stage-10-frontend.mp4` | 1′42″ | Vue3 前端与 SSE 消费 | 阶段 10 | `55c89f0` |
| `stage-11-deploy.mp4` | 1′28″ | 部署上线与密钥安全（结业） | 阶段 11 | `7fa8fdf` `8a317ff` |

合计约 **20 分钟**，单支 1~2 分钟，适合每学完一个阶段看对应的视频复盘。

## 如何修改与重新生成

所有课件内容集中在 `scripts/content.py`（每页幻灯片的标题、要点、旁白），修改后重新生成：

```bash
# 重新生成某个阶段（改了 content.py 里对应阶段后）
python scripts/build.py --stage 3

# 重新生成全部
python scripts/build.py --all

# 强制忽略缓存（改了 build.py 的画面样式后）
python scripts/build.py --all --force
```

依赖：Python 3.10+、Pillow、edge-tts（`pip install pillow edge-tts`）、ffmpeg（需在 PATH 中）。

## 目录说明

```
video/
├── stage-*.mp4        # 最终视频（12 支）
├── scripts/
│   ├── content.py     # 课件内容（想改讲什么 → 改这里）
│   └── build.py       # 渲染 + TTS + 合成流水线（想改画面 → 改这里）
└── .work/             # 中间产物（幻灯片 PNG、旁白 MP3、分页片段），可整目录删除
```
