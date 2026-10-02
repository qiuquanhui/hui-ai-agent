<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { generateId } from '@/utils/uuid'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps({
  /** 主题：love（恋爱大师）| manus（超级智能体） */
  theme: { type: String, default: 'love' },
  /** 应用标题与副标题 */
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  /** 头像 emoji */
  aiAvatar: { type: String, default: '🤖' },
  userAvatar: { type: String, default: '🙂' },
  /** 是否生成并展示会话 ID（恋爱大师接口依赖 chatId 区分会话记忆） */
  useChatId: { type: Boolean, default: false },
  /** 输入框占位文案 */
  placeholder: { type: String, default: '输入消息，按 Enter 发送...' },
  /** 空会话时的欢迎文案 */
  welcomeTitle: { type: String, default: '开始新的对话吧' },
  welcomeText: { type: String, default: '' },
  /**
   * 发送函数：(message, chatId, handlers) => Promise
   * 由各页面注入对应的 SSE 接口调用，handlers 见 utils/sse.js
   */
  sendFunction: { type: Function, required: true }
})

const router = useRouter()

const messages = ref([])
const input = ref('')
const streaming = ref(false)
const chatId = ref('')
/** 标记本次请求是否为用户主动停止，供 onDone 收尾时判断 */
const stopRequested = ref(false)

const messageArea = ref(null)
const inputRef = ref(null)
let abortController = null

onMounted(() => {
  chatId.value = generateId()
})

const shortChatId = computed(() => chatId.value.slice(0, 8))

/** 发送消息：先展示用户消息，再以流式方式填充 AI 回复 */
async function handleSend() {
  const message = input.value.trim()
  if (!message || streaming.value) return

  input.value = ''
  resetTextareaHeight()
  messages.value.push({ id: generateId(), role: 'user', content: message, done: true })

  // 响应式消息对象：流式追加 content 时才会触发视图更新
  const aiMsg = reactive({
    id: generateId(),
    role: 'assistant',
    content: '',
    done: false,
    isError: false
  })
  messages.value.push(aiMsg)

  streaming.value = true
  stopRequested.value = false
  abortController = new AbortController()
  scrollToBottom()

  const finish = () => {
    aiMsg.done = true
    streaming.value = false
    abortController = null
  }

  await props.sendFunction(message, chatId.value, {
    signal: abortController.signal,
    onMessage: (chunk) => {
      aiMsg.content += chunk
      scrollToBottom()
    },
    onError: (err) => {
      aiMsg.isError = true
      aiMsg.content += (aiMsg.content ? '\n' : '') + `⚠ 请求出错：${err.message || err}`
      finish()
    },
    onDone: () => {
      if (stopRequested.value && !aiMsg.content) {
        aiMsg.content = '（已停止生成）'
      }
      finish()
    }
  }).catch(() => {
    // fetchSSE 内部已通过 onError 回调处理异常，这里兜底复位状态
    if (streaming.value) finish()
  })
}

/** 停止生成：中断 SSE 请求，fetchSSE 捕获 AbortError 后以 onDone 收尾 */
function stopGenerating() {
  if (!streaming.value || !abortController) return
  stopRequested.value = true
  abortController.abort()
}

/** 新对话：中断进行中的请求，重新生成会话 ID 并清空聊天记录 */
function newConversation() {
  if (streaming.value) {
    stopRequested.value = true
    abortController?.abort()
  }
  chatId.value = generateId()
  messages.value = []
}

function copyChatId() {
  if (!chatId.value) return
  try {
    navigator.clipboard?.writeText(chatId.value)
  } catch {
    /* 剪贴板不可用时静默忽略 */
  }
}

