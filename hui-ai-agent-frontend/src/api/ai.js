import request from './request'
import { fetchSSE } from '@/utils/sse'

//const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

// 根据环境变量设置 API 基础 URL
const BASE_URL = process.env.NODE_ENV === 'production'
    ? '/api' // 生产环境使用相对路径，适用于前后端部署在同一域名下
    : 'http://localhost:8123/api' // 开发环境指向本地后端服务

/**
 * AI 恋爱大师 —— SSE 流式聊天
 * 后端接口：GET /ai/love_app/chat/sse（Flux<String>，按 chatId 隔离会话记忆）
 *
 * 注意：该接口逐 token 输出，data 之间必须直接拼接，不能加分隔符
 *
 * @param {string} message 用户消息
 * @param {string} chatId 会话 ID
 * @param {Object} handlers 见 fetchSSE（onMessage / onError / onDone / signal）
 */
export function chatWithLoveAppSse(message, chatId, handlers = {}) {
  const params = new URLSearchParams({ message, chatId })
  return fetchSSE(`${BASE_URL}/ai/love_app/chat/sse?${params.toString()}`, handlers)
}

/**
 * AI 超级智能体（Manus）—— SSE 流式聊天
 * 后端接口：GET /ai/manus/chat（SseEmitter，每个事件是一次完整的步骤输出）
 *
 * 与恋爱大师不同：每个 data 是完整的一行步骤结果，
 * 直接拼接会导致 "Step 1: ...Step 2: ..." 粘连，因此事件间用空行分隔
 *
 * @param {string} message 用户消息
 * @param {Object} handlers 见 fetchSSE（onMessage / onError / onDone / signal）
 */
export function chatWithManus(message, handlers = {}) {
  const params = new URLSearchParams({ message })
  let firstEvent = true
  const wrapped = {
    ...handlers,
    onMessage:
      handlers.onMessage &&
      ((data) => {
        handlers.onMessage(firstEvent ? data : `\n\n${data}`)
        firstEvent = false
      })
  }
  return fetchSSE(`${BASE_URL}/ai/manus/chat?${params.toString()}`, wrapped)
}

export default request
