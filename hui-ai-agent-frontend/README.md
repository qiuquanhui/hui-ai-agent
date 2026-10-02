# Hui AI Agent 前端

基于 **Vue 3 + Vite + Vue Router + Axios** 的 AI 应用前端，包含：

- **主页**：应用入口，切换不同 AI 应用
- **AI 恋爱大师**：聊天室风格，SSE 流式输出（接口 `/api/ai/love_app/chat/sse`）
- **AI 超级智能体（Manus）**：聊天室风格，SSE 流式输出（接口 `/api/ai/manus/chat`）

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev
```

> 需要先启动后端服务（`http://localhost:8123`），开发环境通过 Vite 代理 `/api` 前缀转发请求，无需处理跨域。

## 结构说明

```
src/
├── api/            # axios 实例与 SSE 聊天接口封装
├── utils/          # SSE 流式解析工具、chatId 生成
├── router/         # 路由（/ 主页、/love 恋爱大师、/manus 超级智能体）
├── components/     # ChatRoom 通用聊天室组件
└── views/          # 三个页面视图
```

## 其它

- 构建生产包：`npm run build`；预览：`npm run preview`
- 后端地址可通过环境变量 `VITE_API_BASE_URL` 覆盖（默认走 `/api` 代理）