function autoResize() {
  const el = inputRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${Math.min(el.scrollHeight, 120)}px`
}

function resetTextareaHeight() {
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
}

async function scrollToBottom() {
  await nextTick()
  const el = messageArea.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}
</script>

<template>
  <div class="chat-room" :class="`chat-room--${theme}`">
    <!-- 顶栏 -->
    <header class="chat-header">
      <button class="back-btn" title="返回主页" @click="router.push('/')">←</button>
      <div class="header-info">
        <span class="app-icon">{{ aiAvatar }}</span>
        <div class="header-text">
          <h1 class="app-title">{{ title }}</h1>
          <p class="app-subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="header-actions">
        <span
          v-if="useChatId"
          class="chat-id-badge"
          :title="`会话 ID：${chatId}（点击复制）`"
          @click="copyChatId"
        >
          # {{ shortChatId }}
        </span>
        <button class="new-chat-btn" @click="newConversation">＋ 新对话</button>
      </div>
    </header>

    <!-- 消息区 -->
    <main ref="messageArea" class="message-area">
      <div class="message-list">
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-icon">{{ aiAvatar }}</div>
          <h2 class="empty-title">{{ welcomeTitle }}</h2>
          <p class="empty-text">{{ welcomeText }}</p>
        </div>

        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-row"
          :class="msg.role"
        >
          <div class="avatar" :class="msg.role">
            {{ msg.role === 'user' ? userAvatar : aiAvatar }}
          </div>
          <div class="bubble" :class="{ error: msg.isError }">
            <!-- 用户消息为纯文本；AI 回复按 Markdown 渲染（渲染器内部已转义，可安全 v-html） -->
            <span v-if="msg.role === 'user'" class="bubble-text">{{ msg.content }}</span>
            <div v-else class="bubble-content" v-html="renderMarkdown(msg.content)"></div>
            <span v-if="msg.role === 'assistant' && !msg.done && msg.content" class="cursor">▌</span>
            <span
              v-if="msg.role === 'assistant' && !msg.done && !msg.content"
              class="typing-dots"
            >
              <i></i><i></i><i></i>
            </span>
          </div>
        </div>
      </div>
    </main>

    <!-- 输入区 -->
    <footer class="input-area">
      <div class="input-box">
        <textarea
          ref="inputRef"
          v-model="input"
          rows="1"
          :placeholder="placeholder"
          :disabled="streaming"
          @input="autoResize"
          @keydown.enter.exact.prevent="handleSend"
        ></textarea>
        <button
          v-if="!streaming"
          class="send-btn"
          :disabled="!input.trim()"
          @click="handleSend"
        >
          发送
        </button>
        <button v-else class="stop-btn" @click="stopGenerating">■ 停止</button>
      </div>
      <p class="input-hint">Enter 发送 · Shift + Enter 换行</p>
    </footer>
  </div>
</template>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg);
}

/* ---------- 主题变量 ---------- */
.chat-room--love {
  --accent: #e11d48;
  --accent-grad: linear-gradient(135deg, #f43f5e, #fb7185);
  --bg: #fff5f6;
  --bg-strong: #ffe4e6;
  --bubble-border: #fecdd3;
}

.chat-room--manus {
  --accent: #4f46e5;
  --accent-grad: linear-gradient(135deg, #6366f1, #8b5cf6);
  --bg: #f5f6ff;
  --bg-strong: #e0e7ff;
  --bubble-border: #c7d2fe;
}

/* ---------- 顶栏 ---------- */
.chat-header {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 64px;
  padding: 0 18px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}

.back-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--bg-strong);
  color: var(--accent);
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.15s;
}

.back-btn:hover {
  transform: translateX(-2px);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1;
}

.app-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: var(--accent-grad);
  font-size: 20px;
  flex-shrink: 0;
}

.header-text {
  min-width: 0;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-subtitle {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.chat-id-badge {
  font-size: 12px;
  font-family: Consolas, Monaco, monospace;
  color: #6b7280;
  background: var(--bg-strong);
  border-radius: 8px;
  padding: 5px 10px;
  cursor: pointer;
  user-select: all;
}

.new-chat-btn {
  border: 1px solid var(--bubble-border);
  background: #fff;
  color: var(--accent);
  border-radius: 10px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s;
}

.new-chat-btn:hover {
  background: var(--bg-strong);
}

/* ---------- 消息区 ---------- */
.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px 18px;
}

.message-list {
  max-width: 860px;
  margin: 0 auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 80px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 56px;
  margin-bottom: 14px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.empty-text {
  font-size: 14px;
  line-height: 1.7;
  max-width: 420px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 18px;
  animation: fade-in-up 0.25s ease;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  font-size: 18px;
  flex-shrink: 0;
}

.avatar.assistant {
  background: var(--accent-grad);
}

.avatar.user {
  background: #e5e7eb;
}

.bubble {
  max-width: min(72%, 640px);
  padding: 10px 14px;
  border-radius: 4px 16px 16px 16px;
  background: #fff;
  border: 1px solid var(--bubble-border);
  font-size: 14.5px;
  line-height: 1.7;
  word-break: break-word;
}

.message-row.user .bubble {
  background: var(--accent-grad);
  color: #fff;
  border: none;
  border-radius: 16px 4px 16px 16px;
  white-space: pre-wrap;
}

/* AI 回复的 Markdown 排版 */
.bubble-content :deep(p) {
  margin: 0 0 8px;
}

.bubble-content :deep(p:last-child),
.bubble-content :deep(ul:last-child),
.bubble-content :deep(ol:last-child),
.bubble-content :deep(pre:last-child) {
  margin-bottom: 0;
}

.bubble-content :deep(h3),
.bubble-content :deep(h4),
.bubble-content :deep(h5),
.bubble-content :deep(h6) {
  margin: 10px 0 6px;
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.bubble-content :deep(h3:first-child),
.bubble-content :deep(h4:first-child) {
  margin-top: 2px;
}

.bubble-content :deep(ul),
.bubble-content :deep(ol) {
  margin: 0 0 8px;
  padding-left: 20px;
}

.bubble-content :deep(li) {
  margin: 3px 0;
}

.bubble-content :deep(code) {
  padding: 1px 5px;
  border-radius: 4px;
  background: var(--bg-strong);
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
}

.bubble-content :deep(pre) {
  margin: 0 0 8px;
  padding: 10px 12px;
  border-radius: 8px;
  background: #1e293b;
  overflow-x: auto;
}

.bubble-content :deep(pre code) {
  padding: 0;
  background: transparent;
  color: #e2e8f0;
}

.bubble-content :deep(a) {
  color: var(--accent);
}

.bubble-content :deep(strong) {
  font-weight: 600;
}

.bubble.error {
  border-color: #fca5a5;
  background: #fef2f2;
  color: #dc2626;
}

/* 流式输出光标 */
.cursor {
  display: inline-block;
  margin-left: 2px;
  color: var(--accent);
  font-weight: bold;
  animation: blink 1s step-start infinite;
}

/* 等待首字时的打字动画 */
.typing-dots {
  display: inline-flex;
  gap: 4px;
  padding: 5px 0;
}

.typing-dots i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  animation: bounce 1.2s infinite;
}

.typing-dots i:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots i:nth-child(3) {
  animation-delay: 0.4s;
}

/* ---------- 输入区 ---------- */
.input-area {
  padding: 12px 18px 14px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.input-box {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  max-width: 860px;
  margin: 0 auto;
  padding: 8px 8px 8px 14px;
  background: var(--bg);
  border: 1.5px solid var(--bubble-border);
  border-radius: 14px;
  transition: border-color 0.2s;
}

.input-box:focus-within {
  border-color: var(--accent);
}

.input-box textarea {
  flex: 1;
  border: none;
  outline: none;
  resize: none;
  background: transparent;
  font-family: inherit;
  font-size: 14.5px;
  line-height: 1.5;
  max-height: 120px;
  color: #1f2937;
}

.input-box textarea::placeholder {
  color: #b0b6c0;
}

.send-btn {
  border: none;
  background: var(--accent-grad);
  color: #fff;
  padding: 8px 20px;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.15s, transform 0.1s;
}

.send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.send-btn:active:not(:disabled) {
  transform: scale(0.96);
}

.send-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.stop-btn {
  border: 1px solid var(--accent);
  background: #fff;
  color: var(--accent);
  padding: 7px 16px;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
}

.stop-btn:hover {
  background: var(--bg-strong);
}

.input-hint {
  margin-top: 8px;
  text-align: center;
  font-size: 12px;
  color: #b0b6c0;
  user-select: none;
}

/* ---------- 动画 ---------- */
@keyframes blink {
  50% {
    opacity: 0;
  }
}

@keyframes bounce {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }
  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

/* ---------- 移动端适配 ---------- */
@media (max-width: 640px) {
  .chat-header {
    gap: 10px;
    padding: 0 12px;
  }

  .app-subtitle,
  .chat-id-badge {
    display: none;
  }

  .message-area {
    padding: 16px 12px;
  }

  .bubble {
    max-width: 82%;
  }

  .input-area {
    padding: 10px 12px 12px;
  }
}
</style>
